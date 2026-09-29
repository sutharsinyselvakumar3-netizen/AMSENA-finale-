package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AiCompanionColorScheme = lightColorScheme(
    primary = NavyBlue,
    onPrimary = RoyalWhite,
    primaryContainer = SurfaceWhite,
    onPrimaryContainer = NavyBlue,
    secondary = NavyBlueSecondary,
    onSecondary = RoyalWhite,
    secondaryContainer = CardBorder,
    onSecondaryContainer = NavyBlue,
    tertiary = NavyBlueLight,
    onTertiary = RoyalWhite,
    background = RoyalWhite,
    onBackground = NavyBlue,
    surface = RoyalWhite,
    onSurface = NavyBlue,
    surfaceVariant = SurfaceWhite,
    onSurfaceVariant = NavyBlueSecondary,
    outline = CardBorder,
    outlineVariant = LightGrey,
    error = StatusRed,
    onError = RoyalWhite
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AiCompanionColorScheme,
        typography = Typography,
        content = content
    )
}
