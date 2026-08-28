package dev.tenx.fxmobile.data.remote

import dev.tenx.fxmobile.data.remote.dto.ChatRequestDto
import dev.tenx.fxmobile.data.remote.dto.ChatResponseDto
import dev.tenx.fxmobile.data.remote.dto.ModelsResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface KiloApi {

    @POST("chat/completions")
    suspend fun chat(
        @Header("Authorization") authorization: String,
        @Body request: ChatRequestDto
    ): Response<ChatResponseDto>

    @GET("models")
    suspend fun listModels(
        @Header("Authorization") authorization: String? = null
    ): Response<ModelsResponseDto>
}
