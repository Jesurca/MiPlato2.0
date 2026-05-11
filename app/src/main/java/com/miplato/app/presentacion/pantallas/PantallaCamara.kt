@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.miplato.app.presentacion.pantallas

import android.Manifest
import android.content.pm.PackageManager
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.miplato.app.presentacion.componentes.VistaCapturaCamara
import com.miplato.app.presentacion.navegacion.Rutas
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
        topBar = {
            TopAppBar(
                title = { Text("Cámara IA") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { relleno ->
        Column(modifier = Modifier.fillMaxSize().padding(relleno).padding(16.dp)) {
            Text(
                "Captura el plato. ML Kit detectará el alimento y consultaremos la nutrición en Spoonacular.",
                style = MaterialTheme.typography.bodyMedium
            )
            if (textoError.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = textoError,
                    color = MaterialTheme.colorScheme.error
                )
            }
            Spacer(Modifier.height(8.dp))
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
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
                Text("Volver")
            }
        }
    }
}
