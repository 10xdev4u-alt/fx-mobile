package dev.tenx.fxmobile.domain.model

data class AgentSession(
    val id: String,
    val title: String,
    val createdAt: Long,
    val updatedAt: Long,
    val messageCount: Int
)

data class AgentMessage(
    val id: String,
    val sessionId: String,
    val role: MessageRole,
    val content: String,
    val createdAt: Long
)

enum class MessageRole {
    USER,
    ASSISTANT,
    SYSTEM
}

data class InferenceConfig(
    val provider: String = "kilo",
    val model: String = "kimi-k2",
    val temperature: Float = 0.7f,
    val maxTokens: Int = 4096
)

data class UserPreferences(
    val darkMode: Boolean = true,
    val notifications: Boolean = true,
    val autoSaveSessions: Boolean = true,
    val inferenceConfig: InferenceConfig = InferenceConfig()
)
