package com.miplato.app.dominio.usecase

import com.miplato.app.dominio.Usuario
import com.miplato.app.dominio.repositorio.AutenticacionRepositorio
import javax.inject.Inject

class IniciarSesionUseCase @Inject constructor(
    private val repositorio: AutenticacionRepositorio
) {
    suspend operator fun invoke(correo: String, contra: String): Result<Usuario> =
        repositorio.iniciarSesion(correo, contra)
}

class RegistrarUsuarioUseCase @Inject constructor(
    private val repositorio: AutenticacionRepositorio
) {
    suspend operator fun invoke(nombre: String, correo: String, contra: String): Result<Usuario> =
        repositorio.registrarUsuario(nombre, correo, contra)
}

class IniciarSesionConGoogleUseCase @Inject constructor(
    private val repositorio: AutenticacionRepositorio
) {
    suspend operator fun invoke(idToken: String): Result<Usuario> =
        repositorio.iniciarSesionConGoogle(idToken)
}

class ObtenerUsuarioActualUseCase @Inject constructor(
    private val repositorio: AutenticacionRepositorio
) {
    operator fun invoke(): Usuario? = repositorio.obtenerUsuarioActual()
}

class CerrarSesionUseCase @Inject constructor(
    private val repositorio: AutenticacionRepositorio
) {
    suspend operator fun invoke() = repositorio.cerrarSesion()
}

class ActualizarFotoPerfilUseCase @Inject constructor(
    private val repositorio: AutenticacionRepositorio
) {
    suspend operator fun invoke(uri: Any): Result<String> = repositorio.actualizarFotoPerfil(uri)
}
