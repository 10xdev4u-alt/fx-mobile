package dev.tenx.fxmobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// YATA-inspired warm coral palette
val FxPrimary = Color(0xFF8E4A3B)
val FxOnPrimary = Color(0xFFFFFFFF)
val FxPrimaryContainer = Color(0xFFFFDAD1)
val FxOnPrimaryContainer = Color(0xFF3A0B01)
val FxSecondary = Color(0xFF5D6140)
val FxOnSecondary = Color(0xFFFFFFFF)
val FxSecondaryContainer = Color(0xFFE2E6BC)
val FxOnSecondaryContainer = Color(0xFF1B1D04)
val FxTertiary = Color(0xFF5F5791)
val FxOnTertiary = Color(0xFFFFFFFF)
val FxTertiaryContainer = Color(0xFFE5DEFF)
val FxOnTertiaryContainer = Color(0xFF1B1148)
val FxError = Color(0xFFBA1A1A)
val FxErrorContainer = Color(0xFFFFDAD6)
val FxSurface = Color(0xFFFFF8F6)
val FxSurfaceContainerLow = Color(0xFFFFF0EC)
val FxSurfaceContainer = Color(0xFFFCEAE4)
val FxSurfaceContainerHigh = Color(0xFFF6E4DE)
val FxOnSurface = Color(0xFF231916)
val FxOnSurfaceVariant = Color(0xFF53433F)
val FxOutline = Color(0xFF85736E)
val FxOutlineVariant = Color(0xFFD8C2BC)

private val LightColorScheme = lightColorScheme(
    primary = FxPrimary,
    onPrimary = FxOnPrimary,
    primaryContainer = FxPrimaryContainer,
    onPrimaryContainer = FxOnPrimaryContainer,
    secondary = FxSecondary,
    onSecondary = FxOnSecondary,
    secondaryContainer = FxSecondaryContainer,
    onSecondaryContainer = FxOnSecondaryContainer,
    tertiary = FxTertiary,
    onTertiary = FxOnTertiary,
    tertiaryContainer = FxTertiaryContainer,
    onTertiaryContainer = FxOnTertiaryContainer,
    error = FxError,
    errorContainer = FxErrorContainer,
    background = FxSurface,
    onBackground = FxOnSurface,
    surface = FxSurface,
    onSurface = FxOnSurface,
    surfaceVariant = FxSurfaceContainer,
    onSurfaceVariant = FxOnSurfaceVariant,
    outline = FxOutline,
    outlineVariant = FxOutlineVariant,
    surfaceContainer = FxSurfaceContainer,
    surfaceContainerHigh = FxSurfaceContainerHigh,
    surfaceContainerLow = FxSurfaceContainerLow,
    surfaceContainerLowest = Color.White,
    surfaceContainerHighest = Color(0xFFF0DED8)
)

private val DarkColorScheme = darkColorScheme(
    primary = FxPrimaryContainer,
    onPrimary = FxOnPrimaryContainer,
    primaryContainer = FxPrimary,
    onPrimaryContainer = FxOnPrimary,
    secondary = FxSecondaryContainer,
    onSecondary = FxOnSecondaryContainer,
    secondaryContainer = FxSecondary,
    onSecondaryContainer = FxOnSecondary,
    tertiary = FxTertiaryContainer,
    onTertiary = FxOnTertiaryContainer,
    tertiaryContainer = FxTertiary,
    onTertiaryContainer = FxOnTertiary,
    error = FxErrorContainer,
    onError = FxError,
    errorContainer = FxError,
    onErrorContainer = FxErrorContainer,
    background = FxOnSurface,
    onBackground = FxSurface,
    surface = FxOnSurface,
    onSurface = FxSurface,
    surfaceVariant = FxSurfaceContainerHigh,
    onSurfaceVariant = FxSurface,
    outline = FxOutlineVariant,
    outlineVariant = FxOutline,
    surfaceContainer = FxSurfaceContainer,
    surfaceContainerHigh = FxSurfaceContainerHigh,
    surfaceContainerLow = FxSurfaceContainerLow,
    surfaceContainerLowest = FxOnSurface,
    surfaceContainerHighest = FxSurfaceContainerHighest
)

val FxTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 34.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.02).em
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.01).em
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.W600,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.01).em
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.W600,
        fontSize = 17.sp,
        lineHeight = 24.sp,
        letterSpacing = (-0.01).em
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.01.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 13.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.01.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.05.sp
    )
)

@Composable
fun FxTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FxTypography,
        content = content
    )
}
