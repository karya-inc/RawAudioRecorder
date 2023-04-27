package com.dxn.audiorecorder

import org.junit.Test


class TestAmplitudePadding {

    class AmplitudeHolder(private val size: Int) {
        var amplitudes = List(size) { 0 }
            private set

        fun append(amps: List<Int>) {
            amplitudes = (amplitudes + amps).subList(amps.size, amps.size + size)
        }
    }



    @Test
    fun checkAmplitudePadding() {
        val amps = AmplitudeHolder(10)
        println(amps.amplitudes.joinToString(","))
        amps.append(listOf(3, 4, 5, 5, 6, 2))
        println(amps.amplitudes.joinToString(","))

    }
}