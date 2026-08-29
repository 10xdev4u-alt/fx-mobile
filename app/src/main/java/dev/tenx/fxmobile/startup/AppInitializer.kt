package dev.tenx.fxmobile.startup

import android.content.Context
import androidx.startup.Initializer
import dev.tenx.fxmobile.data.local.db.FxDatabase
import dev.tenx.fxmobile.data.remote.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AppInitializer : Initializer<Unit> {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun create(context: Context) {
        scope.launch {
            // Pre-warm database
            FxDatabase.getInstance(context)
            // Pre-load preferences
            PreferencesManager(context).apply {
                getDarkMode()
                getModel()
            }
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
