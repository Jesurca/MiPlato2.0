package com.miplato.app.datos.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.miplato.app.dominio.Alimento

@Entity(tableName = "alimentos")
data class AlimentoEntidad(
    @PrimaryKey val id: String,
    val userId: String,
    val nombre: String,
    val calorias: Int,
    val proteina: Float,
    val carbohidratos: Float,
    val grasas: Float,
    val fechaEpoch: Long,
    val fechaTexto: String = "",
    val sincronizado: Boolean = false
)

@Entity(tableName = "usuarios")
data class UsuarioEntidad(
    @PrimaryKey val id: String,
    val correo: String,
    val nombre: String
)

@Entity(tableName = "consumo_diario", primaryKeys = ["userId", "fecha"])
data class ConsumoDiarioEntidad(
    val userId: String,
    val fecha: String,
    val caloriasTotales: Int,
    val proteinaTotal: Float,
    val carbohidratosTotal: Float,
    val grasasTotales: Float
)

fun AlimentoEntidad.toDomain() = Alimento(
    id = id,
    userId = userId,
    nombre = nombre,
    calorias = calorias,
    proteina = proteina,
    carbohidratos = carbohidratos,
    grasas = grasas,
    fechaEpoch = fechaEpoch,
    sincronizado = sincronizado
)

fun Alimento.toEntity(sincronizado: Boolean = false) = AlimentoEntidad(
    id = id,
    userId = userId,
    nombre = nombre,
    calorias = calorias,
    proteina = proteina,
    carbohidratos = carbohidratos,
    grasas = grasas,
    fechaEpoch = fechaEpoch,
    fechaTexto = java.time.LocalDate.now().toString(),
    sincronizado = sincronizado
)
