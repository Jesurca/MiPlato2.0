package com.miplato.app.presentacion.camara

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miplato.app.dominio.usecase.ReconocerAlimentoUseCase
import com.miplato.app.presentacion.util.EstadoUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CamaraViewModel @Inject constructor(
    private val reconocerAlimentoUseCase: ReconocerAlimentoUseCase
) : ViewModel() {

    private val _estadoIA = MutableStateFlow<EstadoUI<List<String>>>(EstadoUI.Idle)
    val estadoIA: StateFlow<EstadoUI<List<String>>> = _estadoIA

    fun analizarImagen(bitmap: Bitmap) {
        viewModelScope.launch {
            _estadoIA.value = EstadoUI.Cargando
            val resultado = reconocerAlimentoUseCase(bitmap)
            resultado.onSuccess { labels ->
                _estadoIA.value = EstadoUI.Exito(labels)
            }.onFailure { error ->
                _estadoIA.value = EstadoUI.Error(error.message ?: "Error al procesar imagen")
            }
        }
    }

    fun resetearEstado() {
        _estadoIA.value = EstadoUI.Idle
    }
}
