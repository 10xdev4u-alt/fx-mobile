package dev.tenx.fxmobile.data.local.storage

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.tenx.fxmobile.data.local.storage.FileInfo
import dev.tenx.fxmobile.data.local.storage.StorageManager
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class StorageManagerTest {

    @Test
    fun `getAppStoragePath returns valid path`() {
        val context = mockk<android.content.Context>(relaxed = true)
        val filesDir = File("/data/data/dev.tenx.fxmobile/files")
        every { context.filesDir } returns filesDir

        val manager = StorageManager(context)
        val path = manager.getAppStoragePath()

        assertTrue(path.contains("workspace"))
    }

    @Test
    fun `getAvailableSpace returns positive value`() {
        val context = mockk<android.content.Context>(relaxed = true)
        val filesDir = File("/data/data/dev.tenx.fxmobile/files")
        every { context.filesDir } returns filesDir

        val manager = StorageManager(context)
        val space = manager.getAvailableSpace()

        assertTrue(space >= 0)
    }
}
