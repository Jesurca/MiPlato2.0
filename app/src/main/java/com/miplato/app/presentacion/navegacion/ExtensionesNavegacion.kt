package com.miplato.app.presentacion.navegacion

import androidx.navigation.NavHostController

fun NavHostController.navegarAlInicioTrasAutenticacion() {
    navigate(Rutas.Home) {
        popUpTo(Rutas.Login) { inclusive = true }
        launchSingleTop = true
    }
}

fun NavHostController.navegarALoginYCerrarSesion() {
    navigate(Rutas.Login) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}

fun NavHostController.navegarDesdeSplash(destino: String) {
    navigate(destino) {
        popUpTo(Rutas.Splash) { inclusive = true }
    }
}
