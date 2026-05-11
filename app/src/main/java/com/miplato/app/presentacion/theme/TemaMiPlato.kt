package com.miplato.app.presentacion.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Mint,
    onPrimary = DarkBackground,
    primaryContainer = Mint.copy(alpha = 0.1f),
    onPrimaryContainer = Mint,
    secondary = Mint,
    onSecondary = DarkBackground,
    surface = DarkBackground,
    onSurface = OnDarkSurface,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = TextGray,
    background = DarkBackground,
    onBackground = OnDarkSurface,
    error = ErrorRed,
    onError = Color.White,
    outline = DarkSurfaceVariant
)

@Composable
fun TemaMiPlato(
    darkTheme: Boolean = true, // Forzamos dark theme para coincidir con el nuevo diseño
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
