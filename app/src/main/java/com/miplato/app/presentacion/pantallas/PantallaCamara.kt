@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.miplato.app.presentacion.pantallas

import android.Manifest
import android.content.pm.PackageManager
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.miplato.app.presentacion.componentes.VistaCapturaCamara
import com.miplato.app.presentacion.navegacion.Rutas
import com.miplato.app.presentacion.theme.Mint
import com.miplato.app.presentacion.theme.OnDarkSurface
import kotlin.text.Charsets

@Composable
fun PantallaCamara(navController: NavHostController) {
    val contexto = LocalContext.current
    var permisoOtorgado by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(contexto, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var textoError by remember { mutableStateOf("") }
    val lanzadorPermiso =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { permisoOtorgado = it }

    LaunchedEffect(Unit) {
        if (!permisoOtorgado) lanzadorPermiso.launch(Manifest.permission.CAMERA)
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { relleno ->
        Box(modifier = Modifier.fillMaxSize().padding(relleno)) {
            VistaCapturaCamara(
                permisoConcedido = permisoOtorgado,
                onFotoGuardada = { rutaAbsoluta ->
                    val codigo =
                        Base64.encodeToString(
                            rutaAbsoluta.toByteArray(Charsets.UTF_8),
                            Base64.URL_SAFE or Base64.NO_WRAP
                        )
                    navController.navigate("${Rutas.ResultadoIa}/$codigo")
                },
                alDarError = { textoError = it },
                modifier = Modifier.fillMaxSize()
            )
            
            if (textoError.isNotBlank()) {
                Surface(
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.TopCenter).padding(16.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = textoError,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}
