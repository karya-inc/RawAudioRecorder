package com.daiatech.karya.rawaudiorecorder

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.audiofx.NoiseSuppressor
import android.util.Log
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

class RawAudioRecorder
@RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
constructor(
    private val listener: RecorderEventListener,
    private val coroutineScope: CoroutineScope
) {

    /**
     * Configuration for recording audio file.
     */
    private var recorderConfig: RecorderConfig = RecorderConfig()

    /**
     * Activates Noise Suppressor during recording if the device implements noise
     * suppression.
     */
    private var noiseSuppressorActive: Boolean = false

    /**
     * The ID of the audio session this WaveRecorder belongs to.
     * The default value is -1 which means no audio session exist.
     */
    private var audioSessionId: Int = -1

    private var isRecording = false
    private var isPaused = false
    private lateinit var audioRecorder: AudioRecord
    private var noiseSuppressor: NoiseSuppressor? = null
    private var bytesPerSecond = 1
    private var bufferSizeInBytes: Int = 0


    private lateinit var filePath: String

    private var recordedFileDurationMs: Long? = null

    private var preRecordJob: Job? = null
    private var preRecordDurationMs: Int = 0
    private var postRecordDurationMs: Int = 0
    private var preRecordBufferSize: Int = 0
    private var preRecordBuffer = mutableListOf<ByteArray>()

    fun prepare(
        filePath: String,
        config: RecorderConfig = RecorderConfig(),
        suppressNoise: Boolean = false,
        // Segment to record before the user actually presses record
        preRecordDurationMs: Int = 0,
        // Segment to record after the user has clicker stop
        postRecordDurationMs: Int = 0,
        enablePreRecording: Boolean = false
    ) {
        if (isRecording)
            throw IllegalStateException("Cannot change filePath when still recording.")
        this.filePath = filePath
        this.recorderConfig = config
        this.noiseSuppressorActive = suppressNoise
        this.preRecordDurationMs = preRecordDurationMs
        this.postRecordDurationMs = postRecordDurationMs
        this.bufferSizeInBytes = AudioRecord.getMinBufferSize(
            recorderConfig.sampleRate(),
            recorderConfig.channels,
            recorderConfig.audioEncoding()
        )
        val outputFile = File(this.filePath)
        if(!outputFile.exists()) outputFile.createNewFile()
        listener.onPrepared()
        Log.d("TAG", "Prepared RawAudioRecorder")

        if (enablePreRecording) {
            preRecordJob?.cancel()
            startPreRecording()
        }
    }

    private fun startPreRecording() {
        if (!isAudioRecorderInitialized()) {

            initializeAudioRecord()

            preRecordJob = coroutineScope.launch { writeAudioDataToPreRecordBuffer() }
            Log.d("TAG", "Started Pre-Recording")
        }
    }

    /**
     * Starts audio recording asynchronously and writes recorded data chunks on storage.
     */
    fun startRecording() {
        if (!isAudioRecorderInitialized()) {
            initializeAudioRecord()
        }

        if (!isRecording) {
            isRecording = true

            listener.onStart()

            coroutineScope.launch {
                preRecordJob?.cancelAndJoin()
                writeAudioDataToStorage()
            }
            Log.d("TAG", "Started Recording")
        }
    }


    @SuppressLint("MissingPermission")
    private fun initializeAudioRecord() {
        Log.d("TAG", "Initialized AudioRecord")
        audioRecorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            recorderConfig.sampleRate(),
            recorderConfig.channels,
            recorderConfig.audioEncoding(),
            bufferSizeInBytes
        )

        /**
         * Units: bitPerSample = bits/sample
         *        sampleRate = samples/second (Hz)
         *     => bytesPerSecond = bits/sample * samples/second
         *                       = bytes/second
         *
         * [bytesPerSecond] = bitPerSample * sampleRate
         */
        bytesPerSecond =
            bitsPerSample(recorderConfig.audioEncoding) * recorderConfig.sampleRate() / 8
        if (recorderConfig.channels == AudioFormat.CHANNEL_IN_STEREO)
            bytesPerSecond *= 2

        /** buffer size (no of bytes) = (total pre-record seconds) * (bytes for one second) */
        preRecordBufferSize = ((preRecordDurationMs / 1000) * bytesPerSecond)

        audioSessionId = audioRecorder.audioSessionId

        audioRecorder.startRecording()

        if (noiseSuppressorActive) {
            noiseSuppressor = NoiseSuppressor.create(audioRecorder.audioSessionId)
        }
    }

    private suspend fun writeAudioDataToPreRecordBuffer() = withContext(Dispatchers.IO) {
        Log.d("TAG", "Writing data to pre-record buffer")
        // Initialize pre-record buffer with a byte array of size [preRecordBufferSize] filling it with empty ByteArray
        preRecordBuffer.clear()

        val data = ByteArray(bufferSizeInBytes)

        while (isActive) {
            val operationStatus = audioRecorder.read(data, 0, bufferSizeInBytes)
            if (AudioRecord.ERROR_INVALID_OPERATION != operationStatus) {
                preRecordBuffer.add(data.clone())
                // check if overflow than [preRecordBufferSize], take last [preRecordBufferSize] in such case
                if (preRecordBuffer.size > preRecordBufferSize) {
                    preRecordBuffer = preRecordBuffer.takeLast(preRecordBufferSize).toMutableList()
                }
            }
        }
    }

    private suspend fun writeAudioDataToStorage() = withContext(Dispatchers.IO) {
        val data = ByteArray(bufferSizeInBytes)
        val file = File(filePath)
        val outputStream = file.outputStream()

        Log.d("TAG", "Saving pre-record to disk")
        // add the pre-record buffer
        preRecordBuffer.forEach { bufferByte ->
            outputStream.write(bufferByte)
        }


        val publishResultJob = CoroutineScope(Dispatchers.Main).launch {
            while (isRecording) {
                delay(100) // publish the progress every 100ms
                if (!isPaused) {
                    val maxAmplitude = calculateAmplitudeMax(data)
                    // file.length() = total number of bytes in the file,
                    // no need to deduct the header size because it hasn't been added yet
                    val progress: Long = file.length() / (bytesPerSecond)
                    listener.onProgressUpdate(maxAmplitude, progress)
                }
            }
        }

        Log.d("TAG", "Saving recording to disk")
        while (isRecording) {
            val operationStatus = audioRecorder.read(data, 0, bufferSizeInBytes)

            if (AudioRecord.ERROR_INVALID_OPERATION != operationStatus) {
                if (!isPaused) outputStream.write(data)
            }
        }

        // file.length() = total number of bytes in the file,
        // no need to deduct the header size because it hasn't been added yet
        recordedFileDurationMs =
            (file.length().toDouble() / (bytesPerSecond) * 1000).toLong()

        outputStream.close()
        publishResultJob.cancel()
        noiseSuppressor?.release()

        // Once recording is stopped, write the header and notify the listener
        HeaderWriter(filePath, recorderConfig).writeHeader()
        listener.onStop(durationMs = recordedFileDurationMs!!)
    }

    private fun calculateAmplitudeMax(data: ByteArray): Int {
        val shortData = ShortArray(data.size / 2)
        ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()
            .get(shortData)

        return shortData.maxOrNull()?.toInt() ?: 0
    }

    /**
     * Stops audio recorder and release resources then writes recorded file headers.
     */
    fun stopRecording() {
        coroutineScope.launch {
            delay(postRecordDurationMs.toLong())
            if (isAudioRecorderInitialized()) {
                isRecording = false
                isPaused = false
                audioRecorder.stop()
                audioRecorder.release()
                audioSessionId = -1
            }
        }
    }

    private fun isAudioRecorderInitialized(): Boolean =
        this::audioRecorder.isInitialized && audioRecorder.state == AudioRecord.STATE_INITIALIZED

    fun pauseRecording() {
        isPaused = true
        listener.onPause()
    }

    fun resumeRecording() {
        isPaused = false
        listener.onResume()
    }
}