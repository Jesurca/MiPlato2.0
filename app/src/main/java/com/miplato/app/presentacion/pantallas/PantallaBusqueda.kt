@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.miplato.app.presentacion.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.miplato.app.core.EstadoUi
import com.miplato.app.dominio.AlimentoSugerido
import com.miplato.app.presentacion.viewmodels.DashboardViewModel
import kotlinx.coroutines.launch

@Composable
fun PantallaBusqueda(
    navController: NavHostController,
    modeloTablero: DashboardViewModel = hiltViewModel()
) {
    val estadoBusqueda by modeloTablero.estadoBusqueda.collectAsState()
    val anfitrionSnack = remember { SnackbarHostState() }
    val ambitoCoroutine = rememberCoroutineScope()
    var consultaTexto by remember { mutableStateOf("") }

    DisposableEffect(Unit) {
        onDispose { modeloTablero.limpiarResultadoBusqueda() }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(anfitrionSnack) },
        topBar = {
            TopAppBar(
                title = { Text("Buscar Alimentos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
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
            OutlinedTextField(
                value = consultaTexto,
                onValueChange = { consultaTexto = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ej: Manzana, Pollo...") },
                label = { Text("Buscar alimento") },
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )
            
            Spacer(Modifier.height(12.dp))
            
            Button(
                onClick = { 
                    if (consultaTexto.isNotBlank()) {
                        modeloTablero.buscarAlimento(consultaTexto)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Buscar")
            }
            
            Spacer(Modifier.height(16.dp))

            when (val muestra = estadoBusqueda) {
                is EstadoUi.Cargando ->
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                is EstadoUi.Error -> {
                    Text(
                        text = muestra.mensaje,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    LaunchedEffect(muestra.mensaje) {
                        anfitrionSnack.showSnackbar(muestra.mensaje)
                    }
                }
                is EstadoUi.Exito -> {
                    if (muestra.datos.isEmpty() && consultaTexto.isNotBlank()) {
                        Text("No se encontraron resultados", modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                    
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(muestra.datos) { alimento ->
                            TarjetaAlimentoBusqueda(
                                alimento = alimento,
                                alAgregar = {
                                    modeloTablero.agregarDesdeSugerido(
                                        alimento.nombre,
                                        alimento.calorias,
                                        alimento.proteina,
                                        alimento.carbohidratos,
                                        alimento.grasas
                                    )
                                    ambitoCoroutine.launch {
                                        anfitrionSnack.showSnackbar("${alimento.nombre} agregado")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaAlimentoBusqueda(
    alimento: AlimentoSugerido,
    alAgregar: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(alimento.nombre, style = MaterialTheme.typography.titleMedium)
            Text("${alimento.calorias} kcal | P: ${alimento.proteina}g | C: ${alimento.carbohidratos}g | G: ${alimento.grasas}g", 
                 style = MaterialTheme.typography.bodySmall)
        }
        Button(
            onClick = alAgregar,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Añadir")
        }
    }
}
