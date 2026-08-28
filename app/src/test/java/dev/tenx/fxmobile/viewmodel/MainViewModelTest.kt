package dev.tenx.fxmobile.viewmodel

import app.cash.turbine.test
import dev.tenx.fxmobile.data.repository.SessionRepository
import dev.tenx.fxmobile.domain.model.AgentMessage
import dev.tenx.fxmobile.domain.model.MessageRole
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val sessionRepository = mockk<SessionRepository>(relaxed = true)
    private lateinit var viewModel: MainViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MainViewModel(sessionRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has empty values`() {
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("", state.inputDraft)
            assertFalse(state.isGenerating)
            assertEquals(null, state.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onInputChanged updates draft`() {
        viewModel.onInputChanged("Hello")

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Hello", state.inputDraft)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `send with empty draft does nothing`() {
        viewModel.send()
        coVerify(exactly = 0) { sessionRepository.sendMessage(any(), any()) }
    }

    @Test
    fun `openSession loads messages`() {
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
    fun `deleteSession removes session`() {
        viewModel.deleteSession("session-1")
        coVerify { sessionRepository.deleteSession("session-1") }
    }
}
