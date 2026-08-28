package dev.tenx.fxmobile.data.repository

import dev.tenx.fxmobile.data.local.db.MessageDao
import dev.tenx.fxmobile.data.local.db.SessionDao
import dev.tenx.fxmobile.data.local.db.MessageEntity
import dev.tenx.fxmobile.data.local.db.SessionEntity
import dev.tenx.fxmobile.data.remote.KiloRepository
import dev.tenx.fxmobile.data.remote.InferenceResult
import dev.tenx.fxmobile.data.remote.PreferencesManager
import dev.tenx.fxmobile.domain.model.MessageRole
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionRepositoryTest {

    private val sessionDao = mockk<SessionDao>(relaxed = true)
    private val messageDao = mockk<MessageDao>(relaxed = true)
    private val kiloRepository = mockk<KiloRepository>()
    private val preferencesManager = mockk<PreferencesManager>()

    private val repository = SessionRepository(
        sessionDao = sessionDao,
        messageDao = messageDao,
        kiloRepository = kiloRepository,
        preferencesManager = preferencesManager
    )

    @Test
    fun `createSession inserts session and returns id`() = runTest {
        val id = repository.createSession("Test session")

        assertTrue(id.isNotEmpty())
        coVerify { sessionDao.insert(any()) }
    }

    @Test
    fun `createSession uses default title when empty`() = runTest {
        val id = repository.createSession()

        assertTrue(id.isNotEmpty())
        coVerify { sessionDao.insert(any()) }
    }

    @Test
    fun `sendMessage inserts user message and assistant reply`() = runTest {
        val sessionId = "session-1"
        val messageText = "Hello"
        val responseContent = "Hi there!"

        every { messageDao.observeForSession(sessionId) } returns flowOf(
            listOf(MessageEntity("1", sessionId, MessageRole.USER.name, messageText, 0))
        )
        coEvery { preferencesManager.getModel() } returns "kimi-k2"
        coEvery { kiloRepository.sendMessage(any(), any()) } returns Result.success(
            InferenceResult(responseContent, "stop")
        )

        val result = repository.sendMessage(sessionId, messageText)

        assertTrue(result.isSuccess)
        assertEquals(responseContent, result.getOrNull()?.content)
        assertEquals(MessageRole.ASSISTANT, result.getOrNull()?.role)
        coVerify { messageDao.insert(any()) }
        coVerify { sessionDao.incrementMessageCount(sessionId, any()) }
    }

    @Test
    fun `sendMessage returns failure when kilo fails`() = runTest {
        val sessionId = "session-1"

        every { messageDao.observeForSession(sessionId) } returns flowOf(
            listOf(MessageEntity("1", sessionId, MessageRole.USER.name, "Hello", 0))
        )
        coEvery { preferencesManager.getModel() } returns "kimi-k2"
        coEvery { kiloRepository.sendMessage(any(), any()) } returns Result.failure(
            Exception("Network error")
        )

        val result = repository.sendMessage(sessionId, "Hello")

        assertTrue(result.isFailure)
    }

    @Test
    fun `observeSessions maps entities to domain`() = runTest {
        val entities = listOf(
            SessionEntity("1", "Session 1", 1000, 2000, 5),
            SessionEntity("2", "Session 2", 3000, 4000, 10)
        )
        every { sessionDao.observeAll() } returns flowOf(entities)

        val sessions = repository.observeSessions().first()

        assertEquals(2, sessions.size)
        assertEquals("Session 1", sessions[0].title)
        assertEquals(5, sessions[0].messageCount)
        assertEquals("Session 2", sessions[1].title)
        assertEquals(10, sessions[1].messageCount)
    }

    @Test
    fun `observeMessages maps entities to domain`() = runTest {
        val sessionId = "session-1"
        val entities = listOf(
            MessageEntity("1", sessionId, MessageRole.USER.name, "Hello", 0),
            MessageEntity("2", sessionId, MessageRole.ASSISTANT.name, "Hi!", 0)
        )
        every { messageDao.observeForSession(sessionId) } returns flowOf(entities)

        val messages = repository.observeMessages(sessionId).first()

        assertEquals(2, messages.size)
        assertEquals("Hello", messages[0].content)
        assertEquals(MessageRole.USER, messages[0].role)
        assertEquals("Hi!", messages[1].content)
        assertEquals(MessageRole.ASSISTANT, messages[1].role)
    }

    @Test
    fun `deleteSession removes messages and session`() = runTest {
        val sessionId = "session-1"

        repository.deleteSession(sessionId)

        coVerify { messageDao.deleteForSession(sessionId) }
        coVerify { sessionDao.deleteById(sessionId) }
    }

    @Test
    fun `renameSession updates title`() = runTest {
        val sessionId = "session-1"
        val existing = SessionEntity(sessionId, "Old title", 1000, 2000, 5)
        coEvery { sessionDao.getById(sessionId) } returns existing

        repository.renameSession(sessionId, "New title")

        coVerify { sessionDao.insert(existing.copy(title = "New title")) }
    }
}
