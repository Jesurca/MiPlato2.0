package com.miplato.app.datos.remoto

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** Respuesta de búsqueda de ingredientes Spoonacular. */
data class RespuestaBusquedaIngredientes(
    val results: List<ElementoIngredienteDto> = emptyList()
)

data class ElementoIngredienteDto(
    val id: Long = 0L,
    val name: String = ""
)

/**
 * Estimación por nombre de plato (@see https://spoonacular.com/food-api/docs#Estimate-Recipe-Nutrition-by-Title).
 */
data class EstimacionNutricionDto(
    val calories: ValorNutrienteSpoonacularDto? = null,
    val protein: ValorNutrienteSpoonacularDto? = null,
    val carbs: ValorNutrienteSpoonacularDto? = null,
    val fat: ValorNutrienteSpoonacularDto? = null
)

data class ValorNutrienteSpoonacularDto(val value: Double = 0.0)

/** Detalle de ingrediente incluye lista de nutrientes. */
data class DetalleIngredienteDto(
    val id: Long = 0L,
    val name: String = "",
    val nutrition: WrapperNutrientesDto? = null
)

data class WrapperNutrientesDto(
    val nutrients: List<NutrienteApiDto> = emptyList()
)

data class NutrienteApiDto(
    val name: String = "",
    val amount: Double = 0.0
)

interface ServicioSpoonacular {
    @GET("food/ingredients/search")
    suspend fun buscarIngredientes(
        @Query("query") texto: String,
        @Query("apiKey") claveApi: String,
        @Query("number") cantidad: Int = 15
    ): RespuestaBusquedaIngredientes

    @GET("food/ingredients/{id}/information")
    suspend fun obtenerInformacionIngrediente(
        @Path("id") identificador: Long,
        @Query("amount") cantidad: Int = 100,
        @Query("unit") unidad: String = "grams",
        @Query("apiKey") claveApi: String
    ): DetalleIngredienteDto

    @GET("recipes/guessNutrition")
    suspend fun guessNutrition(
        @Query("title") nombre: String,
        @Query("apiKey") claveApi: String
    ): EstimacionNutricionDto
}
