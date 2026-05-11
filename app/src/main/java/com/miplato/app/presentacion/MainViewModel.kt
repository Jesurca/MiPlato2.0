package com.miplato.app.presentacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _estaListo = MutableStateFlow(false)
    val estaListo = _estaListo.asStateFlow()

    private val _estaLogueado = MutableStateFlow(false)
    val estaLogueado = _estaLogueado.asStateFlow()

    init {
        verificarSesion()
    }

    private fun verificarSesion() {
        viewModelScope.launch {
            // Simulamos una pequeña carga o verificación inicial si fuera necesaria
            val usuario = firebaseAuth.currentUser
            _estaLogueado.value = usuario != null
            _estaListo.value = true
        }
    }
}
