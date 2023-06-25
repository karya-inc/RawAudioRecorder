package com.daiatech.karya.recorder.ui.screens.recorder

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daiatech.karya.rawaudiorecorder.RawAudioRecorder
import com.daiatech.karya.rawaudiorecorder.RecorderEventListener
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
        override fun onPrepared() {
            _uiState.update { it.copy(state = UiState.State.INITIAL) }
        }

        override fun onStart() {
            _uiState.update { it.copy(state = UiState.State.RECORDING) }
        }

        override fun onPause() {
            _uiState.update { it.copy(state = UiState.State.PAUSED) }
        }

        override fun onResume() {
            _uiState.update { it.copy(state = UiState.State.RECORDING) }
        }

        override fun onStop(durationMs: Long) {
            _uiState.update { UiState.EMPTY }
            Log.d("TAG", "onStop: $durationMs")
        }

        override fun onProgressUpdate(maxAmplitude: Int, duration: Long) {
            _uiState.update { it.copy(maxAmplitude = maxAmplitude, progress = duration) }
        }
    }

    @SuppressLint("MissingPermission")
    private val recorder = RawAudioRecorder(listener, viewModelScope)

    fun startRecording() {
        val fileName = "AUD_${System.currentTimeMillis()}.wav"
        val audioFile = File(audioDirectoryPath, fileName)
        recorder.prepare(audioFile.path)
        recorder.startRecording()
        _uiState.update { it.copy(recordingFileName = fileName) }
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