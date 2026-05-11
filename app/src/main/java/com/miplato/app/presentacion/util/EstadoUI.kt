package com.miplato.app.presentacion.util

sealed class EstadoUI<out T> {
    object Idle : EstadoUI<Nothing>()
    object Cargando : EstadoUI<Nothing>()
    data class Exito<T>(val datos: T) : EstadoUI<T>()
    data class Error(val mensaje: String) : EstadoUI<Nothing>()
}
