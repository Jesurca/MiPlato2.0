package com.miplato.app.presentacion.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VerdePrincipal = Color(0xFF1B8D46)
private val VerdeClaro = Color(0xFFE3F7EC)
private val Fondo = Color(0xFFF7FAF8)

private val EsquemaClaroMiPlato =
    lightColorScheme(
        primary = VerdePrincipal,
        onPrimary = Color.White,
        primaryContainer = VerdeClaro,
        onPrimaryContainer = Color(0xFF0B3D1F),
        secondary = Color(0xFF2E7D32),
        onSecondary = Color.White,
        surface = Fondo,
        onSurface = Color(0xFF1A1C1A),
        error = Color(0xFFB3261E),
        onError = Color.White
    )

@Composable
fun TemaMiPlato(contenido: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaClaroMiPlato,
        content = contenido
    )
}
