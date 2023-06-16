package com.daiatech.karya.rawaudiorecorder

interface RecorderEventListener {
    
    /**
     * A callback to be invoked in every recorded chunk of audio data
     * to get max amplitude of that chunk.
     */
    fun onAmplitudeChange(amplitude: Int)

    /**
     * Whenever state of the recorder changes, publishes the updated state to listener
     */
    fun onRecorderStateChanged(state: RecorderState)

    /**
     * Publishes the recorded time in milliseconds to the listener
     */
    fun onProgress(timeMS: Long)
}