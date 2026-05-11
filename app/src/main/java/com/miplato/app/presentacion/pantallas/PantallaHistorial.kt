@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.miplato.app.presentacion.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.miplato.app.core.EstadoUi
import com.miplato.app.presentacion.componentes.PantallaCarga
import com.miplato.app.presentacion.navegacion.Rutas
import com.miplato.app.presentacion.viewmodels.HistorialViewModel

@Composable
fun PantallaHistorial(
    navController: NavHostController,
    modeloHistorial: HistorialViewModel = hiltViewModel()
) {
    val estadoLista by modeloHistorial.lista.collectAsState()
    LaunchedEffect(Unit) {
        modeloHistorial.refrescarParaUsuarioActual()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { relleno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno)
                .padding(16.dp)
        ) {
            when (val muestra = estadoLista) {
                is EstadoUi.Cargando -> PantallaCarga("Cargando historial...")
                is EstadoUi.Error ->
                    Text(
                        text = muestra.mensaje,
                        color = MaterialTheme.colorScheme.error
                    )
                is EstadoUi.Exito ->
                    if (muestra.datos.isEmpty()) {
                        Text("No hay datos aún")
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(muestra.datos, key = { fila -> fila.id }) { alimentoIndividual ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF3F9F5), RoundedCornerShape(14.dp))
                                        .clickable {
                                            navController.navigate(
                                                "${Rutas.DetalleAlimento}/${alimentoIndividual.id}"
                                            )
                                        }
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            alimentoIndividual.nombre,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text("${alimentoIndividual.calorias} kcal")
                                    }
                                }
                            }
                        }
                    }
            }
        }
    }
}
