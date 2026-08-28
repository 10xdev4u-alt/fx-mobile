package dev.tenx.fxmobile.service

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.PowerManager
import android.os.Process
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackgroundExecutionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val activityManager: ActivityManager
        get() = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    private val powerManager: PowerManager
        get() = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    fun isAppInForeground(): Boolean {
        val runningProcesses = activityManager.runningAppProcesses ?: return false
        val myPid = Process.myPid()
        return runningProcesses.any { it.pid == myPid && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND }
    }

    fun isBackgroundRestricted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activityManager.isBackgroundRestricted
        } else {
            false
        }
    }

    fun isIgnoringBatteryOptimizations(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            powerManager.isIgnoringBatteryOptimizations(context.packageName)
        } else {
            true
        }
    }

    fun getBackgroundLimitStatus(): BackgroundLimitStatus {
        return BackgroundLimitStatus(
            isBackgroundRestricted = isBackgroundRestricted(),
            isIgnoringBatteryOptimizations = isIgnoringBatteryOptimizations(),
            isAppInForeground = isAppInForeground(),
            maxProcesses = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // Android 12+ enforces 32 phantom processes per device
                32
            } else {
                Int.MAX_VALUE
            }
        )
    }
}

data class BackgroundLimitStatus(
    val isBackgroundRestricted: Boolean,
    val isIgnoringBatteryOptimizations: Boolean,
    val isAppInForeground: Boolean,
    val maxProcesses: Int
)
