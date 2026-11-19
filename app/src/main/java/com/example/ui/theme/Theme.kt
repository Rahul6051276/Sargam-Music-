package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val SargamMidnightColorScheme = darkColorScheme(
    primary = EmeraldTeal,
    onPrimary = OnEmeraldTeal,
    primaryContainer = EmeraldTealContainer,
    onPrimaryContainer = EmeraldTeal,
    secondary = ElectricCyan,
    onSecondary = OnElectricCyan,
    secondaryContainer = ElectricCyanContainer,
    onSecondaryContainer = ElectricCyan,
    tertiary = DeepIndigoAccent,
    onTertiary = MidnightBackground,
    tertiaryContainer = DeepIndigoContainer,
    onTertiaryContainer = TextPrimary,
    background = MidnightBackground,
    onBackground = TextPrimary,
    surface = MidnightSurface,
    onSurface = TextPrimary,
    surfaceVariant = MidnightSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    surfaceContainerLowest = MidnightBackground,
    surfaceContainerLow = ColorTokens.SurfaceLow,
    surfaceContainer = MidnightSurface,
    surfaceContainerHigh = MidnightSurfaceElevated,
    surfaceContainerHighest = MidnightSurfaceHigh,
    outline = MidnightOutline,
    outlineVariant = MidnightOutlineSubtle,
    error = ErrorCoral,
    onError = TextPrimary,
    errorContainer = ErrorCoralContainer,
    onErrorContainer = ErrorCoral
)

private object ColorTokens {
    val SurfaceLow = androidx.compose.ui.graphics.Color(0xFF0E1525)
}

val SargamShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun SargamTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SargamMidnightColorScheme,
        typography = SargamTypography,
        shapes = SargamShapes,
        content = content
    )
}
