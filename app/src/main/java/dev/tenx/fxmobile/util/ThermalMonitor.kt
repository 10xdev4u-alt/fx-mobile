package dev.tenx.fxmobile.util

import android.content.Context
import android.os.PowerManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThermalMonitor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val powerManager: PowerManager
        get() = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    fun getThermalStatus(): ThermalStatus {
        val status = powerManager.currentThermalStatus
        return when (status) {
            PowerManager.THERMAL_STATUS_NONE -> ThermalStatus.NONE
            PowerManager.THERMAL_STATUS_LIGHT -> ThermalStatus.LIGHT
            PowerManager.THERMAL_STATUS_MODERATE -> ThermalStatus.MODERATE
            PowerManager.THERMAL_STATUS_SEVERE -> ThermalStatus.SEVERE
            PowerManager.THERMAL_STATUS_CRITICAL -> ThermalStatus.CRITICAL
            PowerManager.THERMAL_STATUS_EMERGENCY -> ThermalStatus.EMERGENCY
            PowerManager.THERMAL_STATUS_SHUTDOWN -> ThermalStatus.SHUTDOWN
            else -> ThermalStatus.NONE
        }
    }

    fun isThrottling(): Boolean {
        return getThermalStatus().ordinal >= ThermalStatus.MODERATE.ordinal
    }

    fun shouldReduceWorkload(): Boolean {
        return getThermalStatus().ordinal >= ThermalStatus.SEVERE.ordinal
    }

    fun thermalStatusFlow(): Flow<ThermalStatus> = flow {
        while (true) {
            emit(getThermalStatus())
            delay(5000) // Check every 5 seconds
        }
    }
}

enum class ThermalStatus {
    NONE,
    LIGHT,
    MODERATE,
    SEVERE,
    CRITICAL,
    EMERGENCY,
    SHUTDOWN
}
