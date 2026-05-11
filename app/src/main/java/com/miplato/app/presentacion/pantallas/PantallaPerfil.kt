package com.miplato.app.presentacion.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.miplato.app.presentacion.componentes.MiPlatoBoton
import com.miplato.app.presentacion.navegacion.navegarALoginYCerrarSesion
import com.miplato.app.presentacion.theme.DarkSurface
import com.miplato.app.presentacion.theme.DarkSurfaceVariant
import com.miplato.app.presentacion.theme.Mint
import com.miplato.app.presentacion.theme.OnDarkSurface
import com.miplato.app.presentacion.theme.TextGray
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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Mint),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Restaurant, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "MiPlato",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = OnDarkSurface
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = OnDarkSurface)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = Mint)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            MiPlatoBottomBar(navController)
        }
    ) { relleno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Perfil",
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                color = OnDarkSurface
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            // Profile Header / Avatar
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceVariant)
                        .border(2.dp, Mint, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Mint,
                        modifier = Modifier.size(60.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = estado.nombre.ifBlank { "Usuario" },
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = OnDarkSurface
                )
                Text(
                    text = estado.correo.ifBlank { "correo@ejemplo.com" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "CONFIGURACIÓN",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = TextGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            ItemConfiguracion(
                titulo = "Datos Personales",
                subtitulo = "Altura, peso, edad...",
                icon = Icons.Default.Person
            )
            Spacer(modifier = Modifier.height(12.dp))
            ItemConfiguracion(
                titulo = "Notificaciones",
                subtitulo = "Recordatorios de comidas",
                icon = Icons.Default.Notifications
            )

            Spacer(modifier = Modifier.weight(1f))

            MiPlatoBoton(
                texto = "CERRAR SESIÓN",
                onClick = { mostrarConfirmacionSalida = true },
                color = Color.Transparent,
                contentColor = Color(0xFFE57373) // Soft Red
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (mostrarConfirmacionSalida) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacionSalida = false },
            containerColor = DarkSurface,
            title = { Text("¿Deseas cerrar sesión?", color = OnDarkSurface) },
            text = { Text("Se cerrará tu sesión actual y volverás a la pantalla de login.", color = TextGray) },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacionSalida = false
                        modeloSesion.cerrarSesion {
                            navController.navegarALoginYCerrarSesion()
                        }
                    }
                ) {
                    Text("Confirmar", color = Mint, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacionSalida = false }) {
                    Text("Cancelar", color = TextGray)
                }
            }
        )
    }
}

@Composable
fun ItemConfiguracion(titulo: String, subtitulo: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Mint, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = titulo, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = OnDarkSurface)
            Text(text = subtitulo, style = MaterialTheme.typography.bodySmall, color = TextGray)
        }
        Icon(
            painter = androidx.compose.ui.res.painterResource(id = com.miplato.app.R.drawable.ic_splash_logo),
            contentDescription = null,
            tint = TextGray,
            modifier = Modifier.size(16.dp)
        )
    }
}
