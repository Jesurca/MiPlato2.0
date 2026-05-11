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
import com.miplato.app.presentacion.theme.Mint
import com.miplato.app.presentacion.theme.DarkSurfaceVariant
import com.miplato.app.presentacion.theme.TextGray
import com.miplato.app.presentacion.theme.OnDarkSurface
import com.miplato.app.presentacion.theme.ErrorRed
import com.miplato.app.presentacion.componentes.MiPlatoBoton

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
            title = { Text("¿Deseas cerrar sesión?", color = OnDarkSurface) },
            text = { Text("Tendrás que volver a ingresar tus credenciales para acceder a tus datos.", color = TextGray) },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogo = false
                    viewModel.cerrarSesion(onCerrarSesion)
                }) {
                    Text("Confirmar", color = ErrorRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar", color = OnDarkSurface)
                }
            },
            containerColor = DarkSurfaceVariant
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", color = OnDarkSurface) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = OnDarkSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
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
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceVariant)
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
                        modifier = Modifier.size(70.dp),
                        tint = Mint
                    )
                }
                
                if (estaCargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(140.dp),
                        color = Mint,
                        strokeWidth = 3.dp
                    )
                }

                // Indicador de edición
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Mint),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Cambiar foto",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Black
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = nombre, 
                fontSize = 24.sp, 
                fontWeight = FontWeight.Bold,
                color = OnDarkSurface
            )
            Text(
                text = correo, 
                color = TextGray,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.weight(1f))

            MiPlatoBoton(
                texto = "Cerrar Sesión",
                onClick = { mostrarDialogo = true },
                color = ErrorRed.copy(alpha = 0.1f),
                contentColor = ErrorRed
            )
        }
    }
}
