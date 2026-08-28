package dev.tenx.fxmobile.data.local.storage

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val appStorageDir: File
        get() = File(context.filesDir, "workspace").apply { mkdirs() }

    fun getAppStoragePath(): String = appStorageDir.absolutePath

    fun getWorkspaceDir(): File = appStorageDir

    suspend fun readFile(path: String): String = withContext(Dispatchers.IO) {
        File(path).readText()
    }

    suspend fun writeFile(path: String, content: String) = withContext(Dispatchers.IO) {
        val file = File(path)
        file.parentFile?.mkdirs()
        file.writeText(content)
    }

    suspend fun deleteFile(path: String): Boolean = withContext(Dispatchers.IO) {
        File(path).delete()
    }

    suspend fun listFiles(dirPath: String): List<FileInfo> = withContext(Dispatchers.IO) {
        val dir = File(dirPath)
        if (!dir.exists() || !dir.isDirectory) return@withContext emptyList()
        dir.listFiles()?.map { file ->
            FileInfo(
                name = file.name,
                path = file.absolutePath,
                isDirectory = file.isDirectory,
                size = file.length(),
                lastModified = file.lastModified()
            )
        } ?: emptyList()
    }

    suspend fun createDirectory(path: String): Boolean = withContext(Dispatchers.IO) {
        File(path).mkdirs()
    }

    fun getExternalStoragePath(): String? {
        val externalDirs = context.getExternalFilesDirs(null)
        return externalDirs.firstOrNull()?.absolutePath
    }

    fun isExternalStorageAvailable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    fun getAvailableSpace(): Long {
        return appStorageDir.usableSpace
    }

    fun getTotalSpace(): Long {
        return appStorageDir.totalSpace
    }
}

data class FileInfo(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long,
    val lastModified: Long
)
