@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.miplato.app.presentacion.pantallas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.miplato.app.core.EstadoUi
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
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Alimento") },
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
        Column(modifier = Modifier.fillMaxSize().padding(relleno).padding(16.dp)) {
            when (val muestra = estadoDetalle) {
                is EstadoUi.Cargando ->
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                is EstadoUi.Error ->
                    Text(text = muestra.mensaje, color = MaterialTheme.colorScheme.error)
                is EstadoUi.Exito ->
                    Column {
                        Text(
                            muestra.datos.nombre,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(16.dp))
                        
                        InformacionNutricionalFila("Calorías", "${muestra.datos.calorias} kcal")
                        InformacionNutricionalFila("Proteína", "${muestra.datos.proteina} g")
                        InformacionNutricionalFila("Carbohidratos", "${muestra.datos.carbohidratos} g")
                        InformacionNutricionalFila("Grasas", "${muestra.datos.grasas} g")
                    }
            }
        }
    }
}

@Composable
private fun InformacionNutricionalFila(etiqueta: String, valor: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = etiqueta, style = MaterialTheme.typography.labelLarge)
        Text(text = valor, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(8.dp))
    }
}
