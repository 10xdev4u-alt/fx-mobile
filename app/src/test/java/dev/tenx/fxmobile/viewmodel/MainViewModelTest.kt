package dev.tenx.fxmobile.viewmodel

import app.cash.turbine.test
import dev.tenx.fxmobile.data.repository.SessionRepository
import dev.tenx.fxmobile.domain.model.AgentMessage
import dev.tenx.fxmobile.domain.model.AgentSession
import dev.tenx.fxmobile.domain.model.MessageRole
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MainViewModelTest {

    private val sessionRepository = mockk<SessionRepository>(relaxed = true)
    private val viewModel = MainViewModel(sessionRepository)

    @Test
    fun `initial state has empty values`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("", state.inputDraft)
            assertFalse(state.isGenerating)
            assertEquals(null, state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onInputChanged updates draft`() = runTest {
        viewModel.onInputChanged("Hello")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Hello", state.inputDraft)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `send with empty draft does nothing`() = runTest {
        viewModel.send()
        coVerify(exactly = 0) { sessionRepository.sendMessage(any(), any()) }
    }

    @Test
    fun `send creates session and sends message`() = runTest {
        every { sessionRepository.createSession(any()) } returns "session-1"
        every { sessionRepository.observeMessages(any()) } returns flowOf(
            listOf(
                AgentMessage("1", "session-1", MessageRole.USER, "Hello", 0),
                AgentMessage("2", "session-1", MessageRole.ASSISTANT, "Hi!", 0)
            )
        )
        coEvery { sessionRepository.sendMessage(any(), any()) } returns Result.success(
            AgentMessage("2", "session-1", MessageRole.ASSISTANT, "Hi!", 0)
        )

        viewModel.onInputChanged("Hello")
        viewModel.send()

        // Wait for the coroutine to complete
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isGenerating)
            coVerify { sessionRepository.sendMessage("session-1", "Hello") }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `openSession loads messages`() = runTest {
        val messages = listOf(
            AgentMessage("1", "session-1", MessageRole.USER, "Hello", 0),
            AgentMessage("2", "session-1", MessageRole.ASSISTANT, "Hi!", 0)
        )
        every { sessionRepository.observeMessages("session-1") } returns flowOf(messages)

        viewModel.openSession("session-1")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("session-1", state.currentSessionId)
            assertEquals(2, state.messages.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteSession removes session`() = runTest {
        viewModel.deleteSession("session-1")
        coVerify { sessionRepository.deleteSession("session-1") }
    }
}
