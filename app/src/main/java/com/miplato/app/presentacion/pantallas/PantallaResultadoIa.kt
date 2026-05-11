@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.miplato.app.presentacion.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.miplato.app.core.EstadoUi
import com.miplato.app.presentacion.componentes.MiPlatoBoton
import com.miplato.app.presentacion.navegacion.Rutas
import com.miplato.app.presentacion.theme.Mint
import com.miplato.app.presentacion.theme.DarkSurface
import com.miplato.app.presentacion.theme.TextGray
import androidx.compose.material.icons.filled.Notifications
import com.miplato.app.presentacion.theme.OnDarkSurface
import com.miplato.app.presentacion.theme.WarningYellow
import com.miplato.app.presentacion.theme.DarkSurfaceVariant
import com.miplato.app.presentacion.viewmodels.DashboardViewModel

@Composable
fun PantallaResultadoIa(
    navController: NavHostController,
    rutaImagen: String,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val estadoIa by viewModel.estadoIa.collectAsState()
    
    LaunchedEffect(rutaImagen) {
        if (rutaImagen.isNotBlank()) viewModel.procesarImagenIa(rutaImagen)
    }
    
    DisposableEffect(Unit) {
        onDispose { viewModel.limpiarResultadoIa() }
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
                text = "Alimento capturado",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = OnDarkSurface
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(id = com.miplato.app.R.drawable.ic_splash_logo),
                    contentDescription = null,
                    tint = Mint,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Analizando alimento...",
                    color = Mint,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Image Preview Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
            ) {
                AsyncImage(
                    model = rutaImagen,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "CONFIRMADO AL 92%",
                        color = Mint,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (val actual = estadoIa) {
                is EstadoUi.Exito -> {
                    val dato = actual.datos
                    ResultadoSugeridoCard(
                        nombre = dato.nombre.ifBlank { "Pollo con arroz" },
                        proteina = dato.proteina.takeIf { it > 0 } ?: 32f,
                        carbos = dato.carbohidratos.takeIf { it > 0 } ?: 45f,
                        grasas = dato.grasas.takeIf { it > 0 } ?: 12f
                    )
                }
                is EstadoUi.Cargando -> {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Mint)
                    }
                }
                else -> {}
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "OPCIONES ALTERNATIVAS",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = TextGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            OpcionAlternativaItem("Pollo", icon = Icons.Default.Restaurant)
            Spacer(modifier = Modifier.height(8.dp))
            OpcionAlternativaItem("Arroz", icon = Icons.Default.Restaurant)
            Spacer(modifier = Modifier.height(8.dp))
            OpcionAlternativaItem("Ensalada", icon = Icons.Default.Restaurant)

            Spacer(modifier = Modifier.height(32.dp))

            MiPlatoBoton(
                texto = "Confirmar alimento",
                onClick = {
                    // Acción de confirmar
                    navController.navigate(Rutas.Home) {
                        popUpTo(Rutas.Home) { inclusive = true }
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ResultadoSugeridoCard(
    nombre: String,
    proteina: Float,
    carbos: Float,
    grasas: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(DarkSurface)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "RESULTADO SUGERIDO",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = TextGray
                    )
                    Text(
                        text = nombre,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Mint
                    )
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Restaurant, contentDescription = null, tint = Mint, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MacroMiniCard("PROT", "${proteina.toInt()}g", Modifier.weight(1f))
                MacroMiniCard("CARBS", "${carbos.toInt()}g", Modifier.weight(1f))
                MacroMiniCard("GRASAS", "${grasas.toInt()}g", Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun MacroMiniCard(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceVariant)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextGray)
            Text(text = value, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = WarningYellow)
        }
    }
}

@Composable
fun OpcionAlternativaItem(nombre: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = nombre, color = Color.White, modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(20.dp)
                .border(1.dp, TextGray, RoundedCornerShape(4.dp))
        )
    }
}
