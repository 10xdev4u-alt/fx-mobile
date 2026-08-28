package dev.tenx.fxmobile

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FxApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
