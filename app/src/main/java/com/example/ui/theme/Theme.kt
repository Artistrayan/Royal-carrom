package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CarromColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = VelvetDark,
    primaryContainer = GoldDark,
    onPrimaryContainer = Color.White,
    secondary = PieceQueen,
    onSecondary = Color.White,
    secondaryContainer = VelvetCardLight,
    onSecondaryContainer = GoldLight,
    tertiary = CyanGlow,
    background = VelvetDark,
    onBackground = Color.White,
    surface = VelvetSurface,
    onSurface = Color.White,
    surfaceVariant = VelvetCard,
    onSurfaceVariant = Color(0xFFD1C8E0),
    outline = GoldDark.copy(alpha = 0.5f),
    error = CrimsonFoul,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CarromColorScheme,
        typography = Typography,
        content = content
    )
}
