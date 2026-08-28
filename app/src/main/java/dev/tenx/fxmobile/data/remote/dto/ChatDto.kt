package dev.tenx.fxmobile.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChatMessageDto(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String
)

data class ChatRequestDto(
    @SerializedName("model") val model: String,
    @SerializedName("messages") val messages: List<ChatMessageDto>,
    @SerializedName("temperature") val temperature: Float = 0.7f,
    @SerializedName("max_tokens") val maxTokens: Int = 4096,
    @SerializedName("stream") val stream: Boolean = false
)

data class ChatResponseDto(
    @SerializedName("id") val id: String,
    @SerializedName("object") val objectType: String,
    @SerializedName("created") val created: Long,
    @SerializedName("model") val model: String,
    @SerializedName("choices") val choices: List<ChoiceDto>
)

data class ChoiceDto(
    @SerializedName("index") val index: Int,
    @SerializedName("message") val message: ChatMessageDto?,
    @SerializedName("finish_reason") val finishReason: String?
)

data class ErrorResponseDto(
    @SerializedName("error") val error: ErrorDetailDto
)

data class ErrorDetailDto(
    @SerializedName("message") val message: String,
    @SerializedName("type") val type: String? = null,
    @SerializedName("code") val code: Int? = null
)

data class ModelsResponseDto(
    @SerializedName("data") val data: List<ModelDto>
)

data class ModelDto(
    @SerializedName("id") val id: String,
    @SerializedName("object") val objectType: String,
    @SerializedName("created") val created: Long,
    @SerializedName("owned_by") val ownedBy: String,
    @SerializedName("name") val name: String? = null,
    @SerializedName("context_length") val contextLength: Int? = null,
    @SerializedName("pricing") val pricing: PricingDto? = null
)

data class PricingDto(
    @SerializedName("prompt") val prompt: String,
    @SerializedName("completion") val completion: String
)
