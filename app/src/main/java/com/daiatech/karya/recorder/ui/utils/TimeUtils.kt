package com.daiatech.karya.recorder.ui.utils

object TimeUtils {
    fun secondsToTimeString(seconds: Long): String {
        val mins = seconds / 60
        val remSecs = seconds % 60
        val m = "%02d".format(mins)
        val s = "%02d".format(remSecs)
        return "$m : $s"
    }
}