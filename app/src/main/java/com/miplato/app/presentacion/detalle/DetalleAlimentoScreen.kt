package com.miplato.app.presentacion.detalle

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.miplato.app.dominio.Alimento
import com.miplato.app.presentacion.componentes.MiPlatoBoton
import com.miplato.app.presentacion.componentes.VerdePrimario
import com.miplato.app.presentacion.util.EstadoUI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleAlimentoScreen(
    onBack: () -> Unit,
    onAlimentoAgregado: () -> Unit,
    viewModel: DetalleAlimentoViewModel = hiltViewModel()
) {
    val estadoAlimento by viewModel.estadoAlimento.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Alimento") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val estado = estadoAlimento) {
                is EstadoUI.Cargando -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = VerdePrimario)
                }
                is EstadoUI.Exito -> {
                    ContenidoDetalle(
                        alimento = estado.datos,
                        onAgregar = {
                            viewModel.agregarHoy(estado.datos) {
                                onAlimentoAgregado()
                            }
                        }
                    )
                }
                is EstadoUI.Error -> {
                    Text(
                        text = estado.mensaje,
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> {}
            }
        }
    }
}

@Composable
fun ContenidoDetalle(alimento: Alimento, onAgregar: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = alimento.nombre,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = VerdePrimario
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                InfoNutricionalRow("Calorías", "${alimento.calorias} kcal")
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                InfoNutricionalRow("Proteínas", "${alimento.proteina}g")
                InfoNutricionalRow("Carbohidratos", "${alimento.carbohidratos}g")
                InfoNutricionalRow("Grasas", "${alimento.grasas}g")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        MiPlatoBoton(
            texto = "Agregar a mi plato de hoy",
            onClick = onAgregar
        )
    }
}

@Composable
fun InfoNutricionalRow(label: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(text = valor, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}
