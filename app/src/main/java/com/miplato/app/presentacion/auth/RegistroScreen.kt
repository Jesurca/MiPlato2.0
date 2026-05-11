package com.miplato.app.presentacion.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.miplato.app.presentacion.componentes.MiPlatoBoton
import com.miplato.app.presentacion.componentes.MiPlatoCampoTexto
import com.miplato.app.presentacion.componentes.VerdePrimario
import com.miplato.app.presentacion.util.EstadoUI

@Composable
fun RegistroScreen(
    onNavegarAHome: () -> Unit,
    onBack: () -> Unit,
    viewModel: AutenticacionViewModel = hiltViewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contra by remember { mutableStateOf("") }
    var confirmarContra by remember { mutableStateOf("") }
    val estadoAuth by viewModel.estadoAuth.collectAsState()

    LaunchedEffect(estadoAuth) {
        if (estadoAuth is EstadoUI.Exito) {
            onNavegarAHome()
            viewModel.resetearEstado()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Crear Cuenta",
            style = MaterialTheme.typography.headlineLarge,
            color = VerdePrimario,
            modifier = Modifier.padding(top = 40.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        MiPlatoCampoTexto(valor = nombre, onValueChange = { nombre = it }, label = "Nombre completo")
        Spacer(modifier = Modifier.height(16.dp))
        MiPlatoCampoTexto(valor = correo, onValueChange = { correo = it }, label = "Correo electrónico")
        Spacer(modifier = Modifier.height(16.dp))
        MiPlatoCampoTexto(valor = contra, onValueChange = { contra = it }, label = "Contraseña", esPassword = true)
        Spacer(modifier = Modifier.height(16.dp))
        MiPlatoCampoTexto(
            valor = confirmarContra,
            onValueChange = { confirmarContra = it },
            label = "Confirmar Contraseña",
            esPassword = true,
            error = if (estadoAuth is EstadoUI.Error) (estadoAuth as EstadoUI.Error).mensaje else null
        )

        Spacer(modifier = Modifier.height(32.dp))

        MiPlatoBoton(
            texto = "Registrarse",
            onClick = { viewModel.registrarUsuario(nombre, correo, contra, confirmarContra) },
            cargando = estadoAuth is EstadoUI.Cargando
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBack) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}
