package com.daiatech.karya.recorder.ui.utils

object TimeUtils {
    fun millisecondsToTimeString(milliseconds: Long): String {
        val secs = milliseconds / 1000 // millis to s
        val mins = secs / 60
        val remSecs = secs % 60
        val m = "%02d".format(mins)
        val s = "%02d".format(remSecs)
        return "$m : $s"
    }
}