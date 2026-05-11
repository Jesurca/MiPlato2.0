package com.miplato.app.presentacion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miplato.app.dominio.usecase.CerrarSesionUseCase
import com.miplato.app.dominio.usecase.IniciarSesionConGoogleUseCase
import com.miplato.app.dominio.usecase.IniciarSesionUseCase
import com.miplato.app.dominio.usecase.ObtenerUsuarioActualUseCase
import com.miplato.app.dominio.usecase.RegistrarUsuarioUseCase
import com.miplato.app.dominio.usecase.SincronizarDatosUsuarioDesdeNubeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EstadoSesion(
    val correo: String = "",
    val clave: String = "",
    val nombre: String = "",
    val error: String = "",
    val cargando: Boolean = false,
    val autenticado: Boolean = false
)

@HiltViewModel
class SesionViewModel @Inject constructor(
    private val iniciarSesionUseCase: IniciarSesionUseCase,
    private val iniciarSesionGoogleUseCase: IniciarSesionConGoogleUseCase,
    private val registrarUsuarioUseCase: RegistrarUsuarioUseCase,
    private val obtenerUsuarioActualUseCase: ObtenerUsuarioActualUseCase,
    private val cerrarSesionUseCase: CerrarSesionUseCase,
    private val sincronizarDatosUsuarioDesdeNubeUseCase: SincronizarDatosUsuarioDesdeNubeUseCase
) : ViewModel() {

    private val _estado = MutableStateFlow(EstadoSesion())
    val estado: StateFlow<EstadoSesion> = _estado.asStateFlow()

    fun verificarSesion() {
        _estado.update { it.copy(autenticado = obtenerUsuarioActualUseCase() != null) }
    }

    fun actualizarCorreo(valor: String) = _estado.update { it.copy(correo = valor, error = "") }
    fun actualizarClave(valor: String) = _estado.update { it.copy(clave = valor, error = "") }
    fun actualizarNombre(valor: String) = _estado.update { it.copy(nombre = valor, error = "") }

    fun mostrarError(mensaje: String) {
        _estado.update { it.copy(error = mensaje, cargando = false) }
    }

    fun iniciarSesion() {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = "") }
            val actual = _estado.value
            val resultado = iniciarSesionUseCase(actual.correo.trim(), actual.clave)
            resultado.onSuccess { usuario ->
                sincronizarDatosUsuarioDesdeNubeUseCase(usuario.id)
            }
            _estado.update {
                if (resultado.isSuccess) {
                    val usuario = resultado.getOrNull()
                    it.copy(
                        cargando = false,
                        autenticado = true,
                        error = "",
                        correo = usuario?.correo ?: it.correo,
                        nombre = usuario?.nombre ?: it.nombre
                    )
                } else {
                    it.copy(
                        cargando = false,
                        error = resultado.exceptionOrNull()?.message ?: "Error desconocido"
                    )
                }
            }
        }
    }

    fun iniciarSesionConGoogle(identificacionToken: String) {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = "") }
            val resultado = iniciarSesionGoogleUseCase(identificacionToken)
            resultado.onSuccess { usuario ->
                sincronizarDatosUsuarioDesdeNubeUseCase(usuario.id)
            }
            _estado.update {
                if (resultado.isSuccess) {
                    val usuario = resultado.getOrNull()
                    it.copy(
                        cargando = false,
                        autenticado = true,
                        error = "",
                        correo = usuario?.correo ?: it.correo,
                        nombre = usuario?.nombre ?: it.nombre
                    )
                } else {
                    it.copy(
                        cargando = false,
                        error = resultado.exceptionOrNull()?.message ?: "Error con Google"
                    )
                }
            }
        }
    }

    fun registrar(confirmacionClave: String) {
        val actual = _estado.value
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(actual.correo).matches()) {
            _estado.update { it.copy(error = "Email inválido") }
            return
        }
        if (actual.clave.length < 6) {
            _estado.update { it.copy(error = "Contraseña inválida") }
            return
        }
        if (actual.clave != confirmacionClave) {
            _estado.update { it.copy(error = "Las contraseñas no coinciden") }
            return
        }
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = "") }
            val resultado =
                registrarUsuarioUseCase(actual.correo.trim(), actual.clave, actual.nombre.trim())
            resultado.onSuccess { usuario ->
                sincronizarDatosUsuarioDesdeNubeUseCase(usuario.id)
            }
            _estado.update {
                if (resultado.isSuccess) {
                    val usuario = resultado.getOrNull()
                    it.copy(
                        cargando = false,
                        autenticado = true,
                        error = "",
                        correo = usuario?.correo ?: it.correo,
                        nombre = usuario?.nombre ?: it.nombre
                    )
                } else {
                    it.copy(
                        cargando = false,
                        error = resultado.exceptionOrNull()?.message ?: "Error de registro"
                    )
                }
            }
        }
    }

    fun cerrarSesion(enCompletado: () -> Unit) {
        viewModelScope.launch {
            cerrarSesionUseCase()
            _estado.value = EstadoSesion()
            enCompletado()
        }
    }
}
