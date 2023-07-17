package com.daiatech.karya.recorder.ui.screens.list

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.daiatech.karya.recorder.R
import com.daiatech.karya.recorder.models.Recording
import com.daiatech.karya.recorder.utils.TimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordingsListItem(
    recording: Recording,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        ListItem(
            headlineContent = { Text(recording.name) },
            leadingContent = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_music_24),
                    contentDescription = "Localized description",
                )
            },
            supportingContent = {
                val time =
                    remember { TimeUtils.secondsToTimeString(recording.durationMs.div(1000)) }
                Text(text = time)
            },
            trailingContent = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "options"
                    )
                }
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}