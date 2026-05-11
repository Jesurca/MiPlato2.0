package com.miplato.app.presentacion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miplato.app.core.EstadoUi
import com.miplato.app.dominio.Alimento
import com.miplato.app.dominio.repositorio.NutricionRepositorio
import com.miplato.app.dominio.usecase.ObtenerUsuarioActualUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistorialViewModel @Inject constructor(
    private val obtenerUsuarioActualUseCase: ObtenerUsuarioActualUseCase,
    private val repositorioNutricion: NutricionRepositorio
) : ViewModel() {

    private val _lista = MutableStateFlow<EstadoUi<List<Alimento>>>(EstadoUi.Cargando)
    val lista: StateFlow<EstadoUi<List<Alimento>>> = _lista.asStateFlow()

    private var trabajoHistorial: Job? = null

    init {
        refrescarParaUsuarioActual()
    }

    fun refrescarParaUsuarioActual() {
        trabajoHistorial?.cancel()
        trabajoHistorial = viewModelScope.launch {
            val usuario = obtenerUsuarioActualUseCase()
            if (usuario == null) {
                _lista.value = EstadoUi.Error("Sesión no válida")
                return@launch
            }
            _lista.value = EstadoUi.Cargando
            repositorioNutricion.observarHistorial(usuario.id).collect { registros ->
                _lista.value = EstadoUi.Exito(registros)
            }
        }
    }
}
