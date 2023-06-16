package com.dxn.audiorecorder

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daiatech.karya.rawaudiorecorder.RecorderState
import com.dxn.audiorecorder.ui.theme.AudioRecorderTheme

@Composable
fun AudioRecorderUi(
    state: RecorderState?,
    progress: Int,
    amplitudes: List<Float>,
    onStart: () -> Unit,
    onStop: () -> Unit
) {

    val mins = progress / 60
    val secs = progress % 60

    Row {
        when (state) {
            RecorderState.RECORDING -> {
                IconButton(onClick = onStop) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                }
                Waveform(amplitudes = amplitudes, modifier = Modifier.weight(1f))
            }

            RecorderState.PAUSED -> {}
            RecorderState.STOPPED -> {}
            RecorderState.PREPARED -> {
                IconButton(onClick = onStart) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                }
            }
            null->{}
        }
        Text(text = "$mins : $secs", modifier = Modifier.padding(4.dp))
    }
}

@Composable
fun Waveform(modifier: Modifier = Modifier, amplitudes: List<Float>, noOfBars: Int = 50) {
    Canvas(
        modifier = modifier
            .height(48.dp)
            .border(1.dp, Color.Green)
    ) {
        val maxV = amplitudes.maxOrNull() ?: 1f

        val width = this.size.width
        val height = this.size.height.times(0.9).toFloat()

        val slope = height.toInt() / maxV

        val xSteps = width / noOfBars

        val amps = getAmplitudesPadded(noOfBars, amplitudes).mapIndexed { index, i ->
            val x = index * xSteps
            val y = height - slope * i
            Offset(x, y)
        }

        drawPath(
            path = Path().apply { drawQuadraticBezier(amps) },
            color = Color.Red,
            style = Stroke(
                width = 2f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

fun Path.drawQuadraticBezier(points: List<Offset>) {
    if (points.size <= 1) return // need atLeast two points to draw path
    moveTo(points[0].x, points[0].y) // move the cursor from (0,0) to x0, y0
    var prevPoint = points[1]
    points.forEachIndexed { idx, point ->
        if (idx == 0) return@forEachIndexed
        // set middle as control point
        val controlPoint = Offset((prevPoint.x + point.x) / 2, (prevPoint.y + point.y) / 2)
        // draw a bezier curve from `prevPoint` to `point` through `controlPoint`
        quadraticBezierTo(controlPoint.x, controlPoint.y, point.x, point.y)
        prevPoint = point
    }
}


/**
 *
 */
fun getAmplitudesPadded(size: Int, amplitudes: List<Float>): List<Float> {
    return if (size > amplitudes.size) {
        List(size - amplitudes.size) { 0f } + amplitudes
    } else {
        amplitudes.subList(amplitudes.size - size, amplitudes.size)
    }
}

@Preview
@Composable
fun WaveformPreview() {
    AudioRecorderTheme {
        val amplitudes = listOf(1f, 2f, 4f, 5f, 1f, 3f, 5f, 12f, 8f, 9f, 3f)
        Waveform(amplitudes = amplitudes, modifier = Modifier.fillMaxWidth())
    }
}