package com.dxn.audiorecorder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dxn.audiorecorder.recorder.RecorderState
import com.dxn.audiorecorder.ui.theme.AudioRecorderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AudioRecorderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val viewModel: MainViewModel by viewModels()
                        LaunchedEffect(null) {
                            viewModel.createAudioRecorder(filesDir.path + "/my_recording.wav")
                        }

                        val state by viewModel.recorderState.collectAsState()
                        val amplitudes by viewModel.amplitudes.collectAsState()
                        val progress by viewModel.progress.collectAsState()

                        AudioRecorderUi(
                            state = state,
                            progress = progress,
                            amplitudes = amplitudes,
                            onStart = { viewModel.startRecording() },
                            onStop = { viewModel.stopRecording() }
                        )

                        if(state == RecorderState.STOPPED) {
                            Button(onClick = { viewModel.createAudioRecorder(filesDir.path + "/my_recording.wav") }) {
                                Text(text = "ReRecord")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AudioRecorderTheme {
        Greeting("Android")
    }
}