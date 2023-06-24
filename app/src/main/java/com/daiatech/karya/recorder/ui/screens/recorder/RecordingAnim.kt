package com.daiatech.karya.recorder.ui.screens.recorder

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.daiatech.karya.recorder.ui.theme.AudioRecorderTheme

@Composable
fun RecordingAnim(
    modifier: Modifier = Modifier,
    currentAmp: Float,
    barGap: Dp = 4.dp,
    noOfBars: Int = 200,
    barColor: Color = Color.Red,
    maxAmplitude: Float = 18000f
) {
    var amplitudes by remember { mutableStateOf(List(noOfBars) { 1f }) }

    val animationSpec = remember { tween<Float>(durationMillis = 200) }
    val animateFloat by animateFloatAsState(
        targetValue = currentAmp,
        animationSpec = animationSpec,
        label = ""
    )
    amplitudes = amplitudes.takeLast(noOfBars - 1) + animateFloat
    BarGraph(
        amplitudes = amplitudes,
        modifier = modifier,
        barGap = barGap,
        barColor = barColor,
        maxAmplitude = maxAmplitude
    )
}

@Composable
private fun BarGraph(
    amplitudes: List<Float>,
    modifier: Modifier,
    barGap: Dp = 4.dp,
    barColor: Color = Color.Red,
    maxAmplitude: Float = 18000f,
) {
    val density = LocalDensity.current

    Canvas(modifier = modifier) {
        val xStep = size.width / amplitudes.size
        val w =
            (size.width - with(density) { barGap.toPx() }.times(amplitudes.size)) / amplitudes.size

        amplitudes.normalized(size.height, 0f, maxAmplitude).forEachIndexed { idx, y ->
            val x = (idx + 0.5f) * xStep
            drawLine(
                start = Offset(x, (size.height / 2) - (y / 3)),
                end = Offset(x, ((size.height / 2)) + y / 3),
                brush = SolidColor(barColor),
                strokeWidth = w,
                cap = StrokeCap.Round
            )
        }
    }
}


private fun List<Float>.normalized(max: Float, min: Float, lMax: Float): List<Float> {
    val lMin = this.min()

    // If the list min == max, then return as it is
    if (lMax == lMin) return this

    /**
     * y = mx + c
     * m = (y2-y1) / (x2 - x1)
     */
    val slope = (max - min) / (lMax - lMin)
    val yIntercept = max - (slope * lMax)

    val y: (Float) -> Float = { x ->
        slope * x + yIntercept + 1f
    }

    return map(y)
}

@Preview
@Composable
fun BarGraphPrev() {
    AudioRecorderTheme {
        BarGraph(
            amplitudes = listOf(200f, 30f, 45f, 5f, 16f),
            modifier = Modifier
                .fillMaxWidth()
                .height(460.dp)
        )
    }
}
