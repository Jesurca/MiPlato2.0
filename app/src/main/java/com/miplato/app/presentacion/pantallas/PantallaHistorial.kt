@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.miplato.app.presentacion.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.miplato.app.core.EstadoUi
import com.miplato.app.presentacion.navegacion.Rutas
import com.miplato.app.presentacion.theme.*
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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Mint),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Restaurant, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "MiPlato",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = OnDarkSurface
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = OnDarkSurface)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = Mint)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            MiPlatoBottomBar(navController)
        }
    ) { relleno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(relleno)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Historial",
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                color = OnDarkSurface
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            when (val muestra = estadoLista) {
                is EstadoUi.Cargando -> 
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Mint)
                    }
                is EstadoUi.Error ->
                    Text(
                        text = muestra.mensaje,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                is EstadoUi.Exito ->
                    if (muestra.datos.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No hay datos aún", color = TextGray)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 32.dp)
                        ) {
                            items(muestra.datos, key = { fila -> fila.id }) { alimento ->
                                ItemHistorialCard(
                                    nombre = alimento.nombre,
                                    calorias = alimento.calorias,
                                    onClick = {
                                        navController.navigate("${Rutas.DetalleAlimento}/${alimento.id}")
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
fun ItemHistorialCard(nombre: String, calorias: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Restaurant, contentDescription = null, tint = Mint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = nombre,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = OnDarkSurface
                )
                Text(
                    text = "$calorias kcal",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )
            }
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextGray)
    }
}
