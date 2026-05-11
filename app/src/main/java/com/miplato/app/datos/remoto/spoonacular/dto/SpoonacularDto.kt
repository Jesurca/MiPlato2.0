package com.miplato.app.datos.remoto.spoonacular.dto

import com.miplato.app.dominio.Alimento
import com.squareup.moshi.Json

data class BuscarAlimentosRespuesta(
    @Json(name = "results") val resultados: List<AlimentoDto>
)

data class AlimentoDto(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val titulo: String,
    @Json(name = "nutrition") val nutricion: NutricionDto?
)

data class NutricionDto(
    @Json(name = "nutrients") val nutrientes: List<NutrienteDto>
)

data class NutrienteDto(
    @Json(name = "name") val nombre: String,
    @Json(name = "amount") val cantidad: Float,
    @Json(name = "unit") val unidad: String
)

fun AlimentoDto.toDomain(userId: String): Alimento {
    val calorias = nutricion?.nutrientes?.find { it.nombre == "Calories" }?.cantidad?.toInt() ?: 0
    val proteinas = nutricion?.nutrientes?.find { it.nombre == "Protein" }?.cantidad ?: 0f
    val carbohidratos = nutricion?.nutrientes?.find { it.nombre == "Carbohydrates" }?.cantidad ?: 0f
    val grasas = nutricion?.nutrientes?.find { it.nombre == "Fat" }?.cantidad ?: 0f

    return Alimento(
        id = id.toString(),
        userId = userId,
        nombre = titulo,
        calorias = calorias,
        proteina = proteinas,
        carbohidratos = carbohidratos,
        grasas = grasas,
        fechaEpoch = System.currentTimeMillis()
    )
}
