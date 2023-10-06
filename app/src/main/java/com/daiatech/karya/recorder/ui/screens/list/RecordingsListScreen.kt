package com.daiatech.karya.recorder.ui.screens.list

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.daiatech.karya.recorder.models.Recording
import com.daiatech.karya.recorder.ui.theme.AudioRecorderTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun RecordingsListScreen(
    viewModel: RecordingsListVM,
    navigateUp: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    RecordingsListScreen(uiState = uiState, navigateUp = navigateUp)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordingsListScreen(
    uiState: UiState,
    navigateUp: () -> Unit,
) {

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val context = LocalContext.current
    var currentPlaying by remember { mutableStateOf<Recording?>(null) }
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }

    // Audio Player States
    var isPlaying by remember { mutableStateOf(false) }
    var progressMs by remember { mutableLongStateOf(0) }
    var duration by remember { mutableLongStateOf(0) }

    // Coroutine scope
    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        val listener = object : Player.Listener {
            private var timeoutJob: Job? = null
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    ExoPlayer.STATE_ENDED, ExoPlayer.STATE_IDLE -> {
                        timeoutJob?.cancel()
                        progressMs = 0
                        isPlaying = false
                        duration = 0
                        currentPlaying = null
                    }

                    ExoPlayer.STATE_READY -> {
                        duration = exoPlayer.duration
                    }

                    else -> {}
                }
            }

            override fun onIsPlayingChanged(playing: Boolean) {
                super.onIsPlayingChanged(playing)
                isPlaying = playing
                timeoutJob?.cancel()
                if (playing) {
                    timeoutJob = scope.launch(Dispatchers.Main) {
                        while (isActive) {
                            delay(100)
                            progressMs += 100
                        }
                    }
                }
            }
        }

        exoPlayer.addListener(listener)

        // Cleanup when component is destroyed
        onDispose {
            exoPlayer.release()
        }
    }

    val onNavigateUp = {
        if (exoPlayer.isPlaying) exoPlayer.stop()
        navigateUp()
    }

    BackHandler(true, onNavigateUp)

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(text = "Recordings") },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "navigate up"
                        )
                    }
                }
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->

        Column(
            Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            LazyColumn(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(uiState.recordings) {
                    RecordingsListItem(
                        recording = it,
                        onClick = {
                            if (exoPlayer.isPlaying) exoPlayer.stop()
                            progressMs = 0
                            isPlaying = false
                            duration = 0
                            val mediaItem = MediaItem.fromUri(it.path)
                            exoPlayer.setMediaItem(mediaItem)
                            exoPlayer.prepare()
                            exoPlayer.playWhenReady = true
                            currentPlaying = it
                        }
                    )
                }
            }

            AnimatedVisibility(visible = currentPlaying != null) {
                currentPlaying?.let {
                    AudioPlayerUi(
                        title = currentPlaying!!.name,
                        isPlaying = isPlaying,
                        currentPositionMs = progressMs,
                        durationMS = duration,
                        play = { exoPlayer.play() },
                        pause = { exoPlayer.pause() }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun RecordingsListPrev1() {
    AudioRecorderTheme(darkTheme = true) {
        val r = Recording("recording.wav", 32000, 28.9f, "path", listOf())
        val uiState = UiState(List(20) { r })
        RecordingsListScreen(uiState = uiState) {}
    }
}


@Preview
@Composable
fun RecordingsListPrev() {
    AudioRecorderTheme {
        val r = Recording("recording.wav", 32000, 28.9f, "path", listOf())
        val uiState = UiState(List(20) { r })
        RecordingsListScreen(uiState = uiState) {}
    }
}