package com.miplato.app.presentacion.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miplato.app.dominio.ConsumoDiario
import com.miplato.app.dominio.usecase.ObtenerConsumoDiarioUseCase
import com.miplato.app.dominio.usecase.ObtenerUsuarioActualUseCase
import com.miplato.app.presentacion.util.EstadoUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val obtenerConsumoDiarioUseCase: ObtenerConsumoDiarioUseCase,
    private val obtenerUsuarioActualUseCase: ObtenerUsuarioActualUseCase
) : ViewModel() {

    private val _estadoConsumo = MutableStateFlow<EstadoUI<ConsumoDiario>>(EstadoUI.Idle)
    val estadoConsumo: StateFlow<EstadoUI<ConsumoDiario>> = _estadoConsumo

    private val _nombreUsuario = MutableStateFlow("")
    val nombreUsuario: StateFlow<String> = _nombreUsuario

    init {
        cargarDatosDelDia()
    }

    private fun cargarDatosDelDia() {
        val usuario = obtenerUsuarioActualUseCase()
        if (usuario == null) {
            _estadoConsumo.value = EstadoUI.Error("No hay sesión activa")
            return
        }

        _nombreUsuario.value = usuario.nombre
        val fechaHoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        viewModelScope.launch {
            _estadoConsumo.value = EstadoUI.Cargando
            obtenerConsumoDiarioUseCase(usuario.id, fechaHoy).collectLatest { consumo ->
                if (consumo != null) {
                    _estadoConsumo.value = EstadoUI.Exito(consumo)
                } else {
                    // Si no hay datos, mostramos el estado inicial con 0
                    _estadoConsumo.value = EstadoUI.Exito(
                        ConsumoDiario(usuario.id, fechaHoy, 0, 0f, 0f, 0f)
                    )
                }
            }
        }
    }
}
