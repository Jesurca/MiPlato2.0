package com.miplato.app.presentacion.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.miplato.app.dominio.ConsumoDiario
import com.miplato.app.presentacion.ui.theme.VerdePrimario
import com.miplato.app.presentacion.util.EstadoUI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavegarABusqueda: () -> Unit,
    onNavegarACamara: () -> Unit,
    onNavegarAPerfil: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val estadoConsumo by viewModel.estadoConsumo.collectAsState()
    val nombreUsuario by viewModel.nombreUsuario.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("MiPlato", fontWeight = FontWeight.Bold, color = VerdePrimario) },
                actions = {
                    IconButton(onClick = onNavegarAPerfil) {
                        // Aquí iría el icono de perfil
                    }
                }
            )
        },
        floatingActionButton = {
            Column {
                FloatingActionButton(
                    onClick = onNavegarACamara,
                    containerColor = VerdePrimario,
                    contentColor = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Reconocimiento IA")
                }
                FloatingActionButton(
                    onClick = onNavegarABusqueda,
                    containerColor = VerdePrimario,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar alimento")
                }
            }
        }
    ) { padding ->
        when (val estado = estadoConsumo) {
            is EstadoUI.Cargando -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = VerdePrimario)
                }
            }
            is EstadoUI.Exito -> {
                DashboardContent(
                    consumo = estado.datos,
                    nombreUsuario = nombreUsuario,
                    modifier = Modifier.padding(padding)
                )
            }
            is EstadoUI.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = estado.mensaje, color = MaterialTheme.colorScheme.error)
                }
            }
            else -> {}
        }
    }
}

@Composable
fun DashboardContent(
    consumo: ConsumoDiario,
    nombreUsuario: String,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                text = "¡Hola, $nombreUsuario!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Este es tu progreso de hoy",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = VerdePrimario.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Calorías Totales", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "${consumo.caloriasTotales}",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = VerdePrimario
                    )
                    Text("kcal", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        item {
            Text("Macronutrientes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Suponiendo metas diarias base para el cálculo de progreso
            MacroBar(nombre = "Proteína", actual = consumo.proteinaTotal, meta = 150f, color = Color(0xFFE91E63))
            Spacer(modifier = Modifier.height(12.dp))
            MacroBar(nombre = "Carbohidratos", actual = consumo.carbohidratosTotal, meta = 250f, color = Color(0xFF2196F3))
            Spacer(modifier = Modifier.height(12.dp))
            MacroBar(nombre = "Grasas", actual = consumo.grasasTotales, meta = 70f, color = Color(0xFFFFC107))
        }
    }
}

@Composable
fun MacroBar(nombre: String, actual: Float, meta: Float, color: Color) {
    val progreso = (actual / meta).coerceIn(0f, 1f)
    val animProgreso by animateFloatAsState(targetValue = progreso, label = "anim_macro")

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(nombre, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text("${actual.toInt()}g / ${meta.toInt()}g", style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { animProgreso },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp),
            color = color,
            trackColor = color.copy(alpha = 0.2f),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}
