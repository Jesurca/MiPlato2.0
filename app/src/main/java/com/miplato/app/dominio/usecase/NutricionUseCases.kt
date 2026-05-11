package com.miplato.app.dominio.usecase

import com.miplato.app.dominio.Alimento
import com.miplato.app.dominio.ConsumoDiario
import com.miplato.app.dominio.repositorio.NutricionRepositorio
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObtenerConsumoDiarioUseCase @Inject constructor(
    private val repositorio: NutricionRepositorio
) {
    operator fun invoke(userId: String, fecha: String): Flow<ConsumoDiario?> =
        repositorio.obtenerConsumoDiario(userId, fecha)
}

class AgregarAlimentoUseCase @Inject constructor(
    private val repositorio: NutricionRepositorio
) {
    suspend operator fun invoke(alimento: Alimento): Result<Unit> =
        repositorio.guardarAlimento(alimento)
}

class BuscarAlimentosUseCase @Inject constructor(
    private val repositorio: NutricionRepositorio
) {
    suspend operator fun invoke(query: String): Result<List<Alimento>> =
        repositorio.buscarAlimentosSpoonacular(query)
}

class ObtenerAlimentoPorIdUseCase @Inject constructor(
    private val repositorio: NutricionRepositorio
) {
    suspend operator fun invoke(id: String): Result<Alimento> =
        repositorio.obtenerAlimentoPorId(id)
}

class ReconocerAlimentoUseCase @Inject constructor(
    private val repositorio: NutricionRepositorio
) {
    suspend operator fun invoke(bitmap: Any): Result<List<String>> =
        repositorio.reconocerAlimentoDesdeImagen(bitmap)
}
