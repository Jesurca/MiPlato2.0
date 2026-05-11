package com.miplato.app.presentacion.camara

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.miplato.app.presentacion.busqueda.BusquedaViewModel
import com.miplato.app.presentacion.busqueda.ItemAlimentoBusqueda
import com.miplato.app.presentacion.componentes.VerdePrimario
import com.miplato.app.presentacion.util.EstadoUI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultadoIAScreen(
    etiqueta: String,
    onBack: () -> Unit,
    onAlimentoAgregado: () -> Unit,
    viewModel: BusquedaViewModel = hiltViewModel()
) {
    val estadoBusqueda by viewModel.estadoBusqueda.collectAsState()

    LaunchedEffect(etiqueta) {
        viewModel.buscarAlimentos(etiqueta)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resultado de IA") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text(
                text = "Detectamos: $etiqueta",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = VerdePrimario
            )
            Text(
                text = "Selecciona el que mejor se adapte:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val estado = estadoBusqueda) {
                is EstadoUI.Cargando -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = VerdePrimario)
                    }
                }
                is EstadoUI.Exito -> {
                    LazyColumn {
                        items(estado.datos) { alimento ->
                            ItemAlimentoBusqueda(
                                alimento = alimento,
                                onClick = {
                                    viewModel.agregarAlimento(alimento) {
                                        onAlimentoAgregado()
                                    }
                                }
                            )
                        }
                    }
                }
                is EstadoUI.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(estado.mensaje)
                    }
                }
                else -> {}
            }
        }
    }
}
