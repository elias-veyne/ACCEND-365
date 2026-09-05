package com.accend.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AccendDarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = ObsidianBg,
    primaryContainer = GoldDark,
    onPrimaryContainer = GoldLight,
    secondary = BronzeAccent,
    onSecondary = TextPrimary,
    background = ObsidianBg,
    onBackground = TextPrimary,
    surface = ObsidianSurface,
    onSurface = TextPrimary,
    surfaceVariant = ObsidianCard,
    onSurfaceVariant = TextSecondary,
    outline = GoldHairline,
    outlineVariant = DividerColor
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // ACCEND always runs with the dark ascension visual design
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = AccendDarkColorScheme,
        typography = Typography,
        content = content
    )
}
