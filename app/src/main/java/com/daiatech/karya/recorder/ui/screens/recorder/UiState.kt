package com.daiatech.karya.recorder.ui.screens.recorder

data class UiState(
    val state: State,
    val recordingFileName: String?,
    val progress: Long,
    val maxAmplitude: Int,
    val enableStop: Boolean
) {

    enum class State {
        INITIAL, RECORDING, PAUSED
    }

    val isRecording = state == State.RECORDING
    val isPaused = state == State.PAUSED

    companion object {
        val EMPTY = UiState(State.INITIAL, null, 0, 0, true)
    }
}

