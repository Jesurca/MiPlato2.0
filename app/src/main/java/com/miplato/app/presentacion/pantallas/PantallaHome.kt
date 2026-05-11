package com.miplato.app.presentacion.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.miplato.app.core.EstadoUi
import com.miplato.app.presentacion.componentes.BarraProgresoMacro
import com.miplato.app.presentacion.componentes.PantallaCarga
import com.miplato.app.presentacion.navegacion.Rutas
import com.miplato.app.presentacion.viewmodels.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHome(
    navController: NavHostController,
    modeloTablero: DashboardViewModel = hiltViewModel()
) {
    val estadoConsumo by modeloTablero.estadoConsumo.collectAsState()

    LaunchedEffect(Unit) {
        modeloTablero.cargarConsumoHoy()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "MiPlato",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    ) 
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Rutas.Perfil) }) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil")
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
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                "Resumen Diario",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            when (val actual = estadoConsumo) {
                is EstadoUi.Cargando -> PantallaCarga("Actualizando dashboard...")
                is EstadoUi.Error -> {
                    Text(
                        text = "Error al cargar datos: ${actual.mensaje}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is EstadoUi.Exito -> {
                    // Si datos es null, usamos un objeto vacío por defecto
                    val datos = actual.datos ?: com.miplato.app.dominio.ConsumoDiario("", "", 0, 0f, 0f, 0f)
                    
                    // Card del Dashboard Principal
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(
                                "Calorías Totales",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                "${datos.caloriasTotales} kcal",
                                style = MaterialTheme.typography.displayMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Macros con metas dinámicas (ejemplo de metas estándar)
                            BarraProgresoMacro("Proteína", datos.proteinaTotal, 150f, Color(0xFFFF9800))
                            BarraProgresoMacro("Carbohidratos", datos.carbohidratosTotal, 250f, Color(0xFF4CAF50))
                            BarraProgresoMacro("Grasas", datos.grasasTotales, 70f, Color(0xFF2196F3))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                "Acciones Rápidas",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Grid de acciones
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TarjetaAccion(
                    titulo = "Cámara IA",
                    icono = Icons.Default.PhotoCamera,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = { navController.navigate(Rutas.Camara) },
                    modifier = Modifier.weight(1f)
                )
                TarjetaAccion(
                    titulo = "Buscar",
                    icono = Icons.Default.Search,
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    onClick = { navController.navigate(Rutas.Busqueda) },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TarjetaAccion(
                    titulo = "Historial",
                    icono = Icons.Default.History,
                    color = Color(0xFFE8F5E9),
                    onClick = { navController.navigate(Rutas.Historial) },
                    modifier = Modifier.weight(1f)
                )
                TarjetaAccion(
                    titulo = "Planes",
                    icono = Icons.Default.Menu,
                    color = Color(0xFFFFF3E0),
                    onClick = { navController.navigate(Rutas.Planes) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun TarjetaAccion(
    titulo: String,
    icono: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(20.dp),
        color = color
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icono, contentDescription = null, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(titulo, style = MaterialTheme.typography.labelLarge)
        }
    }
}
