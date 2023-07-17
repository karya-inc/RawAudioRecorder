package com.daiatech.karya.recorder.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daiatech.karya.recorder.models.Recording
import com.daiatech.karya.recorder.utils.getAmplitudesAndDuration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import linc.com.amplituda.Amplituda
import java.io.File

class RecordingsListVM(
    private val audioDirectoryPath: String,
    private val amplituda: Amplituda
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState.INITIAL)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val audioDir = File(audioDirectoryPath)
            val recordings = mutableListOf<Recording>()
            audioDir.listFiles()?.forEach { file ->
                if (file.isFile) {
                    val (amplitude, duration) = amplituda.getAmplitudesAndDuration(file.path)
                    val sizeInBytes = file.length()
                    val fileSizeInMegabytes: Float =
                        sizeInBytes.toFloat() / 1048576 // divide by 1024 * 1024
                    recordings.add(
                        Recording(
                            name = file.name,
                            durationMs = duration,
                            size = fileSizeInMegabytes,
                            path = file.path,
                            amplitudes = amplitude
                        )
                    )
                }
            }
            _uiState.update { it.copy(recordings = recordings) }
        }
    }
}