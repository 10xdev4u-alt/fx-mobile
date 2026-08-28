package dev.tenx.fxmobile.data.local.storage

import dev.tenx.fxmobile.data.local.storage.StorageManager
import io.mockk.every
import io.mockk.mockk
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
