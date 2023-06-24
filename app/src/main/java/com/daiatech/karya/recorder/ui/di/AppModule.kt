package com.daiatech.karya.recorder.ui.di

import com.daiatech.karya.recorder.ui.screens.list.RecordingsListVM
import com.daiatech.karya.recorder.ui.screens.recorder.RecorderViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

val appModule = module {
    single<String>(named("recordingsDir")) {
        val file = File(androidContext().filesDir.path, "recordings")
        if (!file.exists()) file.mkdirs()
        file.path
    }

    viewModel { RecorderViewModel(get(named("recordingsDir"))) }
    viewModel { RecordingsListVM(get(named("recordingsDir"))) }
}