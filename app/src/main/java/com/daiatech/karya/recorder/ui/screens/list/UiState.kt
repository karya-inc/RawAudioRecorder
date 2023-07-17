package com.daiatech.karya.recorder.ui.screens.list

import com.daiatech.karya.recorder.models.Recording

data class UiState(
    val recordings: List<Recording>
) {
    companion object {
        val INITIAL = UiState(listOf())
    }
}