package com.miplato.app.presentacion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miplato.app.core.EstadoUi
import com.miplato.app.dominio.Alimento
import com.miplato.app.dominio.AlimentoSugerido
import com.miplato.app.dominio.ConsumoDiario
import com.miplato.app.dominio.repositorio.NutricionRepositorio
import com.miplato.app.dominio.usecase.AgregarAlimentoUseCase
import com.miplato.app.dominio.usecase.ObtenerConsumoDiarioUseCase
import com.miplato.app.dominio.usecase.ObtenerUsuarioActualUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val obtenerUsuarioActualUseCase: ObtenerUsuarioActualUseCase,
    private val obtenerConsumoDiarioUseCase: ObtenerConsumoDiarioUseCase,
    private val agregarAlimentoUseCase: AgregarAlimentoUseCase,
    private val repositorioAlimentos: NutricionRepositorio
) : ViewModel() {

    private val _estadoConsumo =
        MutableStateFlow<EstadoUi<ConsumoDiario?>>(EstadoUi.Cargando)
    val estadoConsumo: StateFlow<EstadoUi<ConsumoDiario?>> = _estadoConsumo.asStateFlow()

    private val _estadoBusqueda =
        MutableStateFlow<EstadoUi<List<AlimentoSugerido>>>(EstadoUi.Exito(emptyList()))
    val estadoBusqueda = _estadoBusqueda.asStateFlow()

    private val _estadoIa =
        MutableStateFlow<EstadoUi<AlimentoSugerido>>(
            EstadoUi.Exito(AlimentoSugerido("", 0, 0f, 0f, 0f))
        )
    val estadoIa = _estadoIa.asStateFlow()

    private var observacionConsumo: Job? = null

    fun cargarConsumoHoy() {
        val usuario = obtenerUsuarioActualUseCase() ?: return
        observacionConsumo?.cancel()
        observacionConsumo =
            viewModelScope.launch {
                _estadoConsumo.value = EstadoUi.Cargando
                obtenerConsumoDiarioUseCase(usuario.id, java.time.LocalDate.now().toString()).collect { consumo ->
                    _estadoConsumo.value = EstadoUi.Exito(consumo)
                }
            }
    }

    fun buscarAlimento(consulta: String) {
        viewModelScope.launch {
            _estadoBusqueda.value = EstadoUi.Cargando
            val resultado = repositorioAlimentos.buscarAlimentosSpoonacular(consulta.trim())
            _estadoBusqueda.value =
                resultado.fold(
                    onSuccess = { lista -> 
                        val sugeridos = lista.map { 
                            AlimentoSugerido(it.nombre, it.calorias, it.proteina, it.carbohidratos, it.grasas)
                        }
                        EstadoUi.Exito(sugeridos)
                    },
                    onFailure = { EstadoUi.Error(it.message ?: "No se pudo consultar la API") }
                )
        }
    }

    fun agregarDesdeSugerido(
        nombre: String,
        calorias: Int,
        proteina: Float,
        carbohidratos: Float,
        grasas: Float
    ) {
        val usuario = obtenerUsuarioActualUseCase() ?: return
        viewModelScope.launch {
            agregarAlimentoUseCase(
                Alimento(
                    id = "",
                    userId = usuario.id,
                    nombre = nombre,
                    calorias = calorias,
                    proteina = proteina,
                    carbohidratos = carbohidratos,
                    grasas = grasas,
                    fechaEpoch = System.currentTimeMillis()
                )
            )
            cargarConsumoHoy()
        }
    }

    fun procesarImagenIa(rutaImagen: String) {
        // Implementación simplificada o delegada a NutricionRepositorio
        // Nota: NutricionRepositorio.reconocerAlimentoDesdeImagen requiere un Bitmap
    }

    /** Reinicia el estado de pantalla tras salir para no arrastrar resultados entre cuentas. */
    fun limpiarResultadoBusqueda() {
        _estadoBusqueda.value = EstadoUi.Exito(emptyList())
    }

    fun limpiarResultadoIa() {
        _estadoIa.value = EstadoUi.Exito(AlimentoSugerido("", 0, 0f, 0f, 0f))
    }
}
