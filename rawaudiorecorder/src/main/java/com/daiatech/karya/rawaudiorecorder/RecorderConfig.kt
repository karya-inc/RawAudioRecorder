package com.daiatech.karya.rawaudiorecorder

import android.media.AudioFormat

data class RecorderConfig(
    var sampleRate: Int = 16000,
    var channels: Int = AudioFormat.CHANNEL_IN_MONO,
    var audioEncoding: Int = AudioFormat.ENCODING_PCM_16BIT
)

