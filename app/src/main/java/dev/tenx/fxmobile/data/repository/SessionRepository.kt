package dev.tenx.fxmobile.data.repository

import dev.tenx.fxmobile.data.local.db.MessageDao
import dev.tenx.fxmobile.data.local.db.SessionDao
import dev.tenx.fxmobile.data.local.db.MessageEntity
import dev.tenx.fxmobile.data.local.db.SessionEntity
import dev.tenx.fxmobile.data.remote.KiloRepository
import dev.tenx.fxmobile.data.remote.PreferencesManager
import dev.tenx.fxmobile.domain.model.AgentMessage
import dev.tenx.fxmobile.domain.model.AgentSession
import dev.tenx.fxmobile.domain.model.InferenceConfig
import dev.tenx.fxmobile.domain.model.MessageRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor(
    private val sessionDao: SessionDao,
    private val messageDao: MessageDao,
    private val kiloRepository: KiloRepository,
    private val preferencesManager: PreferencesManager
) {
    fun observeSessions(): Flow<List<AgentSession>> =
        sessionDao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observeMessages(sessionId: String): Flow<List<AgentMessage>> =
        messageDao.observeForSession(sessionId).map { list -> list.map { it.toDomain() } }

    suspend fun createSession(title: String = "New session"): String {
        val id = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        sessionDao.insert(
            SessionEntity(
                id = id,
                title = title,
                createdAt = now,
                updatedAt = now,
                messageCount = 0
            )
        )
        return id
    }

    suspend fun sendMessage(sessionId: String, content: String): Result<AgentMessage> = runCatching {
        val userMessage = AgentMessage(
            id = UUID.randomUUID().toString(),
            sessionId = sessionId,
            role = MessageRole.USER,
            content = content,
            createdAt = System.currentTimeMillis()
        )
        messageDao.insert(userMessage.toEntity())

        // Collect current messages for context
        val history = messageDao.observeForSession(sessionId).first()
        val messages = history.map { it.toDomain() }
        val config = InferenceConfig(model = preferencesManager.getModel())

        val result = kiloRepository.sendMessage(
            messages = messages,
            config = config
        ).getOrThrow()

        val assistantMessage = AgentMessage(
            id = UUID.randomUUID().toString(),
            sessionId = sessionId,
            role = MessageRole.ASSISTANT,
            content = result.content,
            createdAt = System.currentTimeMillis()
        )
        messageDao.insert(assistantMessage.toEntity())
        sessionDao.incrementMessageCount(sessionId, System.currentTimeMillis())

        assistantMessage
    }

    suspend fun deleteSession(sessionId: String) {
        messageDao.deleteForSession(sessionId)
        sessionDao.deleteById(sessionId)
    }

    suspend fun renameSession(sessionId: String, title: String) {
        val session = sessionDao.getById(sessionId) ?: return
        sessionDao.insert(session.copy(title = title, updatedAt = System.currentTimeMillis()))
    }

    private fun SessionEntity.toDomain() = AgentSession(
        id = id,
        title = title,
        createdAt = createdAt,
        updatedAt = updatedAt,
        messageCount = messageCount
    )

    private fun MessageEntity.toDomain() = AgentMessage(
        id = id,
        sessionId = sessionId,
        role = MessageRole.valueOf(role),
        content = content,
        createdAt = createdAt
    )

    private fun AgentMessage.toEntity() = MessageEntity(
        id = id,
        sessionId = sessionId,
        role = role.name,
        content = content,
        createdAt = createdAt
    )
}
