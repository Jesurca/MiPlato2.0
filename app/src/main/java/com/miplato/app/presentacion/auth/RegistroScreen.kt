package com.miplato.app.presentacion.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.miplato.app.R
import com.miplato.app.presentacion.componentes.MiPlatoBoton
import com.miplato.app.presentacion.componentes.MiPlatoCampoTexto
import com.miplato.app.presentacion.theme.Mint
import com.miplato.app.presentacion.theme.DarkSurface
import com.miplato.app.presentacion.theme.TextGray
import com.miplato.app.presentacion.util.EstadoUI

@OptIn(ExperimentalMaterial3Api::class)
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
    var objetivo by remember { mutableStateOf("Mantener") }

    val estadoAuth by viewModel.estadoAuth.collectAsState()

    LaunchedEffect(estadoAuth) {
        if (estadoAuth is EstadoUI.Exito) {
            onNavegarAHome()
            viewModel.resetearEstado()
        }
    }

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
                            Icon(
                                imageVector = Icons.Default.Restaurant,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("MiPlato", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    }
                },
                actions = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Crear Cuenta",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Text(
                text = "Únete a la ciencia de la nutrición inteligente.",
                color = TextGray,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            // User Info Section
            SectionTitle("INFORMACIÓN DEL USUARIO")
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(DarkSurface)
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    MiPlatoCampoTexto(
                        valor = nombre,
                        onValueChange = { nombre = it },
                        label = "Nombre completo",
                        placeholder = "[Nombre...]",
                        leadingIcon = Icons.Default.Person
                    )
                    MiPlatoCampoTexto(
                        valor = correo,
                        onValueChange = { correo = it },
                        label = "Correo electrónico",
                        placeholder = "[Correo...]",
                        leadingIcon = Icons.Default.Email
                    )
                    MiPlatoCampoTexto(
                        valor = contra,
                        onValueChange = { contra = it },
                        label = "Contraseña",
                        placeholder = "........",
                        esPassword = true,
                        leadingIcon = Icons.Default.Lock
                    )
                    MiPlatoCampoTexto(
                        valor = confirmarContra,
                        onValueChange = { confirmarContra = it },
                        label = "Confirmar contraseña",
                        placeholder = "........",
                        esPassword = true,
                        leadingIcon = Icons.Default.Refresh
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Initial Goals Section
            SectionTitle("OBJETIVOS INICIALES (opcional)")
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GoalChip("Bajar peso", seleccionado = objetivo == "Bajar peso") { objetivo = "Bajar peso" }
                GoalChip("Mantener", seleccionado = objetivo == "Mantener") { objetivo = "Mantener" }
                GoalChip("Subir masa", seleccionado = objetivo == "Subir masa") { objetivo = "Subir masa" }
            }

            Spacer(modifier = Modifier.height(32.dp))

            MiPlatoBoton(
                texto = "Registrarse",
                onClick = { viewModel.registrarUsuario(nombre, correo, contra, confirmarContra) },
                cargando = estadoAuth is EstadoUI.Cargando
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Social Register
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.1f))
                Text(
                    text = "O regístrate con",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextGray,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SocialIconCircle(icon = R.drawable.ic_google) { /* TODO */ }
                Spacer(modifier = Modifier.width(24.dp))
                SocialIconCircle(icon = R.drawable.ic_apple) { /* TODO */ }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "¿Ya tienes cuenta? ", color = TextGray)
                Text(
                    text = "Iniciar sesión",
                    color = Mint,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onBack() }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        ),
        color = Mint,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun GoalChip(text: String, seleccionado: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (seleccionado) Mint else DarkSurface,
        border = if (seleccionado) null else BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
        modifier = Modifier.height(40.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = if (seleccionado) Color.Black else Color.White
            )
        }
    }
}

@Composable
fun SocialIconCircle(icon: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = androidx.compose.ui.res.painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color.Unspecified
        )
    }
}
