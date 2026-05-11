package com.miplato.app.presentacion.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.miplato.app.presentacion.componentes.PantallaCarga
import com.miplato.app.presentacion.navegacion.navegarAlInicioTrasAutenticacion
import com.miplato.app.presentacion.viewmodels.SesionViewModel

@Composable
fun PantallaRegistro(
    navController: NavHostController,
    modeloSesion: SesionViewModel = hiltViewModel()
) {
    val estado by modeloSesion.estado.collectAsState()
    var confirmacion by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Registro",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = estado.nombre,
            onValueChange = modeloSesion::actualizarNombre,
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            isError = estado.error.isNotBlank()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = estado.correo,
            onValueChange = modeloSesion::actualizarCorreo,
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            isError = estado.error.isNotBlank()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = estado.clave,
            onValueChange = modeloSesion::actualizarClave,
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            visualTransformation = PasswordVisualTransformation(),
            isError = estado.error.isNotBlank()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmacion,
            onValueChange = { confirmacion = it },
            label = { Text("Confirmar contraseña") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            visualTransformation = PasswordVisualTransformation(),
            isError = estado.error.isNotBlank() && confirmacion != estado.clave
        )

        if (estado.error.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = estado.error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { modeloSesion.registrar(confirmacion) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !estado.cargando,
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Registrarme")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Ya tengo cuenta, iniciar sesión")
        }
    }

    if (estado.cargando) {
        PantallaCarga("Creando tu cuenta...")
    }

    LaunchedEffect(estado.autenticado) {
        if (estado.autenticado) {
            navController.navegarAlInicioTrasAutenticacion()
        }
    }
}
