package com.miplato.app.presentacion.planes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.miplato.app.presentacion.componentes.VerdePrimario

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
        topBar = { TopAppBar(title = { Text("Planes de Nutrición") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(planes) { plan ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = VerdePrimario.copy(alpha = 0.05f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(plan.titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = VerdePrimario)
                        Text(plan.kcal, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(plan.descripcion, style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { /* Seleccionar plan */ }, colors = ButtonDefaults.buttonColors(containerColor = VerdePrimario)) {
                            Text("Activar Plan")
                        }
                    }
                }
            }
        }
    }
}
