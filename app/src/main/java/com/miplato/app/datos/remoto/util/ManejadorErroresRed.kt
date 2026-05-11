package com.miplato.app.datos.remoto.util

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

object ManejadorErroresRed {
    fun mapearError(e: Throwable): String {
        return when (e) {
            is IOException, is FirebaseNetworkException -> "Sin conexión a internet. Revisa tu red."
            is SocketTimeoutException -> "El servidor está tardando demasiado en responder."
            is FirebaseAuthInvalidCredentialsException -> "Contraseña incorrecta o formato inválido."
            is FirebaseAuthInvalidUserException -> "No existe una cuenta con este correo."
            is FirebaseAuthUserCollisionException -> "Ya existe una cuenta con este correo electrónico."
            is HttpException -> {
                when (e.code()) {
                    401 -> "Error de autenticación con el servicio de nutrición."
                    402 -> "Límite de peticiones diarias alcanzado (Spoonacular Free Tier)."
                    404 -> "Alimento no encontrado."
                    500 -> "Error en el servidor de nutrición. Inténtalo más tarde."
                    else -> "Error inesperado en el servidor (${e.code()})."
                }
            }
            else -> e.message ?: "Ha ocurrido un error desconocido."
        }
    }
}
