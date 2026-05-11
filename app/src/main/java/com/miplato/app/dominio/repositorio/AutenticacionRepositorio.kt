package com.miplato.app.dominio.repositorio

import com.miplato.app.dominio.Usuario

interface AutenticacionRepositorio {
    fun obtenerUsuarioActual(): Usuario?
    suspend fun iniciarSesion(correo: String, contra: String): Result<Usuario>
    suspend fun registrarUsuario(nombre: String, correo: String, contra: String): Result<Usuario>
    suspend fun iniciarSesionConGoogle(idToken: String): Result<Usuario>
    suspend fun actualizarFotoPerfil(uri: Any): Result<String>
    suspend fun cerrarSesion()
}
