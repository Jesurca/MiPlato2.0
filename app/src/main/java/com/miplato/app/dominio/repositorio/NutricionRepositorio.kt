package com.miplato.app.dominio.repositorio

import com.miplato.app.dominio.Alimento
import com.miplato.app.dominio.ConsumoDiario
import kotlinx.coroutines.flow.Flow

interface NutricionRepositorio {
    fun obtenerConsumoDiario(userId: String, fecha: String): Flow<ConsumoDiario?>
    fun observarHistorial(userId: String): Flow<List<Alimento>>
    suspend fun guardarAlimento(alimento: Alimento): Result<Unit>
    suspend fun buscarAlimentosSpoonacular(query: String): Result<List<Alimento>>
    suspend fun obtenerAlimentoPorId(id: String): Result<Alimento>
    suspend fun reconocerAlimentoDesdeImagen(bitmap: Any): Result<List<String>>
}
