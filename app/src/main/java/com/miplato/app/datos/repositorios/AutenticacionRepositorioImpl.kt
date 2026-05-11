package com.miplato.app.datos.repositorios

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.miplato.app.datos.local.MiPlatoDao
import com.miplato.app.datos.remoto.util.ManejadorErroresRed
import com.miplato.app.dominio.Usuario
import com.miplato.app.dominio.repositorio.AutenticacionRepositorio
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AutenticacionRepositorioImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val dao: MiPlatoDao
) : AutenticacionRepositorio {

    override fun obtenerUsuarioActual(): Usuario? {
        val firebaseUser = firebaseAuth.currentUser
        return firebaseUser?.let {
            Usuario(
                id = it.uid,
                correo = it.email ?: "",
                nombre = it.displayName ?: "",
                urlFoto = it.photoUrl?.toString()
            )
        }
    }

    override suspend fun iniciarSesion(correo: String, contra: String): Result<Usuario> {
        return try {
            val resultado = firebaseAuth.signInWithEmailAndPassword(correo, contra).await()
            val firebaseUser = resultado.user ?: throw Exception("Usuario nulo")
            
            val doc = firestore.collection("usuarios").document(firebaseUser.uid).get().await()
            val nombre = doc.getString("nombre") ?: firebaseUser.displayName ?: "Usuario"
            val urlFoto = doc.getString("urlFoto") ?: firebaseUser.photoUrl?.toString()
            
            Result.success(Usuario(id = firebaseUser.uid, correo = correo, nombre = nombre, urlFoto = urlFoto))
        } catch (e: Exception) {
            Result.failure(Exception(ManejadorErroresRed.mapearError(e)))
        }
    }

    override suspend fun registrarUsuario(nombre: String, correo: String, contra: String): Result<Usuario> {
        return try {
            val resultado = firebaseAuth.createUserWithEmailAndPassword(correo, contra).await()
            val firebaseUser = resultado.user ?: throw Exception("Error al crear usuario")
            
            val nuevoUsuario = Usuario(id = firebaseUser.uid, correo = correo, nombre = nombre, urlFoto = null)
            
            firestore.collection("usuarios").document(firebaseUser.uid).set(nuevoUsuario).await()
            
            Result.success(nuevoUsuario)
        } catch (e: Exception) {
            Result.failure(Exception(ManejadorErroresRed.mapearError(e)))
        }
    }

    override suspend fun iniciarSesionConGoogle(idToken: String): Result<Usuario> {
        return try {
            val credencial = GoogleAuthProvider.getCredential(idToken, null)
            val resultado = firebaseAuth.signInWithCredential(credencial).await()
            val firebaseUser = resultado.user ?: throw Exception("Error en Google Sign-In")

            val usuario = Usuario(
                id = firebaseUser.uid,
                correo = firebaseUser.email ?: "",
                nombre = firebaseUser.displayName ?: "Usuario Google",
                urlFoto = firebaseUser.photoUrl?.toString()
            )

            firestore.collection("usuarios").document(firebaseUser.uid).set(usuario).await()

            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(Exception(ManejadorErroresRed.mapearError(e)))
        }
    }

    override suspend fun actualizarFotoPerfil(uri: Any): Result<String> {
        return try {
            val user = firebaseAuth.currentUser ?: throw Exception("No hay sesión activa")
            val imageUri = uri as? Uri ?: throw Exception("Formato de imagen no válido")
            
            val ref = storage.reference.child("fotos_perfil/${user.uid}.jpg")
            ref.putFile(imageUri).await()
            val downloadUrl = ref.downloadUrl.await().toString()
            
            // Actualizar en Firestore
            firestore.collection("usuarios").document(user.uid)
                .update("urlFoto", downloadUrl)
                .await()
            
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(Exception(ManejadorErroresRed.mapearError(e)))
        }
    }

    override suspend fun cerrarSesion() {
        val userId = firebaseAuth.currentUser?.uid
        userId?.let {
            dao.limpiarAlimentosPorUsuario(it)
            dao.limpiarConsumoPorUsuario(it)
        }
        firebaseAuth.signOut()
    }
}
