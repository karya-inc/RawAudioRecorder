package com.dxn.audiorecorder

import android.annotation.SuppressLint
import android.media.AudioFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daiatech.karya.rawaudiorecorder.RawAudioRecorder
import com.daiatech.karya.rawaudiorecorder.RecorderEventListener
import com.daiatech.karya.rawaudiorecorder.RecorderState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel(), RecorderEventListener {

    private val _amplitudes = MutableStateFlow(listOf<Float>())
    val amplitudes = _amplitudes.asStateFlow()

    private val _recorderState = MutableStateFlow<RecorderState?>(null)
    val recorderState = _recorderState.asStateFlow()

    private val _progress = MutableStateFlow(0)
    val progress = _progress.asStateFlow()

    @SuppressLint("MissingPermission")
    private var recorder: RawAudioRecorder = RawAudioRecorder(this, viewModelScope)

    fun createAudioRecorder(path: String) {
        recorder.prepare(path)
    }

    fun startRecording() {
        viewModelScope.launch(Dispatchers.IO) { recorder.startRecording() }
    }

    fun stopRecording() {
        viewModelScope.launch(Dispatchers.IO) { recorder.stopRecording() }
    }

    override fun onAmplitudeChange(amplitude: Int) {
        _amplitudes.update {
            it.appendAtEnd(listOf(amplitude.toFloat()))
        }
    }

    override fun onRecorderStateChanged(state: RecorderState) {
        _recorderState.update { state }
    }

    override fun onProgress(timeMS: Long) {
        _progress.update { timeMS.toInt() }
    }
}

/**
 * Appends [list] to the end and strips out first [list.size] elements from original list
 */
fun <T> List<T>.appendAtEnd(list: List<T>): List<T> =
    (this + list).subList(list.size, size + list.size)