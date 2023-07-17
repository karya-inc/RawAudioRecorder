package com.daiatech.karya.recorder.models

data class Recording(
    val name: String,
    val durationMs: Long,
    val size: Float,
    val path: String,
    val amplitudes: List<Int>
)
