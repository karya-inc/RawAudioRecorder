package com.daiatech.karya.rawaudiorecorder

interface RecorderEventListener {
    fun onPrepared()

    fun onStart()

    fun onPause()

    fun onResume()

    fun onStop(durationMs: Long)

    /**
     * Publishes the recorded time in seconds to the listener
     */
    fun onProgressUpdate(maxAmplitude: Int, duration: Long)
}