package com.daiatech.karya.rawaudiorecorder

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.audiofx.NoiseSuppressor
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
    private var timeModulus = 1


    private lateinit var filePath: String

    fun prepare(
        filePath: String,
        config: RecorderConfig = RecorderConfig(),
        suppressNoise: Boolean = false
    ) {
        if (isRecording)
            throw IllegalStateException("Cannot change filePath when still recording.")

        this.filePath = filePath
        this.recorderConfig = config
        this.noiseSuppressorActive = suppressNoise
        listener.onRecorderStateChanged(RecorderState.PREPARED)
    }

    /**
     * Starts audio recording asynchronously and writes recorded data chunks on storage.
     */
    @SuppressLint("MissingPermission")
    fun startRecording() {
        if (!isAudioRecorderInitialized()) {
            audioRecorder = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                recorderConfig.sampleRate(),
                recorderConfig.channels,
                recorderConfig.audioEncoding(),
                AudioRecord.getMinBufferSize(
                    recorderConfig.sampleRate(),
                    recorderConfig.channels,
                    recorderConfig.audioEncoding()
                )
            )
            timeModulus =
                bitsPerSample(recorderConfig.audioEncoding) * recorderConfig.sampleRate() / 8
            if (recorderConfig.channels == AudioFormat.CHANNEL_IN_STEREO)
                timeModulus *= 2

            audioSessionId = audioRecorder.audioSessionId

            isRecording = true

            audioRecorder.startRecording()

            if (noiseSuppressorActive) {
                noiseSuppressor = NoiseSuppressor.create(audioRecorder.audioSessionId)
            }

            listener.onRecorderStateChanged(RecorderState.RECORDING)

            coroutineScope.launch(Dispatchers.IO) {
                writeAudioDataToStorage()
            }
        }
    }

    private suspend fun writeAudioDataToStorage() {
        val bufferSize = AudioRecord.getMinBufferSize(
            recorderConfig.sampleRate(),
            recorderConfig.channels,
            recorderConfig.audioEncoding()
        )

        val data = ByteArray(bufferSize)
        val file = File(filePath)
        val outputStream = file.outputStream()

        val publishResultJob = CoroutineScope(Dispatchers.Main).launch {
            while (isRecording) {
                delay(100) // publish the result every 100ms
                if (!isPaused) {
                    listener.onAmplitudeChange(calculateAmplitudeMax(data))
                    val audioLengthInSeconds: Long = file.length() / timeModulus
                    listener.onProgress(audioLengthInSeconds.times(1000)) // TODO: MS
                }
            }
        }

        while (isRecording) {
            val operationStatus = audioRecorder.read(data, 0, bufferSize)

            if (AudioRecord.ERROR_INVALID_OPERATION != operationStatus) {
                if (!isPaused) outputStream.write(data)
            }
        }

        outputStream.close()
        publishResultJob.cancel()
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
            HeaderWriter(filePath, recorderConfig).writeHeader()
            listener.onRecorderStateChanged(RecorderState.STOPPED)
        }
    }

    private fun isAudioRecorderInitialized(): Boolean =
        this::audioRecorder.isInitialized && audioRecorder.state == AudioRecord.STATE_INITIALIZED

    fun pauseRecording() {
        isPaused = true
        listener.onRecorderStateChanged(RecorderState.PAUSED)
    }

    fun resumeRecording() {
        isPaused = false
        listener.onRecorderStateChanged(RecorderState.RECORDING)
    }
}