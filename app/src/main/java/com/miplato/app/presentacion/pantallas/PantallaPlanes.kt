package com.miplato.app.presentacion.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.miplato.app.presentacion.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPlanes(navController: NavHostController) {
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
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Planes",
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                color = OnDarkSurface
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            PlanCard(
                titulo = "Pérdida de Peso",
                descripcion = "Déficit calórico controlado con alto contenido proteico.",
                premium = false,
                color = Mint
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PlanCard(
                titulo = "Ganancia Muscular",
                descripcion = "Superávit calórico optimizado para hipertrofia.",
                premium = false,
                color = WarningYellow
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PlanCard(
                titulo = "Ayuno Intermitente",
                descripcion = "Protocolos 16/8 y 20/4 para flexibilidad metabólica.",
                premium = true,
                color = InfoBlue
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, Mint.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Mint, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "PÁSATE A PREMIUM",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = OnDarkSurface
                    )
                    Text(
                        "Desbloquea todos los planes y análisis avanzados de IA.",
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = TextGray,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun PlanCard(titulo: String, descripcion: String, premium: Boolean, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(DarkSurface)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = color
                )
                if (premium) {
                    Surface(
                        color = Mint.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "PREMIUM",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Mint
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("VER DETALLES", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = OnDarkSurface)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = OnDarkSurface, modifier = Modifier.size(16.dp))
            }
        }
    }
}
