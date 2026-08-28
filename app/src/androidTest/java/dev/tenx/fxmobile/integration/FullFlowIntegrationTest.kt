package dev.tenx.fxmobile.integration

import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
    lateinit var sessionRepository: dev.tenx.fxmobile.data.repository.SessionRepository

    @Inject
    lateinit var tokenProvider: dev.tenx.fxmobile.data.remote.TokenProvider

    @Test
    fun `create session and send message flow`() = runTest {
        // Create a session
        val sessionId = sessionRepository.createSession("Test session")
        assertNotNull(sessionId)

        // Verify session exists
        val sessions = sessionRepository.observeSessions().first()
        assertEquals(1, sessions.size)
        assertEquals("Test session", sessions.first().title)

        // Observe messages
        val messages = sessionRepository.observeMessages(sessionId).first()
        assertEquals(0, messages.size)
    }

    @Test
    fun `token provider stores and retrieves tokens`() = runTest {
        // Set token
        tokenProvider.setToken("test-token-123")
        val retrieved = tokenProvider.getToken()
        assertEquals("test-token-123", retrieved)

        // Clear token
        tokenProvider.clearToken()
        val cleared = tokenProvider.getToken()
        assertEquals(null, cleared)
    }
}
