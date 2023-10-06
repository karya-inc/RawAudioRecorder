package com.daiatech.karya.recorder.ui.screens.list

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daiatech.karya.recorder.R
import com.daiatech.karya.recorder.ui.theme.AudioRecorderTheme

@Composable
fun AudioPlayerUi(
    modifier: Modifier = Modifier,
    title: String,
    isPlaying: Boolean,
    currentPositionMs: Long,
    durationMS: Long,
    play: () -> Unit,
    pause: () -> Unit
) {
    val progress = (currentPositionMs.toFloat().div(durationMS.toFloat()))
    val buttonIcon = if(isPlaying) R.drawable.ic_pause_24 else R.drawable.ic_play_24
    ListItem(
        modifier = modifier.clip(RoundedCornerShape(16.dp, 16.dp)),
        headlineContent = { Text(text = title) },
        leadingContent = {
            Icon(
                painter = painterResource(id = R.drawable.ic_music_24),
                contentDescription = null,
            )
        },
        trailingContent = {
            IconButton(onClick = { if (isPlaying) pause() else play() }) {
                Icon(
                    painter = painterResource(buttonIcon),
                    contentDescription = "pause play button"
                )
            }
        },
        supportingContent = {
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(progress = progress)
        },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    )
}

@Preview
@Composable
fun AudioPlayerUiPreview() {
    AudioRecorderTheme {
        Row(Modifier.fillMaxWidth()) {
            AudioPlayerUi(
                modifier = Modifier.fillMaxWidth(),
                title = "Audio 0001",
                isPlaying = false,
                currentPositionMs = 10,
                durationMS = 20,
                play = {},
                pause = {}
            )
        }
    }
}

@Preview
@Composable
fun AudioPlayerUiPreview1() {
    AudioRecorderTheme {
        Row(Modifier.fillMaxWidth()) {
            AudioPlayerUi(
                modifier = Modifier.fillMaxWidth(),
                title = "Audio 0001",
                isPlaying = true,
                currentPositionMs = 10,
                durationMS = 20,
                play = {},
                pause = {}
            )
        }
    }
}