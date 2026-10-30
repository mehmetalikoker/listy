package com.example.listy.ui.theme

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val ColorScheme = darkColorScheme(
    primary = Accent,
    background = AppBackground,
    onBackground = TextPrimary,
    surface = AppBackground,
    onSurface = TextPrimary,
    surfaceContainerHigh = CardSurface,
    onSurfaceVariant = TextSecondary,
    outlineVariant = Divider,
)

@Composable
fun ListyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography,
    ) {
        // Surface kullanmadığımız ekranlarda da yazılar açık renk olsun
        CompositionLocalProvider(LocalContentColor provides TextPrimary, content = content)
    }
}
