package com.miplato.app.presentacion.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miplato.app.dominio.usecase.ActualizarFotoPerfilUseCase
import com.miplato.app.dominio.usecase.CerrarSesionUseCase
import com.miplato.app.dominio.usecase.ObtenerUsuarioActualUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerfilViewModel @Inject constructor(
    private val obtenerUsuarioActualUseCase: ObtenerUsuarioActualUseCase,
    private val cerrarSesionUseCase: CerrarSesionUseCase,
    private val actualizarFotoPerfilUseCase: ActualizarFotoPerfilUseCase
) : ViewModel() {

    private val _nombreUsuario = MutableStateFlow("")
    val nombreUsuario: StateFlow<String> = _nombreUsuario

    private val _correoUsuario = MutableStateFlow("")
    val correoUsuario: StateFlow<String> = _correoUsuario

    private val _urlFoto = MutableStateFlow<String?>(null)
    val urlFoto: StateFlow<String?> = _urlFoto

    private val _estaCargando = MutableStateFlow(false)
    val estaCargando: StateFlow<Boolean> = _estaCargando

    init {
        val usuario = obtenerUsuarioActualUseCase()
        _nombreUsuario.value = usuario?.nombre ?: "Usuario"
        _correoUsuario.value = usuario?.correo ?: ""
        _urlFoto.value = usuario?.urlFoto
    }

    fun actualizarFoto(uri: Any) {
        viewModelScope.launch {
            _estaCargando.value = true
            val resultado = actualizarFotoPerfilUseCase(uri)
            if (resultado.isSuccess) {
                _urlFoto.value = resultado.getOrNull()
            }
            _estaCargando.value = false
        }
    }

    fun cerrarSesion(onSuccess: () -> Unit) {
        viewModelScope.launch {
            cerrarSesionUseCase()
            onSuccess()
        }
    }
}
