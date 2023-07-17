package com.daiatech.karya.recorder.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import linc.com.amplituda.Amplituda
import linc.com.amplituda.AmplitudaResult
import linc.com.amplituda.Cache

suspend fun Amplituda.getAmplitudesAndDuration(
    path: String,
    cachePolicy: Int = Cache.REUSE
): Pair<List<Int>, Long> =
    withContext(Dispatchers.IO) {
        var amplitudes = listOf<Int>()
        var duration = 0L
        processAudio(path, Cache.withParams(cachePolicy))
            .get(
                { result ->
                    amplitudes = result.amplitudesAsList()
                    duration = result.getAudioDuration(AmplitudaResult.DurationUnit.MILLIS)
                },
                { error ->
                    Log.e("Amplituda:: ", "getAmplitudesAndDuration: $path", error)
                }
            )
        return@withContext Pair(amplitudes, duration)
    }