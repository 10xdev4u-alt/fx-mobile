package dev.tenx.fxmobile.security

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityPolicy @Inject constructor() {
    private val allowedCommands = setOf(
        "ls", "cd", "pwd", "cat", "echo", "mkdir", "rm", "cp", "mv",
        "git", "grep", "find", "head", "tail", "wc", "sort", "uniq",
        "diff", "patch", "curl", "wget", "tar", "gzip", "unzip",
        "npm", "node", "python3", "pip", "cargo", "rustc", "zig",
        "make", "cmake", "javac", "java", "kotlin", "kotlinc",
        "adb", "fx", "fx ask"
    )

    private val blockedCommands = setOf(
        "sudo", "su", "chmod", "chown", "mkfs", "dd", "fdisk",
        "mount", "umount", "reboot", "shutdown", "halt", "init",
        "insmod", "rmmod", "modprobe", "sysctl", "iptables"
    )

    fun isCommandAllowed(command: String): Boolean {
        val baseCommand = command.trim().split("\\s+".toRegex()).firstOrNull() ?: return false
        return baseCommand !in blockedCommands
    }

    fun sanitizeInput(input: String): String {
        return input.replace(Regex("[;&|`$]"), "")
    }

    fun validatePath(path: String): Boolean {
        return !path.contains("..") && !path.startsWith("/system") && !path.startsWith("/data/data")
    }
}
