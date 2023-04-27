package com.dxn.audiorecorder.recorder

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.audiofx.NoiseSuppressor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

class RawAudioRecorder(
    private val scope: CoroutineScope,

    /**
     * Configuration for recording audio file.
     */
    var waveConfig: WaveConfig = WaveConfig(),
) {

    private lateinit var filePath: String

    /**
     * Register a callback to be invoked in every recorded chunk of audio data
     * to get max amplitude of that chunk.
     */
    var onAmplitudeListener: ((Int) -> Unit)? = null

    /**
     * Register a callback to get elapsed recording time in seconds
     */
    var onTimeElapsed: ((Long) -> Unit)? = null

    /**
     * Register a callback to be invoked in recording state changes
     */
    var onStateChangeListener: ((RecorderState) -> Unit)? = null

    /**
     * Activates Noise Suppressor during recording if the device implements noise
     * suppression.
     */
    var noiseSuppressorActive: Boolean = false

    /**
     * The ID of the audio session this WaveRecorder belongs to.
     * The default value is -1 which means no audio session exist.
     */
    var audioSessionId: Int = -1
        private set

    private var isRecording = false
    private var isPaused = false
    private lateinit var audioRecorder: AudioRecord
    private var noiseSuppressor: NoiseSuppressor? = null
    private var timeModulus = 1

    /**
     * Prepares the audioRecorder to record and sets the output file path
     */
    fun prepare(outputFilePath: String) {
        this.filePath = outputFilePath
        onStateChangeListener?.let { it(RecorderState.PREPARED) }
    }

    /**
     * Starts audio recording asynchronously and writes recorded data chunks on storage.
     */
    @SuppressLint("MissingPermission")
    fun startRecording() {

        if (!isAudioRecorderInitialized()) {
            audioRecorder = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                waveConfig.sampleRate,
                waveConfig.channels,
                waveConfig.audioEncoding,
                AudioRecord.getMinBufferSize(
                    waveConfig.sampleRate,
                    waveConfig.channels,
                    waveConfig.audioEncoding
                )
            )
            timeModulus = bitPerSample(waveConfig.audioEncoding) * waveConfig.sampleRate / 8
            if (waveConfig.channels == AudioFormat.CHANNEL_IN_STEREO)
                timeModulus *= 2

            audioSessionId = audioRecorder.audioSessionId

            isRecording = true

            audioRecorder.startRecording()

            if (noiseSuppressorActive) {
                noiseSuppressor = NoiseSuppressor.create(audioRecorder.audioSessionId)
            }

            onStateChangeListener?.let {
                it(RecorderState.RECORDING)
            }

            scope.launch(Dispatchers.IO) {
                writeAudioDataToStorage()
            }
        }
    }

    private suspend fun writeAudioDataToStorage() = withContext(Dispatchers.IO) {
        val bufferSize = AudioRecord.getMinBufferSize(
            waveConfig.sampleRate,
            waveConfig.channels,
            waveConfig.audioEncoding
        )
        val data = ByteArray(bufferSize)
        val file = File(filePath).apply {
            // If file already exists, then delete and create a new file
            if(exists()) delete()
            createNewFile()
        }

        val outputStream = file.outputStream()
        while (isRecording) {
            val operationStatus = audioRecorder.read(data, 0, bufferSize)

            if (AudioRecord.ERROR_INVALID_OPERATION != operationStatus) {
                if (!isPaused) outputStream.write(data)

                onAmplitudeListener?.let {
                    it(calculateAmplitudeMax(data))
                }
                onTimeElapsed?.let {
                    val audioLengthInSeconds: Long = file.length() / timeModulus
                    it(audioLengthInSeconds)
                }
            }
        }

        outputStream.close()
        noiseSuppressor?.release()
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

        if (isAudioRecorderInitialized()) {
            isRecording = false
            isPaused = false
            audioRecorder.stop()
            audioRecorder.release()
            audioSessionId = -1
            WaveHeaderWriter(filePath, waveConfig).writeHeader()
            onStateChangeListener?.let {
                it(RecorderState.STOPPED)
            }
        }

    }

    private fun isAudioRecorderInitialized(): Boolean =
        this::audioRecorder.isInitialized && audioRecorder.state == AudioRecord.STATE_INITIALIZED

    fun pauseRecording() {
        isPaused = true
        onStateChangeListener?.let {
            it(RecorderState.PAUSED)
        }
    }

    fun resumeRecording() {
        isPaused = false
        onStateChangeListener?.let {
            it(RecorderState.RECORDING)
        }
    }
}