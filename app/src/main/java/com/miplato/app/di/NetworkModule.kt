package com.miplato.app.di

import com.miplato.app.datos.remoto.spoonacular.SpoonacularApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideSpoonacularApi(): SpoonacularApi {
        return Retrofit.Builder()
            .baseUrl(SpoonacularApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(SpoonacularApi::class.java)
    }
}
