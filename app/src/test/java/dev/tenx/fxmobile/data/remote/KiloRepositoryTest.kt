package dev.tenx.fxmobile.data.remote

import dev.tenx.fxmobile.data.remote.dto.ChatRequestDto
import dev.tenx.fxmobile.data.remote.dto.ChatResponseDto
import dev.tenx.fxmobile.data.remote.dto.ChoiceDto
import dev.tenx.fxmobile.data.remote.dto.ChatMessageDto
import dev.tenx.fxmobile.domain.model.AgentMessage
import dev.tenx.fxmobile.domain.model.InferenceConfig
import dev.tenx.fxmobile.domain.model.MessageRole
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class KiloRepositoryTest {

    private val api = mockk<KiloApi>()
    private val tokenProvider = mockk<TokenProvider>()
    private val repository = KiloRepositoryImpl(api, tokenProvider)

    @Test
    fun `sendMessage returns success when API call succeeds`() = runTest {
        val token = "test-token"
        val message = AgentMessage("1", "session-1", MessageRole.USER, "Hello", 0)
        val config = InferenceConfig()

        coEvery { tokenProvider.getToken() } returns token
        coEvery { api.chat(any(), any()) } returns Response.success(
            ChatResponseDto(
                id = "resp-1",
                objectType = "chat.completion",
                created = 1234567890,
                model = "kimi-k2",
                choices = listOf(
                    ChoiceDto(
                        index = 0,
                        message = ChatMessageDto("assistant", "Hi there!"),
                        finishReason = "stop"
                    )
                )
            )
        )

        val result = repository.sendMessage(listOf(message), config)

        assertTrue(result.isSuccess)
        assertEquals("Hi there!", result.getOrNull()?.content)
        assertEquals("stop", result.getOrNull()?.finishReason)
    }

    @Test
    fun `sendMessage returns failure when no token`() = runTest {
        coEvery { tokenProvider.getToken() } returns null

        val result = repository.sendMessage(emptyList(), InferenceConfig())

        assertTrue(result.isFailure)
    }

    @Test
    fun `sendMessage returns unauthorized on 401`() = runTest {
        val token = "bad-token"
        coEvery { tokenProvider.getToken() } returns token
        coEvery { api.chat(any(), any()) } returns Response.error(
            401,
            "{}".toResponseBody()
        )

        val result = repository.sendMessage(emptyList(), InferenceConfig())

        assertTrue(result.isFailure)
    }
}
