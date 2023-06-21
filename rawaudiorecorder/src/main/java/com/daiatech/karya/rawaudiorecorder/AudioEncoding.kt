package com.daiatech.karya.rawaudiorecorder

import android.media.AudioFormat
import android.os.Build
import androidx.annotation.RequiresApi

enum class AudioEncoding(val value: Int) {

    PCM_8BIT(AudioFormat.ENCODING_PCM_8BIT),
    PCM_16BIT(AudioFormat.ENCODING_PCM_16BIT),

    @RequiresApi(Build.VERSION_CODES.S)
    PCM_32BIT(AudioFormat.ENCODING_PCM_32BIT);

    operator fun invoke(): Int {
        return this.value
    }
}