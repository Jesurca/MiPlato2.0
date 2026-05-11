package com.miplato.app.presentacion.detalle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.miplato.app.presentacion.theme.Mint
import com.miplato.app.presentacion.theme.DarkSurface
import com.miplato.app.presentacion.theme.DarkSurfaceVariant
import com.miplato.app.presentacion.theme.TextGray
import com.miplato.app.presentacion.theme.OnDarkSurface
import com.miplato.app.presentacion.theme.WarningYellow
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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Alimento", color = OnDarkSurface) },
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
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val estado = estadoAlimento) {
                is EstadoUI.Cargando -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Mint)
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
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = Mint
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                InfoNutricionalRow("Calorías", "${alimento.calorias} kcal", colorValor = Mint)
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = DarkSurfaceVariant)
                InfoNutricionalRow("Proteínas", "${alimento.proteina}g", colorValor = WarningYellow)
                Spacer(modifier = Modifier.height(8.dp))
                InfoNutricionalRow("Carbohidratos", "${alimento.carbohidratos}g", colorValor = WarningYellow)
                Spacer(modifier = Modifier.height(8.dp))
                InfoNutricionalRow("Grasas", "${alimento.grasas}g", colorValor = WarningYellow)
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
fun InfoNutricionalRow(label: String, valor: String, colorValor: Color = OnDarkSurface) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = TextGray)
        Text(text = valor, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = colorValor)
    }
}
