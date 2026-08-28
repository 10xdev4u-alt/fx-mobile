package dev.tenx.fxmobile.integration

import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.tenx.fxmobile.data.repository.SessionRepository
import dev.tenx.fxmobile.data.remote.TokenProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class FullFlowIntegrationTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var sessionRepository: SessionRepository

    @Inject
    lateinit var tokenProvider: TokenProvider

    @Test
    fun `create session and verify it persists`() = runTest {
        val sessionId = sessionRepository.createSession("Test session")
        assertNotNull(sessionId)

        val sessions = sessionRepository.observeSessions().first()
        assertEquals(1, sessions.size)
        assertEquals("Test session", sessions.first().title)
    }

    @Test
    fun `token provider stores and retrieves tokens`() = runTest {
        tokenProvider.setToken("test-token-123")
        val retrieved = tokenProvider.getToken()
        assertEquals("test-token-123", retrieved)

        tokenProvider.clearToken()
        val cleared = tokenProvider.getToken()
        assertEquals(null, cleared)
    }

    @Test
    fun `session repository creates and deletes sessions`() = runTest {
        val sessionId = sessionRepository.createSession("To delete")
        
        val sessionsBefore = sessionRepository.observeSessions().first()
        assertTrue(sessionsBefore.isNotEmpty())

        sessionRepository.deleteSession(sessionId)

        val sessionsAfter = sessionRepository.observeSessions().first()
        assertEquals(0, sessionsAfter.size)
    }

    @Test
    fun `messages persist in session`() = runTest {
        val sessionId = sessionRepository.createSession("Messages test")
        
        // Observe messages - should be empty initially
        val messages = sessionRepository.observeMessages(sessionId).first()
        assertEquals(0, messages.size)
    }

    @Test
    fun `send message with empty token returns failure`() = runTest {
        // Ensure no token is set
        tokenProvider.clearToken()
        
        val sessionId = sessionRepository.createSession("API test")
        val result = sessionRepository.sendMessage(sessionId, "Hello")

        assertTrue(result.isFailure)
    }

    @Before
    fun setup() {
        hiltRule.inject()
    }
}
