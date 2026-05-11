package com.miplato.app.dominio

data class Usuario(
    val id: String,
    val correo: String,
    val nombre: String,
    val urlFoto: String? = null
)

data class Alimento(
    val id: String,
    val userId: String,
    val nombre: String,
    val calorias: Int,
    val proteina: Float,
    val carbohidratos: Float,
    val grasas: Float,
    val fechaEpoch: Long,
    val sincronizado: Boolean = true
)

data class ConsumoDiario(
    val userId: String,
    val fecha: String,
    val caloriasTotales: Int,
    val proteinaTotal: Float,
    val carbohidratosTotal: Float,
    val grasasTotales: Float
)

data class AlimentoSugerido(
    val nombre: String,
    val calorias: Int,
    val proteina: Float,
    val carbohidratos: Float,
    val grasas: Float
)
