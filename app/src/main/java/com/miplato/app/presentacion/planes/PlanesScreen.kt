package com.miplato.app.presentacion.planes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.miplato.app.presentacion.theme.Mint
import com.miplato.app.presentacion.theme.DarkSurface
import com.miplato.app.presentacion.theme.DarkSurfaceVariant
import com.miplato.app.presentacion.theme.TextGray
import com.miplato.app.presentacion.theme.OnDarkSurface
import com.miplato.app.presentacion.componentes.MiPlatoBoton

data class PlanNutricional(val titulo: String, val descripcion: String, val kcal: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanesScreen(onBack: () -> Unit) {
    val planes = listOf(
        PlanNutricional("Déficit Calórico", "Ideal para perder grasa manteniendo músculo.", "1800 kcal"),
        PlanNutricional("Mantenimiento", "Equilibrio perfecto para tu día a día.", "2200 kcal"),
        PlanNutricional("Volumen Limpio", "Aumenta tu masa muscular de forma controlada.", "2800 kcal")
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Planes de Nutrición", color = OnDarkSurface) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(planes) { plan ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.White.copy(alpha = 0.05f), MaterialTheme.shapes.large),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            plan.titulo,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Mint
                        )
                        Text(
                            plan.kcal,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = OnDarkSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            plan.descripcion,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextGray
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        MiPlatoBoton(
                            texto = "Activar Plan",
                            onClick = { /* Seleccionar plan */ }
                        )
                    }
                }
            }
        }
    }
}
