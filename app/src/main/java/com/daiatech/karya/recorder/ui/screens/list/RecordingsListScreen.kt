package com.daiatech.karya.recorder.ui.screens.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daiatech.karya.recorder.models.Recording
import com.daiatech.karya.recorder.ui.theme.AudioRecorderTheme
import com.daiatech.karya.recorder.utils.TimeUtils
import org.koin.androidx.compose.koinViewModel

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

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(text = "Recordings") },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
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
        LazyColumn(
            Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(uiState.recordings) {
                RecordingsListItem(recording = it) {
                    // on click
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