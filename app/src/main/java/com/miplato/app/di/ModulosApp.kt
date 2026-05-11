package com.miplato.app.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.miplato.app.BuildConfig
import com.miplato.app.datos.local.BaseDatosMiPlato
import com.miplato.app.datos.local.MiPlatoDao
import com.miplato.app.datos.remoto.ServicioSpoonacular
import com.miplato.app.datos.repositorios.AutenticacionRepositorioImpl
import com.miplato.app.datos.repositorios.NutricionRepositorioImpl
import com.miplato.app.dominio.repositorio.AutenticacionRepositorio
import com.miplato.app.dominio.repositorio.NutricionRepositorio
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModuloRed {

    @Provides
    @Singleton
    fun proveerClienteOkHttp(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val reintento = okhttp3.Interceptor { cadena ->
            var respuesta = cadena.proceed(cadena.request())
            var intento = 0
            while (!respuesta.isSuccessful && intento < 2 && respuesta.code in 500..599) {
                respuesta.close()
                intento++
                try {
                    Thread.sleep(250L * intento)
                } catch (_: InterruptedException) {
                    break
                }
                respuesta = cadena.proceed(cadena.request())
            }
            respuesta
        }
        return OkHttpClient.Builder()
            .addInterceptor(reintento)
            .addInterceptor(interceptor)
            .build()
    }

    @Provides
    @Singleton
    fun proveerRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.spoonacular.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun proveerServicioSpoonacular(retrofit: Retrofit): ServicioSpoonacular =
        retrofit.create(ServicioSpoonacular::class.java)

    @Provides
    @Singleton
    fun proveerBaseDatos(@ApplicationContext contexto: Context): BaseDatosMiPlato =
        Room.databaseBuilder(contexto, BaseDatosMiPlato::class.java, "miplato.db").build()

    @Provides
    fun proveerDao(baseDatos: BaseDatosMiPlato): MiPlatoDao = baseDatos.miPlatoDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ModuloRepositorios {

    @Binds
    abstract fun bindAutenticacionRepositorio(
        impl: AutenticacionRepositorioImpl
    ): AutenticacionRepositorio

    @Binds
    abstract fun bindNutricionRepositorio(
        impl: NutricionRepositorioImpl
    ): NutricionRepositorio
}
