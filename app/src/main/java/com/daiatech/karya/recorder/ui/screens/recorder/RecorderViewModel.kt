package com.daiatech.karya.recorder.ui.screens.recorder

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daiatech.karya.rawaudiorecorder.RawAudioRecorder
import com.daiatech.karya.rawaudiorecorder.RecorderEventListener
import com.daiatech.karya.rawaudiorecorder.RecorderState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File


class RecorderViewModel(
    private val audioDirectoryPath: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState.EMPTY)
    val uiState = _uiState.asStateFlow()

    private val listener = object : RecorderEventListener {
        override fun onAmplitudeChange(amplitude: Int) {
            _uiState.update { it.copy(maxAmplitude = amplitude) }
        }

        override fun onRecorderStateChanged(state: RecorderState) {
            when (state) {
                RecorderState.PREPARED -> {
                    _uiState.update { it.copy(state = UiState.State.INITIAL) }
                }

                RecorderState.RECORDING -> {
                    _uiState.update { it.copy(state = UiState.State.RECORDING) }
                }

                RecorderState.PAUSED -> {
                    _uiState.update { it.copy(state = UiState.State.PAUSED) }
                }

                RecorderState.STOPPED -> {
                    _uiState.update { it.copy(state = UiState.State.INITIAL) }
                }
            }
        }

        override fun onProgress(timeMS: Long) {
            Log.d("TAG", "onProgress: $timeMS")
            _uiState.update { it.copy(progress = timeMS.toInt()) }
        }

    }

    @SuppressLint("MissingPermission")
    private val recorder = RawAudioRecorder(listener, viewModelScope)

    fun startRecording() {
        val fileName = "AUD_${System.currentTimeMillis()}.wav"
        val audioFile = File(audioDirectoryPath, fileName)
        recorder.prepare(audioFile.path)
        recorder.startRecording()
    }

    fun stopRecording() {
        recorder.stopRecording()
    }

    fun pauseRecording() {
        recorder.pauseRecording()
    }

    fun resumeRecording() {
        recorder.resumeRecording()
    }
}