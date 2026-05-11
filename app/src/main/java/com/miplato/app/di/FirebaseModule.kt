package com.miplato.app.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        val bucket = com.miplato.app.BuildConfig.FIREBASE_STORAGE_BUCKET.trim()
        return when {
            bucket.startsWith("gs://") -> FirebaseStorage.getInstance(bucket)
            bucket.isNotBlank() -> FirebaseStorage.getInstance("gs://$bucket")
            else -> FirebaseStorage.getInstance()
        }
    }

    @Provides
    @Singleton
    fun provideImageLabeler(): ImageLabeler {
        return ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
    }
}
