package dev.tenx.fxmobile.analytics

import android.content.Context
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.tenx.fxmobile.util.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val logger: Logger
) {
    private var isEnabled = false
    private val eventQueue = mutableListOf<AnalyticsEvent>()

    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
        if (!enabled) {
            eventQueue.clear()
        }
    }

    fun track(event: String, properties: Map<String, Any> = emptyMap()) {
        if (!isEnabled) return

        val analyticsEvent = AnalyticsEvent(
            name = event,
            properties = properties,
            timestamp = System.currentTimeMillis()
        )
        eventQueue.add(analyticsEvent)
        logger.debug("Analytics", "Event: $event, properties: $properties")
    }

    fun trackScreen(screenName: String) {
        track("screen_view", mapOf("screen_name" to screenName))
    }

    fun trackButtonClick(buttonName: String, screenName: String = "") {
        track("button_click", mapOf("button_name" to buttonName, "screen_name" to screenName))
    }

    fun trackError(error: String, screenName: String = "") {
        track("error", mapOf("error" to error, "screen_name" to screenName))
    }

    fun trackAgentInteraction(promptLength: Int, responseLength: Int) {
        track("agent_interaction", mapOf(
            "prompt_length" to promptLength,
            "response_length" to responseLength
        ))
    }

    @VisibleForTesting
    fun getEventQueue(): List<AnalyticsEvent> = eventQueue.toList()

    @VisibleForTesting
    fun clearQueue() = eventQueue.clear()
}

data class AnalyticsEvent(
    val name: String,
    val properties: Map<String, Any>,
    val timestamp: Long
)
