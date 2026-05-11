package com.miplato.app.presentacion.detalle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miplato.app.dominio.Alimento
import com.miplato.app.dominio.usecase.AgregarAlimentoUseCase
import com.miplato.app.dominio.usecase.ObtenerAlimentoPorIdUseCase
import com.miplato.app.presentacion.util.EstadoUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetalleAlimentoViewModel @Inject constructor(
    private val obtenerAlimentoPorIdUseCase: ObtenerAlimentoPorIdUseCase,
    private val agregarAlimentoUseCase: AgregarAlimentoUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _estadoAlimento = MutableStateFlow<EstadoUI<Alimento>>(EstadoUI.Idle)
    val estadoAlimento: StateFlow<EstadoUI<Alimento>> = _estadoAlimento

    init {
        // Obtenemos el ID del alimento desde los argumentos de navegación
        val id = savedStateHandle.get<String>("alimentoId")
        id?.let { cargarAlimento(it) }
    }

    private fun cargarAlimento(id: String) {
        viewModelScope.launch {
            _estadoAlimento.value = EstadoUI.Cargando
            obtenerAlimentoPorIdUseCase(id).onSuccess {
                _estadoAlimento.value = EstadoUI.Exito(it)
            }.onFailure {
                _estadoAlimento.value = EstadoUI.Error("No se pudo cargar la información")
            }
        }
    }

    fun agregarHoy(alimento: Alimento, onSuccess: () -> Unit) {
        viewModelScope.launch {
            agregarAlimentoUseCase(alimento).onSuccess {
                onSuccess()
            }
        }
    }
}
