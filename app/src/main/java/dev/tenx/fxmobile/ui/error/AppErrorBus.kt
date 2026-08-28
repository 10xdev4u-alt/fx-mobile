package dev.tenx.fxmobile.ui.error

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf
import javax.inject.Inject
import javax.inject.Singleton

interface AppErrorBus {
    suspend fun showError(message: String)
    suspend fun showError(message: String, actionLabel: String? = null)
}

@Singleton
class SnackbarErrorBus @Inject constructor() : AppErrorBus {
    var snackbarHostState: SnackbarHostState? = null

    override suspend fun showError(message: String) {
        snackbarHostState?.showSnackbar(message)
    }

    override suspend fun showError(message: String, actionLabel: String?) {
        snackbarHostState?.showSnackbar(message, actionLabel = actionLabel)
    }
}

val LocalErrorBus = compositionLocalOf<AppErrorBus> {
    error("No AppErrorBus provided")
}
