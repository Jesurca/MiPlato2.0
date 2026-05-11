package com.miplato.app.presentacion.busqueda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miplato.app.dominio.Alimento
import com.miplato.app.dominio.usecase.AgregarAlimentoUseCase
import com.miplato.app.dominio.usecase.BuscarAlimentosUseCase
import com.miplato.app.dominio.usecase.ObtenerUsuarioActualUseCase
import com.miplato.app.presentacion.util.EstadoUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BusquedaViewModel @Inject constructor(
    private val buscarAlimentosUseCase: BuscarAlimentosUseCase,
    private val agregarAlimentoUseCase: AgregarAlimentoUseCase,
    private val obtenerUsuarioActualUseCase: ObtenerUsuarioActualUseCase
) : ViewModel() {

    private val _estadoBusqueda = MutableStateFlow<EstadoUI<List<Alimento>>>(EstadoUI.Idle)
    val estadoBusqueda: StateFlow<EstadoUI<List<Alimento>>> = _estadoBusqueda

    fun buscarAlimentos(query: String) {
        if (query.isBlank()) return
        
        viewModelScope.launch {
            _estadoBusqueda.value = EstadoUI.Cargando
            val resultado = buscarAlimentosUseCase(query)
            resultado.onSuccess { lista ->
                _estadoBusqueda.value = if (lista.isEmpty()) EstadoUI.Error("No se encontraron alimentos") else EstadoUI.Exito(lista)
            }.onFailure {
                _estadoBusqueda.value = EstadoUI.Error("Error de conexión con Spoonacular")
            }
        }
    }

    fun agregarAlimento(alimento: Alimento, onSuccess: () -> Unit) {
        val usuario = obtenerUsuarioActualUseCase() ?: return
        viewModelScope.launch {
            val alimentoConUser = alimento.copy(userId = usuario.id)
            agregarAlimentoUseCase(alimentoConUser).onSuccess {
                onSuccess()
            }
        }
    }
}
