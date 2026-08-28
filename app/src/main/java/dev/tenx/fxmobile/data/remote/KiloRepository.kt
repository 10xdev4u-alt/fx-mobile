package dev.tenx.fxmobile.data.remote

import dev.tenx.fxmobile.data.remote.dto.ChatMessageDto
import dev.tenx.fxmobile.data.remote.dto.ChatRequestDto
import dev.tenx.fxmobile.data.remote.dto.ChatResponseDto
import dev.tenx.fxmobile.domain.model.AgentMessage
import dev.tenx.fxmobile.domain.model.InferenceConfig
import dev.tenx.fxmobile.domain.model.MessageRole
import retrofit2.Response

sealed class KiloError : Exception() {
    data class Network(override val message: String) : KiloError()
    data class Unauthorized(override val message: String) : KiloError()
    data class RateLimited(override val message: String) : KiloError()
    data class Server(override val message: String, val code: Int) : KiloError()
    data class Unknown(override val message: String) : KiloError()
}

data class InferenceResult(
    val content: String,
    val finishReason: String?
)

interface KiloRepository {
    suspend fun sendMessage(
        messages: List<AgentMessage>,
        config: InferenceConfig
    ): Result<InferenceResult>

    suspend fun getAvailableModels(): Result<List<String>>
}

class KiloRepositoryImpl(
    private val api: KiloApi,
    private val tokenProvider: TokenProvider
) : KiloRepository {

    override suspend fun sendMessage(
        messages: List<AgentMessage>,
        config: InferenceConfig
    ): Result<InferenceResult> = runCatching {
        val token = tokenProvider.getToken()
            ?: throw KiloError.Unauthorized("No API token configured")

        val request = ChatRequestDto(
            model = config.model,
            messages = messages.map { it.toDto() },
            temperature = config.temperature,
            maxTokens = config.maxTokens,
            stream = false
        )

        val response = api.chat("Bearer $token", request)
        parseResponse(response)
    }

    override suspend fun getAvailableModels(): Result<List<String>> = runCatching {
        val response = api.listModels()
        if (!response.isSuccessful) {
            throw KiloError.Unknown("Failed to fetch models: ${response.code()}")
        }
        val body = response.body() ?: throw KiloError.Unknown("Empty response")
        body.data.map { it.id }
    }

    private fun parseResponse(response: Response<ChatResponseDto>): InferenceResult {
        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            throw when (response.code()) {
                401 -> KiloError.Unauthorized("Invalid API key")
                402 -> KiloError.Unauthorized("Insufficient balance — add credits at kilocode.ai")
                429 -> KiloError.RateLimited("Rate limit exceeded")
                in 500..599 -> KiloError.Server("Server error", response.code())
                else -> KiloError.Unknown("HTTP ${response.code()}: ${errorBody ?: "Unknown error"}")
            }
        }

        val body = response.body() ?: throw KiloError.Unknown("Empty response")
        val choice = body.choices.firstOrNull() ?: throw KiloError.Unknown("No choices returned")

        return InferenceResult(
            content = choice.message?.content ?: "",
            finishReason = choice.finishReason
        )
    }

    private fun AgentMessage.toDto() = ChatMessageDto(
        role = when (role) {
            MessageRole.USER -> "user"
            MessageRole.ASSISTANT -> "assistant"
            MessageRole.SYSTEM -> "system"
        },
        content = content
    )
}
