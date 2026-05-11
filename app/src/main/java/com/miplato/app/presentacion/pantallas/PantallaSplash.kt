package com.miplato.app.presentacion.pantallas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.miplato.app.presentacion.componentes.PantallaCarga
import com.miplato.app.presentacion.navegacion.Rutas
import com.miplato.app.presentacion.navegacion.navegarDesdeSplash
import com.miplato.app.presentacion.viewmodels.SesionViewModel

@Composable
fun PantallaSplash(
    navController: NavHostController,
    modeloSesion: SesionViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        modeloSesion.verificarSesion()
        val destino = if (modeloSesion.estado.value.autenticado) Rutas.Home else Rutas.Login
        navController.navegarDesdeSplash(destino)
    }

    PantallaCarga("Cargando sesión...")
}
