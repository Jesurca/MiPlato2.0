package com.miplato.app.datos.repositorios

import android.content.Context
import android.graphics.Bitmap
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeler
import com.miplato.app.BuildConfig
import com.miplato.app.datos.local.MiPlatoDao
import com.miplato.app.datos.local.toDomain
import com.miplato.app.datos.local.toEntity
import com.miplato.app.datos.remoto.sincronizacion.SincronizacionWorker
import com.miplato.app.datos.remoto.spoonacular.SpoonacularApi
import com.miplato.app.datos.remoto.spoonacular.dto.toDomain
import com.miplato.app.datos.remoto.util.ManejadorErroresRed
import com.miplato.app.datos.remoto.util.TraductorAlimentos
import com.miplato.app.dominio.Alimento
import com.miplato.app.dominio.ConsumoDiario
import com.miplato.app.dominio.repositorio.NutricionRepositorio
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class NutricionRepositorioImpl @Inject constructor(
    private val api: SpoonacularApi,
    private val dao: MiPlatoDao,
    private val firestore: FirebaseFirestore,
    private val labeler: ImageLabeler,
    private val traductor: TraductorAlimentos,
    @ApplicationContext private val context: Context
) : NutricionRepositorio {

    override fun obtenerConsumoDiario(userId: String, fecha: String): Flow<ConsumoDiario?> {
        return dao.observarConsumoDelDia(userId, fecha).map { entidad ->
            entidad?.let {
                ConsumoDiario(
                    userId = it.userId,
                    fecha = it.fecha,
                    caloriasTotales = it.caloriasTotales,
                    proteinaTotal = it.proteinaTotal,
                    carbohidratosTotal = it.carbohidratosTotal,
                    grasasTotales = it.grasasTotales
                )
            } ?: ConsumoDiario(userId, fecha, 0, 0f, 0f, 0f)
        }
    }

    override fun observarHistorial(userId: String): Flow<List<Alimento>> {
        return dao.observarHistorial(userId).map { lista ->
            lista.map { it.toDomain() }
        }
    }

    override suspend fun guardarAlimento(alimento: Alimento): Result<Unit> {
        return try {
            // Guardar localmente
            dao.insertarAlimento(alimento.toEntity(sincronizado = false))
            
            // Programar sincronización
            val restricciones = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val peticionSincro = OneTimeWorkRequestBuilder<SincronizacionWorker>()
                .setConstraints(restricciones)
                .build()

            WorkManager.getInstance(context).enqueue(peticionSincro)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(ManejadorErroresRed.mapearError(e)))
        }
    }

    override suspend fun buscarAlimentosSpoonacular(query: String): Result<List<Alimento>> {
        return try {
            val respuesta = api.buscarAlimentos(query = query, apiKey = BuildConfig.SPOONACULAR_API_KEY)
            Result.success(respuesta.resultados.map { it.toDomain("") })
        } catch (e: Exception) {
            Result.failure(Exception(ManejadorErroresRed.mapearError(e)))
        }
    }

    override suspend fun obtenerAlimentoPorId(id: String): Result<Alimento> {
        return try {
            val doc = firestore.collection("alimentos").document(id).get().await()
            val alimento = doc.toObject(Alimento::class.java) ?: throw Exception("Alimento no encontrado")
            Result.success(alimento)
        } catch (e: Exception) {
            Result.failure(Exception(ManejadorErroresRed.mapearError(e)))
        }
    }

    override suspend fun reconocerAlimentoDesdeImagen(bitmap: Any): Result<List<String>> = suspendCoroutine { cont ->
        if (bitmap !is Bitmap) {
            cont.resume(Result.failure(Exception("Formato no soportado")))
            return@suspendCoroutine
        }
        val image = InputImage.fromBitmap(bitmap, 0)
        labeler.process(image)
            .addOnSuccessListener { labels ->
                val terminosIngles = labels.map { it.text }
                val terminosTraducidos = traductor.traducirLista(terminosIngles)
                cont.resume(Result.success(terminosTraducidos))
            }
            .addOnFailureListener { e -> cont.resume(Result.failure(e)) }
    }
}
