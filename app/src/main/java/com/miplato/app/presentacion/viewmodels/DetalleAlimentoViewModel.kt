package com.miplato.app.presentacion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miplato.app.core.EstadoUi
import com.miplato.app.dominio.Alimento
import com.miplato.app.dominio.usecase.ObtenerAlimentoPorIdUseCase
import com.miplato.app.dominio.usecase.ObtenerUsuarioActualUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetalleAlimentoViewModel @Inject constructor(
    private val obtenerUsuarioActualUseCase: ObtenerUsuarioActualUseCase,
    private val obtenerAlimentoPorIdUseCase: ObtenerAlimentoPorIdUseCase
) : ViewModel() {

    private val _alimento = MutableStateFlow<EstadoUi<Alimento>>(EstadoUi.Cargando)
    val alimento: StateFlow<EstadoUi<Alimento>> = _alimento.asStateFlow()

    fun cargar(alimentoId: String) {
        viewModelScope.launch {
            _alimento.value = EstadoUi.Cargando
            val usuario = obtenerUsuarioActualUseCase()
            if (usuario == null) {
                _alimento.value = EstadoUi.Error("Sesión no válida")
                return@launch
            }
            val resultado = obtenerAlimentoPorIdUseCase(alimentoId)
            _alimento.value = resultado.fold(
                onSuccess = { EstadoUi.Exito(it) },
                onFailure = { EstadoUi.Error(it.message ?: "Error al cargar detalle") }
            )
        }
    }
}
