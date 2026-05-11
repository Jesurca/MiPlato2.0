package com.miplato.app.datos.remoto.sincronizacion

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.miplato.app.datos.local.MiPlatoDao
import com.miplato.app.datos.local.toDomain
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

@HiltWorker
class SincronizacionWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val dao: MiPlatoDao,
    private val firestore: FirebaseFirestore
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val pendientes = dao.obtenerAlimentosPendientes()
            
            for (entidad in pendientes) {
                val alimento = entidad.toDomain()
                // Subir a Firestore
                firestore.collection("alimentos")
                    .document(alimento.id)
                    .set(alimento)
                    .await()
                
                // Marcar como sincronizado localmente
                dao.marcarComoSincronizado(alimento.id)
            }
            
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}
