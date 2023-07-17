package com.daiatech.karya.recorder

import android.app.Application
import com.daiatech.karya.recorder.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class RecorderApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@RecorderApp)
            modules(appModule)
        }
    }
}