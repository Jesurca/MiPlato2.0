package com.miplato.app.presentacion.perfil

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.miplato.app.presentacion.componentes.VerdePrimario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onCerrarSesion: () -> Unit,
    onBack: () -> Unit,
    viewModel: PerfilViewModel = hiltViewModel()
) {
    val nombre by viewModel.nombreUsuario.collectAsState()
    val correo by viewModel.correoUsuario.collectAsState()
    val urlFoto by viewModel.urlFoto.collectAsState()
    val estaCargando by viewModel.estaCargando.collectAsState()
    
    var mostrarDialogo by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.actualizarFoto(it) }
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("¿Deseas cerrar sesión?") },
            text = { Text("Tendrás que volver a ingresar tus credenciales para acceder a tus datos.") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogo = false
                    viewModel.cerrarSesion(onCerrarSesion)
                }) {
                    Text("Confirmar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (urlFoto != null) {
                    AsyncImage(
                        model = urlFoto,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = VerdePrimario
                    )
                }
                
                if (estaCargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(120.dp),
                        color = VerdePrimario,
                        strokeWidth = 2.dp
                    )
                }

                // Indicador de edición
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(VerdePrimario),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Cambiar foto",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(text = nombre, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(text = correo, color = Color.Gray)

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { mostrarDialogo = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFEBEE), 
                    contentColor = Color.Red
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }
        }
    }
}
