package com.dxn.audiorecorder

import android.media.AudioFormat
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dxn.audiorecorder.recorder.RawAudioRecorder
import com.dxn.audiorecorder.recorder.RecorderState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainViewModel : ViewModel() {

    private val _amplitudes = MutableStateFlow(listOf<Float>())
    val amplitudes = _amplitudes.asStateFlow()

    private val _recorderState = MutableStateFlow<RecorderState?>(null)
    val recorderState = _recorderState.asStateFlow()

    private val _progress = MutableStateFlow(0)
    val progress = _progress.asStateFlow()

    private var recorder: RawAudioRecorder = RawAudioRecorder(viewModelScope).apply {
        onTimeElapsed = { progress -> _progress.update { progress.toInt() } }
        onStateChangeListener = { state -> _recorderState.update { state } }
        waveConfig.sampleRate = 44100
        waveConfig.channels = AudioFormat.CHANNEL_IN_STEREO
        waveConfig.audioEncoding = AudioFormat.ENCODING_PCM_8BIT
        onAmplitudeListener = { amp ->
            _amplitudes.update { it.appendAtEnd(listOf(amp.toFloat())) }
        }
    }

    fun createAudioRecorder(path: String) {
        recorder.prepare(path)
    }

    fun startRecording() {
        viewModelScope.launch(Dispatchers.IO) { recorder.startRecording() }
    }

    fun stopRecording() {
        viewModelScope.launch(Dispatchers.IO) { recorder.stopRecording() }
    }
}

/**
 * Appends [list] to the end and strips out first [list.size] elements from original list
 */
fun <T> List<T>.appendAtEnd(list: List<T>): List<T> =
    (this + list).subList(list.size, size + list.size)