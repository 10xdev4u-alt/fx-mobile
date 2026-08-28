package dev.tenx.fxmobile.terminal

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
    private var workingDirectory: File = File("/data/data/dev.tenx.fxmobile/files/workspace")
    
    fun setWorkingDirectory(path: String) {
        val dir = File(path)
        if (dir.exists() && dir.isDirectory) {
            workingDirectory = dir
        }
    }
    
    fun getWorkingDirectory(): String = workingDirectory.absolutePath
    
    suspend fun execute(command: String): CommandResult = withContext(Dispatchers.IO) {
        try {
            val isWindows = System.getProperty("os.name").lowercase().contains("windows")
            val shell = if (isWindows) "cmd.exe" else "/system/bin/sh"
            val shellArg = if (isWindows) "/c" else "-c"
            
            val process = ProcessBuilder()
                .command(shell, shellArg, command)
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
            
            CommandResult(
                command = command,
                output = output.toString().trimEnd(),
                exitCode = exitCode,
                isError = exitCode != 0
            )
        } catch (e: Exception) {
            CommandResult(
                command = command,
                output = "Error: ${e.message}",
                exitCode = -1,
                isError = true
            )
        }
    }
    
    suspend fun executeMultiple(commands: List<String>): List<CommandResult> {
        return commands.map { execute(it) }
    }
    
    fun getEnvironment(): Map<String, String> {
        return mapOf(
            "HOME" to workingDirectory.absolutePath,
            "PWD" to workingDirectory.absolutePath,
            "PATH" to "/data/data/dev.tenx.fxmobile/files/usr/bin:/system/bin:/system/xbin",
            "SHELL" to "/system/bin/sh",
            "TERM" to "xterm-256color",
            "USER" to "fxmobile"
        )
    }
}
