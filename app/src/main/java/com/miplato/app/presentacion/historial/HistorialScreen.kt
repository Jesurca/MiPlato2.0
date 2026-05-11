package com.miplato.app.presentacion.historial

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.miplato.app.presentacion.componentes.ComponenteError
import com.miplato.app.presentacion.componentes.EstadoVacio
import com.miplato.app.presentacion.componentes.VerdePrimario
import com.miplato.app.presentacion.util.EstadoUI
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    onBack: () -> Unit,
    viewModel: HistorialViewModel = hiltViewModel()
) {
    val estadoHistorial by viewModel.estadoHistorial.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Historial") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        when (val estado = estadoHistorial) {
            is EstadoUI.Cargando -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = VerdePrimario)
                }
            }
            is EstadoUI.Exito -> {
                LazyColumn(modifier = Modifier.padding(padding)) {
                    items(estado.datos) { alimento ->
                        val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(alimento.fechaEpoch))
                        ListItem(
                            headlineContent = { Text(alimento.nombre, fontWeight = FontWeight.Bold) },
                            supportingContent = { Text("${alimento.calorias} kcal | $fecha") },
                            trailingContent = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "${alimento.proteina}g P",
                                        color = VerdePrimario,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = if (alimento.sincronizado) Icons.Default.CloudDone else Icons.Default.CloudUpload,
                                        contentDescription = if (alimento.sincronizado) "Sincronizado" else "Pendiente de subir",
                                        tint = if (alimento.sincronizado) VerdePrimario else MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
                    }
                }
            }
            is EstadoUI.Error -> {
                if (estado.mensaje.contains("Aún no", ignoreCase = true)) {
                    EstadoVacio(
                        mensaje = estado.mensaje,
                        icono = Icons.Default.History
                    )
                } else {
                    ComponenteError(
                        mensaje = estado.mensaje,
                        onReintentar = { /* El ViewModel ya observa el Flow, pero podemos forzar recarga si fuera necesario */ }
                    )
                }
            }
            else -> {}
        }
    }
}
