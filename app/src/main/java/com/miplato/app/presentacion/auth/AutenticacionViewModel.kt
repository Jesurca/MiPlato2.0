package com.miplato.app.presentacion.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miplato.app.dominio.Usuario
import com.miplato.app.dominio.usecase.IniciarSesionConGoogleUseCase
import com.miplato.app.dominio.usecase.IniciarSesionUseCase
import com.miplato.app.dominio.usecase.RegistrarUsuarioUseCase
import com.miplato.app.presentacion.util.EstadoUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AutenticacionViewModel @Inject constructor(
    private val iniciarSesionUseCase: IniciarSesionUseCase,
    private val registrarUsuarioUseCase: RegistrarUsuarioUseCase,
    private val iniciarSesionConGoogleUseCase: IniciarSesionConGoogleUseCase
) : ViewModel() {

    private val _estadoAuth = MutableStateFlow<EstadoUI<Usuario>>(EstadoUI.Idle)
    val estadoAuth: StateFlow<EstadoUI<Usuario>> = _estadoAuth

    fun iniciarSesionConGoogle(idToken: String) {
        viewModelScope.launch {
            _estadoAuth.value = EstadoUI.Cargando
            val resultado = iniciarSesionConGoogleUseCase(idToken)
            resultado.onSuccess { usuario ->
                _estadoAuth.value = EstadoUI.Exito(usuario)
            }.onFailure { error ->
                _estadoAuth.value = EstadoUI.Error(error.message ?: "Error en Google Sign-In")
            }
        }
    }

    fun iniciarSesion(correo: String, contra: String) {
        if (correo.isBlank() || contra.isBlank()) {
            _estadoAuth.value = EstadoUI.Error("Por favor, completa todos los campos")
            return
        }

        viewModelScope.launch {
            _estadoAuth.value = EstadoUI.Cargando
            val resultado = iniciarSesionUseCase(correo, contra)
            resultado.onSuccess { usuario ->
                _estadoAuth.value = EstadoUI.Exito(usuario)
            }.onFailure { error ->
                _estadoAuth.value = EstadoUI.Error(error.message ?: "Credenciales incorrectas")
            }
        }
    }

    fun registrarUsuario(nombre: String, correo: String, contra: String, confirmarContra: String) {
        if (nombre.isBlank() || correo.isBlank() || contra.isBlank()) {
            _estadoAuth.value = EstadoUI.Error("Todos los campos son obligatorios")
            return
        }
        if (contra != confirmarContra) {
            _estadoAuth.value = EstadoUI.Error("Las contraseñas no coinciden")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            _estadoAuth.value = EstadoUI.Error("Email no válido")
            return
        }

        viewModelScope.launch {
            _estadoAuth.value = EstadoUI.Cargando
            val resultado = registrarUsuarioUseCase(nombre, correo, contra)
            resultado.onSuccess { usuario ->
                _estadoAuth.value = EstadoUI.Exito(usuario)
            }.onFailure { error ->
                _estadoAuth.value = EstadoUI.Error(error.message ?: "Error al registrar usuario")
            }
        }
    }

    fun resetearEstado() {
        _estadoAuth.value = EstadoUI.Idle
    }
}
