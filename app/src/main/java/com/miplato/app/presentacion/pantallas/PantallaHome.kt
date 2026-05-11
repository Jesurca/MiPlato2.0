package com.miplato.app.presentacion.pantallas

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.miplato.app.R
import com.miplato.app.core.EstadoUi
import com.miplato.app.dominio.ConsumoDiario
import com.miplato.app.presentacion.navegacion.Rutas
import com.miplato.app.presentacion.theme.Mint
import com.miplato.app.presentacion.theme.DarkSurface
import com.miplato.app.presentacion.theme.TextGray
import com.miplato.app.presentacion.theme.DarkSurfaceVariant
import com.miplato.app.presentacion.theme.WarningYellow
import com.miplato.app.presentacion.theme.InfoBlue
import com.miplato.app.presentacion.viewmodels.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHome(
    navController: NavHostController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val estadoConsumo by viewModel.estadoConsumo.collectAsState()
    val nombreUsuario = "Juan" // Debería venir del ViewModel o un SessionManager

    LaunchedEffect(Unit) {
        viewModel.cargarConsumoHoy()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(DarkSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            // Avatar placeholder
                            Icon(
                                painter = painterResource(id = R.drawable.ic_splash_logo),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.Unspecified
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "MiPlato",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Hola, $nombreUsuario",
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                color = Mint
            )
            Text(
                text = "Tu rendimiento de hoy está en camino.",
                color = TextGray,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            when (val estado = estadoConsumo) {
                is EstadoUi.Exito -> {
                    val datos = estado.datos ?: ConsumoDiario("", "", 0, 0f, 0f, 0f)
                    DashboardMainCard(datos)
                }
                is EstadoUi.Cargando -> {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Mint)
                    }
                }
                is EstadoUi.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(DarkSurface)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Error al cargar datos", color = Color.White)
                            TextButton(onClick = { viewModel.cargarConsumoHoy() }) {
                                Text("REINTENTAR", color = Mint)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Recomendaciones",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            RecommendationItem(
                text = "Te faltan 30g de proteína hoy.",
                color = Mint
            )
            Spacer(modifier = Modifier.height(12.dp))
            RecommendationItem(
                text = "Bebe más agua. (1.5L / 3L)",
                color = WarningYellow
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { navController.navigate(Rutas.Camara) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Mint)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "ESCANEAR COMIDA",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = Color.Black
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun DashboardMainCard(datos: ConsumoDiario) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(DarkSurface)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Calorie Ring
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
                CircularProgress(
                    progress = (datos.caloriasTotales / 2000f).coerceIn(0f, 1f),
                    size = 180.dp
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${datos.caloriasTotales}",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "DE 2000 KCAL",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = TextGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MacroVerticalBar("PROTEÍNAS", datos.proteinaTotal, 130f)
                MacroVerticalBar("CARBOS", datos.carbohidratosTotal, 250f)
                MacroVerticalBar("GRASAS", datos.grasasTotales, 70f)
            }
        }
    }
}

@Composable
fun CircularProgress(progress: Float, size: androidx.compose.ui.unit.Dp) {
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "")
    Canvas(modifier = Modifier.size(size)) {
        drawArc(
            color = DarkSurfaceVariant,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
        )
        drawArc(
            color = Mint,
            startAngle = -90f,
            sweepAngle = 360f * animatedProgress,
            useCenter = false,
            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun MacroVerticalBar(label: String, current: Float, target: Float) {
    val progress = (current / target).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "")

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = Mint
        )
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .width(24.dp)
                .height(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(animatedProgress)
                    .align(Alignment.BottomCenter)
                    .background(Mint)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "${current.toInt()}g / ${target.toInt()}g",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White
        )
    }
}

@Composable
fun RecommendationItem(text: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(4.dp, 24.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Icon(
            painter = painterResource(id = if (color == Mint) R.drawable.ic_splash_logo else R.drawable.ic_splash_logo), // Placeholder
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = Color.White)
    }
}

@Composable
fun MiPlatoBottomBar(navController: NavHostController) {
    val items = listOf(
        Triple("Inicio", Rutas.Home, com.miplato.app.R.drawable.ic_splash_logo),
        Triple("Planes", Rutas.Planes, com.miplato.app.R.drawable.ic_splash_logo),
        Triple("Historial", Rutas.Historial, com.miplato.app.R.drawable.ic_splash_logo),
        Triple("Perfil", Rutas.Perfil, com.miplato.app.R.drawable.ic_splash_logo)
    )
    
    NavigationBar(
        containerColor = DarkBackground,
        tonalElevation = 0.dp
    ) {
        items.forEach { (label, route, icon) ->
            val actual = navController.currentBackStackEntryAsState().value?.destination?.route
            val selected = actual == route || (route == Rutas.Home && actual == null)
            NavigationBarItem(
                selected = selected,
                onClick = { navController.navigate(route) },
                label = { Text(label) },
                icon = { Icon(painterResource(id = icon), contentDescription = null, modifier = Modifier.size(24.dp)) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Mint,
                    selectedTextColor = Mint,
                    unselectedIconColor = TextGray,
                    unselectedTextColor = TextGray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

private val DarkBackground @Composable get() = MaterialTheme.colorScheme.background
