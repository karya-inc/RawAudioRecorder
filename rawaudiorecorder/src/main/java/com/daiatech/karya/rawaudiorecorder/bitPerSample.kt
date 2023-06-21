package com.daiatech.karya.rawaudiorecorder

import android.os.Build

internal fun bitsPerSample(audioEncoding: AudioEncoding) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        when (audioEncoding) {
            AudioEncoding.PCM_8BIT -> 8
            AudioEncoding.PCM_16BIT -> 16
            AudioEncoding.PCM_32BIT -> 32
        }
    } else {
        when (audioEncoding) {
            AudioEncoding.PCM_8BIT -> 8
            AudioEncoding.PCM_16BIT -> 16
            else -> 16
        }
    }