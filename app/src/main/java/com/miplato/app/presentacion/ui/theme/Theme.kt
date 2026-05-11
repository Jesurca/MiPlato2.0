package com.miplato.app.presentacion.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val VerdePrimario = Color(0xFF4CAF50)
val VerdeOscuro = Color(0xFF388E3C)
val VerdeClaro = Color(0xFFC8E6C9)

private val LightColorScheme = lightColorScheme(
    primary = VerdePrimario,
    secondary = VerdeOscuro,
    tertiary = VerdeClaro,
    background = Color(0xFFF9F9F9),
    surface = Color.White,
    onPrimary = Color.White,
    onSurface = Color.Black
)

val Shapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun MiPlatoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) darkColorScheme(primary = VerdePrimario) else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = Shapes,
        content = content
    )
}
