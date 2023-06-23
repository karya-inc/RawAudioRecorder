package com.daiatech.karya.recorder.ui.screens.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daiatech.karya.recorder.ui.models.Recording
import com.daiatech.karya.recorder.ui.theme.AudioRecorderTheme
import com.daiatech.karya.recorder.ui.utils.TimeUtils
import org.koin.androidx.compose.koinViewModel

@Composable
fun RecordingsListScreen(viewModel: RecordingsListVM = koinViewModel()) {

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordingsListScreen(
    uiState: UiState
) {

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            LargeTopAppBar(title = { Text(text = "Recordings") }, scrollBehavior = scrollBehavior)
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        LazyColumn(
            Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(uiState.recordings) {
                Card(
                    onClick = {

                    },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    ListItem(
                        headlineContent = { Text(it.name) },
                        leadingContent = {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Localized description",
                            )
                        },
                        supportingContent = {
                            val time =
                                remember { TimeUtils.millisecondsToTimeString(it.durationMs) }
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
        }
    }
}

@Preview
@Composable
fun RecordingsListPrev1() {
    AudioRecorderTheme(darkTheme = true) {
        val r = Recording("recording.wav", 32000, 28.9f)
        val uiState = UiState(List(20) { r })
        RecordingsListScreen(uiState = uiState)
    }
}


@Preview
@Composable
fun RecordingsListPrev() {
    AudioRecorderTheme {
        val r = Recording("recording.wav", 32000, 28.9f)
        val uiState = UiState(List(20) { r })
        RecordingsListScreen(uiState = uiState)
    }
}