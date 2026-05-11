@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.miplato.app.presentacion.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Restaurant
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
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.miplato.app.core.EstadoUi
import com.miplato.app.presentacion.theme.*
import com.miplato.app.presentacion.viewmodels.DetalleAlimentoViewModel

@Composable
fun PantallaDetalleAlimento(
    navController: NavHostController,
    alimentoId: String,
    modeloDetalle: DetalleAlimentoViewModel = hiltViewModel()
) {
    LaunchedEffect(alimentoId) { modeloDetalle.cargar(alimentoId) }
    val estadoDetalle by modeloDetalle.alimento.collectAsState()
    
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
    ) { relleno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            when (val muestra = estadoDetalle) {
                is EstadoUi.Cargando ->
                    Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Mint)
                    }
                is EstadoUi.Error ->
                    Text(text = muestra.mensaje, color = MaterialTheme.colorScheme.error)
                is EstadoUi.Exito -> {
                    val alimento = muestra.datos
                    
                    Text(
                        text = "Detalle del alimento",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = OnDarkSurface
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    // Image Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                    ) {
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=500&q=80", // Placeholder
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = alimento.nombre,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = Mint
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    // Macros Grid
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MacroDetailCard("PROTEÍNA", "${alimento.proteina.toInt()}g", Mint, Modifier.weight(1f))
                        MacroDetailCard("CARBOS", "${alimento.carbohidratos.toInt()}g", WarningYellow, Modifier.weight(1f))
                        MacroDetailCard("GRASAS", "${alimento.grasas.toInt()}g", InfoBlue, Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Additional Info
                    Text(
                        text = "INFORMACIÓN ADICIONAL",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = TextGray
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    InfoRow(label = "Calorías Totales", valor = "${alimento.calorias} kcal")
                    InfoRow(label = "Tamaño de Porción", valor = "100g")
                    InfoRow(label = "Índice Glucémico", valor = "Bajo")
                }
            }
        }
    }
}

@Composable
fun MacroDetailCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextGray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = color)
        }
    }
}

@Composable
fun InfoRow(label: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextGray, style = MaterialTheme.typography.bodyLarge)
        Text(text = valor, color = OnDarkSurface, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
    }
    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
}
