package dev.tenx.fxmobile.subagent

import dev.tenx.fxmobile.data.remote.KiloRepository
import dev.tenx.fxmobile.data.remote.InferenceConfig
import dev.tenx.fxmobile.domain.model.AgentMessage
import dev.tenx.fxmobile.domain.model.MessageRole
import dev.tenx.fxmobile.tools.ToolRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withTimeout
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

object SubagentLimits {
    const val MAX_CONCURRENT = 3
    const val MAX_DEPTH = 2
    const val TIMEOUT_MS = 30_000L
}

data class SubagentTask(
    val id: String,
    val description: String,
    val status: TaskStatus = TaskStatus.PENDING
)

enum class TaskStatus {
    PENDING, RUNNING, COMPLETED, FAILED, CANCELLED
}

data class SubagentResult(
    val taskId: String,
    val output: String,
    val success: Boolean
)

@Singleton
class SubagentManager @Inject constructor(
    private val kiloRepository: KiloRepository,
    private val toolRegistry: ToolRegistry
) {
    private val activeJobs = mutableMapOf<String, Job>()
    private val scope = CoroutineScope(Dispatchers.IO)

    suspend fun runTask(
        sessionId: String,
        task: String,
        context: String = ""
    ): SubagentResult = coroutineScope {
        val taskId = "task-${UUID.randomUUID()}"

        try {
            val result = withTimeout(SubagentLimits.TIMEOUT_MS) {
                val prompt = buildPrompt(task, context)
                val messages = listOf(
                    AgentMessage(
                        id = "system-$taskId",
                        sessionId = sessionId,
                        role = MessageRole.SYSTEM,
                        content = prompt,
                        createdAt = System.currentTimeMillis()
                    )
                )

                val response = kiloRepository.sendMessage(
                    messages = messages,
                    config = InferenceConfig()
                )

                response.fold(
                    onSuccess = { result ->
                        SubagentResult(
                            taskId = taskId,
                            output = result.content,
                            success = true
                        )
                    },
                    onFailure = { e ->
                        SubagentResult(
                            taskId = taskId,
                            output = "Task failed: ${e.message}",
                            success = false
                        )
                    }
                )
            }
            result
        } catch (e: Exception) {
            SubagentResult(
                taskId = taskId,
                output = "Task error: ${e.message}",
                success = false
            )
        }
    }

    suspend fun runParallel(
        sessionId: String,
        tasks: List<String>
    ): List<SubagentResult> = supervisorScope {
        tasks.take(SubagentLimits.MAX_CONCURRENT).mapIndexed { index, task ->
            async {
                runTask(
                    sessionId = sessionId,
                    task = task,
                    context = "Task ${index + 1} of ${tasks.size}"
                )
            }
        }.awaitAll()
    }

    fun cancel(taskId: String) {
        activeJobs[taskId]?.cancel()
        activeJobs.remove(taskId)
    }

    fun cancelAll() {
        activeJobs.values.forEach { it.cancel() }
        activeJobs.clear()
    }

    private fun buildPrompt(task: String, context: String): String {
        return buildString {
            appendLine("You are a subagent helping with a development task.")
            appendLine("Complete the following task concisely and accurately.")
            if (context.isNotEmpty()) {
                appendLine("Context: $context")
            }
            appendLine()
            appendLine("Task: $task")
            appendLine()
            appendLine("Available tools: shell, file_read, file_write, file_list")
            appendLine("If you need to use a tool, format your response as:")
            appendLine("TOOL: <tool_name>")
            appendLine("ARGS: <json_arguments>")
        }
    }
}
