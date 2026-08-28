package dev.tenx.fxmobile.util

import dev.tenx.fxmobile.data.remote.KiloError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorHandler @Inject constructor(
    private val logger: Logger
) {
    fun handle(error: Throwable, tag: String = "fx"): String {
        logger.error(tag, error.message ?: "Unknown error", error)
        return when (error) {
            is KiloError.Unauthorized -> "Invalid API key. Please check your settings."
            is KiloError.RateLimited -> "Rate limit exceeded. Please try again later."
            is KiloError.Network -> "Network error. Check your connection."
            is KiloError.Server -> "Server error. Please try again later."
            else -> error.message ?: "An unexpected error occurred."
        }
    }
}
