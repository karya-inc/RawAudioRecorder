package com.daiatech.karya.rawaudiorecorder

import android.media.AudioFormat

data class RecorderConfig(
    var sampleRate: SampleRate = SampleRate._16_K,
    var channels: Int = AudioFormat.CHANNEL_IN_MONO,
    var audioEncoding: AudioEncoding = AudioEncoding.PCM_16BIT
)

