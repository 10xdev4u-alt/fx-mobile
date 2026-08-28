package dev.tenx.fxmobile.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PermissionManagerTest {

    @Before
    fun setup() {
        mockkStatic(ContextCompat::class)
    }

    @After
    fun tearDown() {
        io.mockk.unmockkAll()
    }

    @Test
    fun `hasInternetPermission returns true when granted`() {
        val context = mockk<Context>(relaxed = true)
        every {
            ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET)
        } returns PackageManager.PERMISSION_GRANTED

        val manager = PermissionManager(context)
        assertTrue(manager.hasInternetPermission())
    }

    @Test
    fun `hasInternetPermission returns false when denied`() {
        val context = mockk<Context>(relaxed = true)
        every {
            ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET)
        } returns PackageManager.PERMISSION_DENIED

        val manager = PermissionManager(context)
        assertFalse(manager.hasInternetPermission())
    }

    @Test
    fun `hasNotificationPermission returns true when granted`() {
        val context = mockk<Context>(relaxed = true)
        every {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        } returns PackageManager.PERMISSION_GRANTED

        val manager = PermissionManager(context)
        assertTrue(manager.hasNotificationPermission())
    }

    @Test
    fun `getRequiredPermissions returns empty when all granted`() {
        val context = mockk<Context>(relaxed = true)
        every {
            ContextCompat.checkSelfPermission(context, any())
        } returns PackageManager.PERMISSION_GRANTED

        val manager = PermissionManager(context)
        val required = manager.getRequiredPermissions()

        assertTrue(required.isEmpty())
    }
}
