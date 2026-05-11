@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.miplato.app.presentacion.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.miplato.app.core.EstadoUi
import com.miplato.app.dominio.AlimentoSugerido
import com.miplato.app.presentacion.theme.Mint
import com.miplato.app.presentacion.theme.DarkSurfaceVariant
import com.miplato.app.presentacion.theme.TextGray
import com.miplato.app.presentacion.theme.OnDarkSurface
import com.miplato.app.presentacion.componentes.MiPlatoBoton
import com.miplato.app.presentacion.componentes.MiPlatoCampoTexto
import com.miplato.app.presentacion.theme.DarkSurface
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
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(anfitrionSnack) },
        topBar = {
            TopAppBar(
                title = { Text("Buscar Alimentos", color = OnDarkSurface) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = OnDarkSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { relleno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            
            MiPlatoCampoTexto(
                valor = consultaTexto,
                onValueChange = { consultaTexto = it },
                label = "Búsqueda",
                placeholder = "Ej: Manzana, Pollo...",
                leadingIcon = Icons.Default.Search
            )
            
            Spacer(Modifier.height(24.dp))
            
            MiPlatoBoton(
                texto = "BUSCAR",
                onClick = { 
                    if (consultaTexto.isNotBlank()) {
                        modeloTablero.buscarAlimento(consultaTexto)
                    }
                }
            )
            
            Spacer(Modifier.height(32.dp))

            when (val muestra = estadoBusqueda) {
                is EstadoUi.Cargando ->
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Mint)
                    }
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
                        Text(
                            "No se encontraron resultados", 
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            color = TextGray
                        )
                    }
                    
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(bottom = 24.dp)
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
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                alimento.nombre, 
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = OnDarkSurface
            )
            Text(
                "${alimento.calorias} kcal | P: ${alimento.proteina}g | C: ${alimento.carbohidratos}g | G: ${alimento.grasas}g", 
                style = MaterialTheme.typography.bodySmall,
                color = TextGray
            )
        }
        TextButton(
            onClick = alAgregar,
            colors = ButtonDefaults.textButtonColors(contentColor = Mint)
        ) {
            Text("AÑADIR", fontWeight = FontWeight.Bold)
        }
    }
}
