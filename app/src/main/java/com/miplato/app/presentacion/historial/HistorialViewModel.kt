package com.miplato.app.presentacion.historial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miplato.app.datos.local.MiPlatoDao
import com.miplato.app.datos.local.toDomain
import com.miplato.app.dominio.Alimento
import com.miplato.app.dominio.usecase.ObtenerUsuarioActualUseCase
import com.miplato.app.presentacion.util.EstadoUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistorialViewModel @Inject constructor(
    private val dao: MiPlatoDao,
    private val obtenerUsuarioActualUseCase: ObtenerUsuarioActualUseCase
) : ViewModel() {

    private val _estadoHistorial = MutableStateFlow<EstadoUI<List<Alimento>>>(EstadoUI.Idle)
    val estadoHistorial: StateFlow<EstadoUI<List<Alimento>>> = _estadoHistorial

    init {
        cargarHistorial()
    }

    private fun cargarHistorial() {
        val usuario = obtenerUsuarioActualUseCase() ?: return
        viewModelScope.launch {
            _estadoHistorial.value = EstadoUI.Cargando
            dao.observarHistorial(usuario.id).collectLatest { lista ->
                _estadoHistorial.value = if (lista.isEmpty()) {
                    EstadoUI.Error("Aún no has registrado alimentos")
                } else {
                    EstadoUI.Exito(lista.map { it.toDomain() })
                }
            }
        }
    }
}
