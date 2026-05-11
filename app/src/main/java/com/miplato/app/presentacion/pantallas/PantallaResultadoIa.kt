@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.miplato.app.presentacion.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.miplato.app.core.EstadoUi
import com.miplato.app.presentacion.navegacion.Rutas
import com.miplato.app.presentacion.viewmodels.DashboardViewModel

@Composable
fun PantallaResultadoIa(
    navController: NavHostController,
    rutaImagen: String,
    modeloTablero: DashboardViewModel = hiltViewModel()
) {
    LaunchedEffect(rutaImagen) {
        if (rutaImagen.isNotBlank()) modeloTablero.procesarImagenIa(rutaImagen)
    }
    val estadoIa by modeloTablero.estadoIa.collectAsState()
    
    DisposableEffect(Unit) {
        onDispose { modeloTablero.limpiarResultadoIa() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resultado IA") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { relleno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (rutaImagen.isBlank()) {
                Text(
                    "No hay imagen para analizar",
                    color = MaterialTheme.colorScheme.error
                )
            }

            when (val muestra = estadoIa) {
                is EstadoUi.Cargando ->
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                is EstadoUi.Error ->
                    Text(
                        text = muestra.mensaje,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth()
                    )
                is EstadoUi.Exito -> {
                    val dato = muestra.datos
                    if (dato.nombre.isBlank() && dato.calorias == 0) {
                        Text("No se detectó ningún alimento claro.")
                    } else {
                        CardInformacionNutricional(
                            nombre = dato.nombre,
                            calorias = dato.calorias,
                            proteina = dato.proteina,
                            carbos = dato.carbohidratos,
                            grasas = dato.grasas
                        )
                        
                        Button(
                            onClick = {
                                modeloTablero.agregarDesdeSugerido(
                                    dato.nombre,
                                    dato.calorias,
                                    dato.proteina,
                                    dato.carbohidratos,
                                    dato.grasas
                                )
                                navController.navigate(Rutas.Home) {
                                    popUpTo(Rutas.Home) { inclusive = true }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Agregar a mi día")
                        }
                    }
                }
            }

            Button(
                onClick = { navController.navigate(Rutas.Home) {
                    popUpTo(Rutas.Home) { inclusive = true }
                } },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors()
            ) {
                Text("Descartar e ir al inicio")
            }
        }
    }
}

@Composable
private fun CardInformacionNutricional(
    nombre: String,
    calorias: Int,
    proteina: Float,
    carbos: Float,
    grasas: Float
) {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = nombre.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(Modifier.height(12.dp))
            Text("🔥 Calorías: $calorias kcal")
            Text("🥩 Proteínas: ${proteina}g")
            Text("🍞 Carbohidratos: ${carbos}g")
            Text("🥑 Grasas: ${grasas}g")
        }
    }
}
