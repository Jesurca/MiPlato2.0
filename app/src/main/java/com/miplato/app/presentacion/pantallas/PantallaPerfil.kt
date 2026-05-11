package com.miplato.app.presentacion.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.miplato.app.presentacion.navegacion.navegarALoginYCerrarSesion
import com.miplato.app.presentacion.viewmodels.SesionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPerfil(
    navController: NavHostController,
    modeloSesion: SesionViewModel = hiltViewModel()
) {
    val estado by modeloSesion.estado.collectAsState()
    var mostrarConfirmacionSalida by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { relleno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Datos del usuario",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Nombre",
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = estado.nombre.ifBlank { "No disponible" },
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Correo electrónico",
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = estado.correo.ifBlank { "No disponible" },
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Seguridad",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Tu sesión está protegida con Firebase Authentication.",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = { mostrarConfirmacionSalida = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text("Cerrar Sesión")
            }
        }
    }

    if (mostrarConfirmacionSalida) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacionSalida = false },
            title = { Text("¿Deseas cerrar sesión?") },
            text = { Text("Se cerrará tu sesión actual y volverás a la pantalla de login.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacionSalida = false
                        modeloSesion.cerrarSesion {
                            navController.navegarALoginYCerrarSesion()
                        }
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacionSalida = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
