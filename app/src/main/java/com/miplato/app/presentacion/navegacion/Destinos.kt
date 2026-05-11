package com.miplato.app.presentacion.navegacion

sealed class Destino(val ruta: String) {
    object Splash : Destino("splash")
    object Login : Destino("login")
    object Registro : Destino("registro")
    object Home : Destino("home")
    object Camara : Destino("camara")
    object ResultadoIA : Destino("resultado_ia/{etiqueta}") {
        fun crearRuta(etiqueta: String) = "resultado_ia/$etiqueta"
    }
    object DetalleAlimento : Destino("detalle/{alimentoId}") {
        fun crearRuta(id: String) = "detalle/$id"
    }
    object Busqueda : Destino("busqueda")
    object Planes : Destino("planes")
    object Historial : Destino("historial")
    object Perfil : Destino("perfil")
}
