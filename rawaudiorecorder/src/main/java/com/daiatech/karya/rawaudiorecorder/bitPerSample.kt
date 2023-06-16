package com.daiatech.karya.rawaudiorecorder

import android.media.AudioFormat

internal fun bitsPerSample(audioEncoding: Int) = when (audioEncoding) {
    AudioFormat.ENCODING_PCM_8BIT -> 8
    AudioFormat.ENCODING_PCM_16BIT -> 16
    AudioFormat.ENCODING_PCM_32BIT -> 32
    else -> 16
}