package com.miplato.app.datos.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MiPlatoDao {
    // Usuarios
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarUsuario(usuario: UsuarioEntidad)

    @Query("DELETE FROM usuarios WHERE id = :userId")
    suspend fun eliminarUsuario(userId: String)

    // Alimentos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarAlimento(alimento: AlimentoEntidad)

    @Query("SELECT * FROM alimentos WHERE id = :alimentoId AND userId = :userId")
    suspend fun obtenerAlimento(alimentoId: String, userId: String): AlimentoEntidad?

    @Query("SELECT * FROM alimentos WHERE userId = :userId ORDER BY fechaEpoch DESC")
    fun observarHistorial(userId: String): Flow<List<AlimentoEntidad>>

    @Query("DELETE FROM alimentos WHERE userId = :userId")
    suspend fun limpiarAlimentosPorUsuario(userId: String)

    @Query("SELECT * FROM alimentos WHERE sincronizado = 0")
    suspend fun obtenerAlimentosPendientes(): List<AlimentoEntidad>

    @Query("UPDATE alimentos SET sincronizado = 1 WHERE id = :id")
    suspend fun marcarComoSincronizado(id: String)

    // Consumo Diario
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarConsumo(consumo: ConsumoDiarioEntidad)

    @Query("SELECT * FROM consumo_diario WHERE userId = :userId AND fecha = :fecha")
    suspend fun obtenerConsumoDelDia(userId: String, fecha: String): ConsumoDiarioEntidad?

    @Query("SELECT * FROM consumo_diario WHERE userId = :userId AND fecha = :fecha")
    fun observarConsumoDelDia(userId: String, fecha: String): Flow<ConsumoDiarioEntidad?>

    @Query("DELETE FROM consumo_diario WHERE userId = :userId")
    suspend fun limpiarConsumoPorUsuario(userId: String)
}
