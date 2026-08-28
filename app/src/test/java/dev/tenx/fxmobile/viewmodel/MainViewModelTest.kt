package dev.tenx.fxmobile.viewmodel

import dev.tenx.fxmobile.data.repository.SessionRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val sessionRepository = mockk<SessionRepository>(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `send with empty draft does nothing`() {
        val viewModel = MainViewModel(sessionRepository)
        viewModel.send()
        coVerify(exactly = 0) { sessionRepository.sendMessage(any(), any()) }
    }

    @Test
    fun `deleteSession removes session`() {
        val viewModel = MainViewModel(sessionRepository)
        viewModel.deleteSession("session-1")
        coVerify { sessionRepository.deleteSession("session-1") }
    }
}
