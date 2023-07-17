package com.daiatech.karya.recorder.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.daiatech.karya.recorder.ui.screens.list.RecordingsListScreen
import com.daiatech.karya.recorder.ui.screens.list.RecordingsListVM
import com.daiatech.karya.recorder.ui.screens.recorder.RecorderScreen
import com.daiatech.karya.recorder.ui.screens.recorder.RecorderViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun RecorderAppNav() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "recorder_screen") {
        composable("recorder_screen") {
            val viewModel: RecorderViewModel = koinViewModel()
            RecorderScreen(
                viewModel = viewModel,
                navigateToRecordingsList = {
                    navController.navigate("recordings_list_screen")
                }
            )
        }

        composable("recordings_list_screen") {
            val viewModel: RecordingsListVM = koinViewModel()
            RecordingsListScreen(viewModel = viewModel, navigateUp = { navController.navigateUp() })
        }
    }
}