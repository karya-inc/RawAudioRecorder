package com.daiatech.karya.rawaudiorecorder

enum class SampleRate(private val value: Int) {
    /** Audio sample at 8 KHz */
    _8_K(8000),

    /** Audio sample at 16 KHz */
    _16_K(16000),

    /** Audio sample at 44.1 KHz */
    _44_K(44100);

    operator fun invoke(): Int {
        return this.value
    }
}