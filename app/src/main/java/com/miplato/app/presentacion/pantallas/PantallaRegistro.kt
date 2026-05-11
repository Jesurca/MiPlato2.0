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
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.miplato.app.presentacion.componentes.MiPlatoBoton
import com.miplato.app.presentacion.componentes.MiPlatoCampoTexto
import com.miplato.app.presentacion.componentes.PantallaCarga
import com.miplato.app.presentacion.navegacion.navegarAlInicioTrasAutenticacion
import com.miplato.app.presentacion.theme.Mint
import com.miplato.app.presentacion.theme.OnDarkSurface
import com.miplato.app.presentacion.theme.TextGray
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
            text = "Crear Cuenta",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Black,
            color = Mint
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Comienza tu viaje saludable hoy",
            style = MaterialTheme.typography.bodyLarge,
            color = TextGray
        )

        Spacer(modifier = Modifier.height(48.dp))

        MiPlatoCampoTexto(
            valor = estado.nombre,
            onValueChange = modeloSesion::actualizarNombre,
            label = "Nombre completo",
            placeholder = "Juan Pérez"
        )

        Spacer(modifier = Modifier.height(16.dp))

        MiPlatoCampoTexto(
            valor = estado.correo,
            onValueChange = modeloSesion::actualizarCorreo,
            label = "Correo electrónico",
            placeholder = "ejemplo@correo.com"
        )

        Spacer(modifier = Modifier.height(16.dp))

        MiPlatoCampoTexto(
            valor = estado.clave,
            onValueChange = modeloSesion::actualizarClave,
            label = "Contraseña",
            placeholder = "••••••••",
            esPassword = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        MiPlatoCampoTexto(
            valor = confirmacion,
            onValueChange = { confirmacion = it },
            label = "Confirmar contraseña",
            placeholder = "••••••••",
            esPassword = true,
            error = if (confirmacion.isNotBlank() && confirmacion != estado.clave) "Las contraseñas no coinciden" else null
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

        Spacer(modifier = Modifier.height(32.dp))

        MiPlatoBoton(
            texto = "REGISTRARME",
            onClick = { modeloSesion.registrar(confirmacion) },
            cargando = estado.cargando
        )

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Ya tengo cuenta, ", color = TextGray)
            Text("iniciar sesión", color = Mint, fontWeight = FontWeight.Bold)
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
