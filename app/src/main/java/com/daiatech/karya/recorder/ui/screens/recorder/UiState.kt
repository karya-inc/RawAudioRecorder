package com.daiatech.karya.recorder.ui.screens.recorder

import com.daiatech.karya.rawaudiorecorder.RecorderState

data class UiState(
    val state: State,
    val recordingFileName: String?,
    val progress: Int,
    val maxAmplitude: Int
) {

    enum class State {
        INITIAL, RECORDING, PAUSED
    }

    val isRecording = state == State.RECORDING
    val isPaused = state == State.PAUSED

    companion object {
        val EMPTY = UiState(State.INITIAL, null, 0, 0)
    }
}

