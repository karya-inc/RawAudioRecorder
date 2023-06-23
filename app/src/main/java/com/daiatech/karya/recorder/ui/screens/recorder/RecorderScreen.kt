package com.daiatech.karya.recorder.ui.screens.recorder

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daiatech.karya.recorder.ui.theme.AudioRecorderTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun RecorderScreen(
    viewModel: RecorderViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    RecorderScreen(
        uiState = uiState,
        start = viewModel::startRecording,
        stop = viewModel::stopRecording,
        pause = viewModel::pauseRecording,
        resume = viewModel::resumeRecording
    ) {

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecorderScreen(
    uiState: UiState,
    start: () -> Unit,
    stop: () -> Unit,
    pause: () -> Unit,
    resume: () -> Unit,
    navigateToRecordingsList: () -> Unit
) {

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val density = LocalDensity.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = {

            }
            )
        }
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(visible = uiState.recordingFileName != null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Saving recording as",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp))
                        uiState.recordingFileName?.let {
                            Text(text = it, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                RecordingAnim(
                    currentAmp = uiState.maxAmplitude.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    barColor = MaterialTheme.colorScheme.primary,
                    barGap = 2.dp,
                    noOfBars = 50
                )
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                val duration = with(uiState) {
                    val secs = progress / 1000 // millis to s
                    val mins = secs / 60
                    val remSecs = secs % 60
                    val m = "%02d".format(mins)
                    val s = "%02d".format(remSecs)
                    "$m : $s"
                }

                Text(
                    text = duration,
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                )
                Row(
                    Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    TextButton(
                        onClick = { if (uiState.isRecording) pause() else if (uiState.isPaused) resume() },
                        modifier = Modifier.weight(1f),
                        enabled = uiState.isRecording || uiState.isPaused
                    ) {
                        if (uiState.isRecording) {
                            Text(text = "Pause")
                        } else if (uiState.isPaused) {
                            Text(text = "Resume")
                        } else {
                            val offset = with(density) { 16.dp.toPx() }
                            val arrowOffset by infiniteTransition.animateFloat(
                                initialValue = -offset,
                                targetValue = offset,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1000),
                                    repeatMode = RepeatMode.Reverse
                                ), label = ""
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.graphicsLayer {
                                    translationX = arrowOffset
                                }
                            )
                        }
                    }


                    Button(
                        onClick = {
                            when (uiState.state) {
                                UiState.State.INITIAL -> start()
                                UiState.State.RECORDING, UiState.State.PAUSED -> stop()
                            }
                        },
                        contentPadding = PaddingValues(32.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        when (uiState.state) {
                            UiState.State.INITIAL -> Text(text = "Start")
                            UiState.State.RECORDING, UiState.State.PAUSED ->
                                Text(text = "Stop")

                        }
                    }

                    TextButton(
                        onClick = navigateToRecordingsList,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Recordings")
                    }
                }
            }
        }

    }


}


@Preview
@Composable
fun RecorderScreenPrev() {
    AudioRecorderTheme {
        Surface {
            var uiState by remember { mutableStateOf(UiState.EMPTY.copy(recordingFileName = "AUD_99991.wav")) }
            RecorderScreen(
                uiState = uiState,
                start = { /*TODO*/ },
                stop = { /*TODO*/ },
                pause = { /*TODO*/ },
                resume = { /*TODO*/ }) {
            }
        }
    }
}


@Preview
@Composable
fun RecorderScreenPrev1() {
    AudioRecorderTheme(darkTheme = true) {
        Surface {
            var uiState by remember { mutableStateOf(UiState.EMPTY.copy(recordingFileName = "AUD_99991.wav")) }
            RecorderScreen(
                uiState = uiState,
                start = { /*TODO*/ },
                stop = { /*TODO*/ },
                pause = { /*TODO*/ },
                resume = { /*TODO*/ }) {
            }
        }
    }
}