package dev.tenx.fxmobile.ui.util

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass

sealed class ScreenLayout {
    data object SinglePane : ScreenLayout()
    data object DualPane : ScreenLayout()
    data object TriplePane : ScreenLayout()
}

fun getScreenLayout(windowSizeClass: WindowSizeClass): ScreenLayout {
    return when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> ScreenLayout.SinglePane
        WindowWidthSizeClass.Medium -> ScreenLayout.DualPane
        WindowWidthSizeClass.Expanded -> ScreenLayout.TriplePane
        else -> ScreenLayout.SinglePane
    }
}
