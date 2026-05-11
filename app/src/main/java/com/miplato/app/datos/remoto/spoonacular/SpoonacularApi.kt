package com.miplato.app.datos.remoto.spoonacular

import com.miplato.app.datos.remoto.spoonacular.dto.BuscarAlimentosRespuesta
import retrofit2.http.GET
import retrofit2.http.Query

interface SpoonacularApi {
    @GET("food/ingredients/search")
    suspend fun buscarAlimentos(
        @Query("query") query: String,
        @Query("number") numero: Int = 10,
        @Query("addChildren") addChildren: Boolean = true,
        @Query("apiKey") apiKey: String
    ): BuscarAlimentosRespuesta

    companion object {
        const val BASE_URL = "https://api.spoonacular.com/"
    }
}
