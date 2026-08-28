package dev.tenx.fxmobile.tools

import dev.tenx.fxmobile.terminal.CommandResult
import dev.tenx.fxmobile.terminal.ShellExecutor

interface Tool {
    val name: String
    val description: String
    val inputSchema: Map<String, Any>
    suspend fun execute(arguments: Map<String, Any>): String
}

class ShellTool(private val shellExecutor: ShellExecutor) : Tool {
    override val name = "shell"
    override val description = "Execute a shell command in the workspace directory"
    override val inputSchema = mapOf(
        "type" to "object",
        "properties" to mapOf(
            "command" to mapOf(
                "type" to "string",
                "description" to "The shell command to execute"
            )
        ),
        "required" to listOf("command")
    )

    override suspend fun execute(arguments: Map<String, Any>): String {
        val command = arguments["command"] as? String ?: return "Error: command is required"
        val result = shellExecutor.execute(command)
        return buildString {
            if (result.output.isNotEmpty()) {
                append(result.output)
            }
            if (result.isError) {
                append("\n[Exit code: ${result.exitCode}]")
            }
            if (result.output.isEmpty() && !result.isError) {
                append("[Command completed successfully]")
            }
        }
    }
}

class FileReadTool : Tool {
    override val name = "file_read"
    override val description = "Read the contents of a file"
    override val inputSchema = mapOf(
        "type" to "object",
        "properties" to mapOf(
            "path" to mapOf(
                "type" to "string",
                "description" to "Path to the file to read"
            )
        ),
        "required" to listOf("path")
    )

    override suspend fun execute(arguments: Map<String, Any>): String {
        return try {
            val path = arguments["path"] as? String ?: return "Error: path is required"
            val file = java.io.File(path)
            if (!file.exists()) return "Error: file not found: $path"
            if (!file.isFile) return "Error: not a file: $path"
            file.readText()
        } catch (e: Exception) {
            "Error reading file: ${e.message}"
        }
    }
}

class FileWriteTool : Tool {
    override val name = "file_write"
    override val description = "Write content to a file"
    override val inputSchema = mapOf(
        "type" to "object",
        "properties" to mapOf(
            "path" to mapOf(
                "type" to "string",
                "description" to "Path to the file to write"
            ),
            "content" to mapOf(
                "type" to "string",
                "description" to "Content to write to the file"
            )
        ),
        "required" to listOf("path", "content")
    )

    override suspend fun execute(arguments: Map<String, Any>): String {
        return try {
            val path = arguments["path"] as? String ?: return "Error: path is required"
            val content = arguments["content"] as? String ?: return "Error: content is required"
            val file = java.io.File(path)
            file.parentFile?.mkdirs()
            file.writeText(content)
            "File written successfully: $path"
        } catch (e: Exception) {
            "Error writing file: ${e.message}"
        }
    }
}

class FileListTool : Tool {
    override val name = "file_list"
    override val description = "List files in a directory"
    override val inputSchema = mapOf(
        "type" to "object",
        "properties" to mapOf(
            "path" to mapOf(
                "type" to "string",
                "description" to "Path to the directory to list"
            )
        ),
        "required" to listOf("path")
    )

    override suspend fun execute(arguments: Map<String, Any>): String {
        return try {
            val path = arguments["path"] as? String ?: return "Error: path is required"
            val dir = java.io.File(path)
            if (!dir.exists()) return "Error: directory not found: $path"
            if (!dir.isDirectory) return "Error: not a directory: $path"
            val files = dir.listFiles() ?: return "Error: cannot list directory"
            files.joinToString("\n") { file ->
                val prefix = if (file.isDirectory) "[DIR] " else "[FILE] "
                "$prefix${file.name}"
            }
        } catch (e: Exception) {
            "Error listing directory: ${e.message}"
        }
    }
}

class ToolRegistry(private val tools: List<Tool>) {
    private val toolMap = tools.associateBy { it.name }
    
    fun getTool(name: String): Tool? = toolMap[name]
    
    fun getAllTools(): List<Tool> = tools
    
    suspend fun execute(name: String, arguments: Map<String, Any>): String {
        val tool = toolMap[name] ?: return "Error: unknown tool '$name'"
        return try {
            tool.execute(arguments)
        } catch (e: Exception) {
            "Error executing tool '$name': ${e.message}"
        }
    }
}
