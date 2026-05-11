package com.miplato.app.dominio.casouso

import com.miplato.app.dominio.Alimento
import com.miplato.app.dominio.ConsumoDiario
import com.miplato.app.dominio.repositorio.NutricionRepositorio
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para obtener el resumen nutricional del día.
 */
class CasoUsoObtenerConsumoDiario @Inject constructor(
    private val repositorio: NutricionRepositorio
) {
    operator fun invoke(idUsuario: String, fecha: String): Flow<ConsumoDiario?> =
        repositorio.obtenerConsumoDiario(idUsuario, fecha)
}

/**
 * Caso de uso para registrar un nuevo alimento consumido.
 */
class CasoUsoAgregarAlimento @Inject constructor(
    private val repositorio: NutricionRepositorio
) {
    suspend operator fun invoke(alimento: Alimento): Result<Unit> =
        repositorio.guardarAlimento(alimento)
}

/**
 * Caso de uso para buscar alimentos en la API externa.
 */
class CasoUsoBuscarAlimentos @Inject constructor(
    private val repositorio: NutricionRepositorio
) {
    suspend operator fun invoke(consulta: String): Result<List<Alimento>> =
        repositorio.buscarAlimentosSpoonacular(consulta)
}

/**
 * Caso de uso para identificar un alimento mediante IA.
 */
class CasoUsoReconocerAlimento @Inject constructor(
    private val repositorio: NutricionRepositorio
) {
    suspend operator fun invoke(imagen: Any): Result<List<String>> =
        repositorio.reconocerAlimentoDesdeImagen(imagen)
}
