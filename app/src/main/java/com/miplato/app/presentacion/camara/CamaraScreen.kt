package com.miplato.app.presentacion.camara

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.miplato.app.presentacion.componentes.VerdePrimario
import com.miplato.app.presentacion.util.EstadoUI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CamaraScreen(
    onAlimentoDetectado: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: CamaraViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(LifecycleCameraController.IMAGE_CAPTURE)
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        }
    }
    
    val estadoIA by viewModel.estadoIA.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    var mostrarResultados by remember { mutableStateOf(false) }

    LaunchedEffect(estadoIA) {
        if (estadoIA is EstadoUI.Exito) {
            mostrarResultados = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView<PreviewView>(
            factory = {
                PreviewView(it).apply {
                    this.controller = controller
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        IconButton(
            onClick = {
                controller.takePicture(
                    ContextCompat.getMainExecutor(context),
                    object : androidx.camera.core.ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            val bitmap = image.toBitmap()
                            viewModel.analizarImagen(bitmap)
                            image.close()
                        }
                    }
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .size(80.dp)
        ) {
            Icon(
                Icons.Default.Camera,
                contentDescription = "Capturar",
                modifier = Modifier.size(64.dp),
                tint = Color.White
            )
        }
        
        if (estadoIA is EstadoUI.Cargando) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = VerdePrimario
            )
        }

        if (mostrarResultados && estadoIA is EstadoUI.Exito) {
            val labels = (estadoIA as EstadoUI.Exito<List<String>>).datos
            
            ModalBottomSheet(
                onDismissRequest = { 
                    mostrarResultados = false
                    viewModel.resetearEstado()
                },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                ) {
                    Text(
                        "Alimentos detectados",
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (labels.isEmpty()) {
                        Text(
                            "No se detectaron alimentos claros. Intenta de nuevo.",
                            modifier = Modifier.padding(24.dp),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    } else {
                        LazyColumn {
                            items(labels) { label ->
                                ListItem(
                                    headlineContent = { Text(label, fontSize = 18.sp) },
                                    leadingContent = { 
                                        Icon(Icons.Default.Restaurant, contentDescription = null, tint = VerdePrimario) 
                                    },
                                    modifier = Modifier.clickable {
                                        mostrarResultados = false
                                        onAlimentoDetectado(label)
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
