package com.example.listy.ui.theme

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun ListyTheme(light: Boolean = false, content: @Composable () -> Unit) {
    val colors = if (light) LightColors else DarkColors
    val colorScheme = if (light) {
        lightColorScheme(
            primary = Accent,
            background = colors.background,
            onBackground = colors.textPrimary,
            surface = colors.background,
            onSurface = colors.textPrimary,
            surfaceContainerHigh = colors.background,
            onSurfaceVariant = colors.textSecondary,
            outlineVariant = colors.divider,
        )
    } else {
        darkColorScheme(
            primary = Accent,
            background = colors.background,
            onBackground = colors.textPrimary,
            surface = colors.background,
            onSurface = colors.textPrimary,
            surfaceContainerHigh = colors.cardSurface,
            onSurfaceVariant = colors.textSecondary,
            outlineVariant = colors.divider,
        )
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
    ) {
        // Surface kullanmadığımız ekranlarda da yazılar temaya uysun
        CompositionLocalProvider(
            LocalListyColors provides colors,
            LocalContentColor provides colors.textPrimary,
            content = content,
        )
    }
}
