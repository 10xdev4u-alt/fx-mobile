package dev.tenx.fxmobile.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PermissionManagerTest {

    @Test
    fun `hasInternetPermission returns true when granted`() {
        val context = mockk<Context>(relaxed = true)
        every {
            context.checkSelfPermission(Manifest.permission.INTERNET)
        } returns PackageManager.PERMISSION_GRANTED

        val manager = PermissionManager(context)
        assertTrue(manager.hasInternetPermission())
    }

    @Test
    fun `hasInternetPermission returns false when denied`() {
        val context = mockk<Context>(relaxed = true)
        every {
            context.checkSelfPermission(Manifest.permission.INTERNET)
        } returns PackageManager.PERMISSION_DENIED

        val manager = PermissionManager(context)
        assertFalse(manager.hasInternetPermission())
    }

    @Test
    fun `hasNotificationPermission returns true when granted`() {
        val context = mockk<Context>(relaxed = true)
        every {
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
        } returns PackageManager.PERMISSION_GRANTED

        val manager = PermissionManager(context)
        assertTrue(manager.hasNotificationPermission())
    }

    @Test
    fun `getRequiredPermissions returns empty when all granted`() {
        val context = mockk<Context>(relaxed = true)
        every {
            context.checkSelfPermission(any())
        } returns PackageManager.PERMISSION_GRANTED

        val manager = PermissionManager(context)
        val required = manager.getRequiredPermissions()

        assertTrue(required.isEmpty())
    }
}
