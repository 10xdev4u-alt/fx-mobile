package dev.tenx.fxmobile.terminal

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

data class CommandResult(
    val command: String,
    val output: String,
    val exitCode: Int,
    val isError: Boolean = false
)

class ShellExecutor {
    private val tag = "ShellExecutor"
    var workingDirectory: File = File("/data/data/dev.tenx.fxmobile/files/workspace")
        private set

    init {
        workingDirectory.mkdirs()
    }

    fun setWorkingDirectory(path: String) {
        val dir = File(path)
        if (dir.exists() && dir.isDirectory) {
            workingDirectory = dir
        }
    }

    suspend fun execute(command: String): CommandResult = withContext(Dispatchers.IO) {
        Log.d(tag, "Executing: $command in ${workingDirectory.absolutePath}")
        
        try {
            val process = ProcessBuilder()
                .command("/system/bin/sh", "-c", command)
                .directory(workingDirectory)
                .redirectErrorStream(true)
                .start()

            val output = StringBuilder()
            val reader = BufferedReader(InputStreamReader(process.inputStream))

            reader.useLines { lines ->
                lines.forEach { line ->
                    output.append(line).append("\n")
                }
            }

            val exitCode = process.waitFor()
            Log.d(tag, "Exit code: $exitCode")

            CommandResult(
                command = command,
                output = output.toString().trimEnd(),
                exitCode = exitCode,
                isError = exitCode != 0
            )
        } catch (e: Exception) {
            Log.e(tag, "Command failed", e)
            CommandResult(
                command = command,
                output = "Error: ${e.message}",
                exitCode = -1,
                isError = true
            )
        }
    }

    fun getEnvironment(): Map<String, String> {
        return mapOf(
            "HOME" to workingDirectory.absolutePath,
            "PWD" to workingDirectory.absolutePath,
            "PATH" to "/data/data/dev.tenx.fxmobile/files/usr/bin:/system/bin:/system/xbin:/system/bin/.ext",
            "SHELL" to "/system/bin/sh",
            "TERM" to "xterm-256color",
            "USER" to "fxmobile",
            "TMPDIR" to File(workingDirectory, "tmp").absolutePath
        )
    }

    fun getWorkingDirectoryPath(): String = workingDirectory.absolutePath

    fun listFiles(path: String = workingDirectory.absolutePath): List<FileInfo> {
        val dir = File(path)
        if (!dir.exists() || !dir.isDirectory) return emptyList()
        return dir.listFiles()?.map { file ->
            FileInfo(
                name = file.name,
                path = file.absolutePath,
                isDirectory = file.isDirectory,
                size = file.length(),
                lastModified = file.lastModified()
            )
        } ?: emptyList()
    }

    suspend fun readFile(path: String): String = withContext(Dispatchers.IO) {
        try {
            File(path).readText()
        } catch (e: Exception) {
            "Error reading file: ${e.message}"
        }
    }

    suspend fun writeFile(path: String, content: String): String = withContext(Dispatchers.IO) {
        try {
            val file = File(path)
            file.parentFile?.mkdirs()
            file.writeText(content)
            "File written successfully: $path"
        } catch (e: Exception) {
            "Error writing file: ${e.message}"
        }
    }
}

data class FileInfo(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long,
    val lastModified: Long
)
