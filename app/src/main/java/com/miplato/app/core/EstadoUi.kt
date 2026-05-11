package com.miplato.app.core

sealed class EstadoUi<out T> {
    data object Cargando : EstadoUi<Nothing>()
    data class Exito<T>(val datos: T) : EstadoUi<T>()
    data class Error(val mensaje: String) : EstadoUi<Nothing>()
}
