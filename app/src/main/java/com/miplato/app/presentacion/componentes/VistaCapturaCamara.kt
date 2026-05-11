package com.miplato.app.presentacion.componentes

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.miplato.app.presentacion.theme.Mint
import com.miplato.app.presentacion.theme.DarkSurface
import java.io.File
import android.net.Uri
import android.provider.MediaStore

@Composable
fun VistaCapturaCamara(
    permisoConcedido: Boolean,
    onFotoGuardada: (String) -> Unit,
    alDarError: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val contexto = LocalContext.current
    val propietarioCiclo = LocalLifecycleOwner.current
    val ejecutor = remember { ContextCompat.getMainExecutor(contexto) }
    var capturaImagen by remember { mutableStateOf<ImageCapture?>(null) }
    var camara by remember { mutableStateOf<Camera?>(null) }
    var flashEncendido by remember { mutableStateOf(false) }

    val lanzadorGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val ruta = obtenerRutaDesdeUri(contexto, it)
            if (ruta != null) {
                onFotoGuardada(ruta)
            } else {
                alDarError("No se pudo obtener la ruta de la imagen")
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (!permisoConcedido) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Se necesita permiso de cámara.", color = Color.White)
            }
        } else {
            AndroidView(
                factory = { ctx ->
                    val vistaPrevia = PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                    val futuroProveedor = ProcessCameraProvider.getInstance(ctx)
                    futuroProveedor.addListener({
                        val proveedor = futuroProveedor.get()
                        val captura = ImageCapture.Builder()
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                            .setFlashMode(if (flashEncendido) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF)
                            .build()
                        capturaImagen = captura
                        val vistaCamara = androidx.camera.core.Preview.Builder().build().also {
                            it.surfaceProvider = vistaPrevia.surfaceProvider
                        }
                        try {
                            proveedor.unbindAll()
                            camara = proveedor.bindToLifecycle(
                                propietarioCiclo,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                vistaCamara,
                                captura
                            )
                        } catch (e: Exception) {
                            alDarError("No se pudo iniciar la cámara: ${e.message}")
                        }
                    }, ejecutor)
                    vistaPrevia
                },
                modifier = Modifier.fillMaxSize(),
                update = {
                    capturaImagen?.flashMode = if (flashEncendido) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
                }
            )

            // Overlay Target Box
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .border(2.dp, Mint.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                )
                
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp)
                ) {
                    Text(
                        "Apunta tu cámara a la comida",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Bottom Controls
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp)
                    .padding(horizontal = 32.dp)
            ) {
                // Gallery button
                IconButton(
                    onClick = { lanzadorGaleria.launch("image/*") },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurface.copy(alpha = 0.8f))
                ) {
                    Icon(Icons.Default.Image, contentDescription = "Galería", tint = Color.White)
                }

                // Capture button
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(80.dp)
                        .border(4.dp, Mint, CircleShape)
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(Mint)
                        .clickable {
                            val captura = capturaImagen ?: return@clickable
                            val archivo = File(contexto.cacheDir, "captura_${System.currentTimeMillis()}.jpg")
                            val opciones = ImageCapture.OutputFileOptions.Builder(archivo).build()
                            captura.takePicture(
                                opciones,
                                ejecutor,
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(resultado: ImageCapture.OutputFileResults) {
                                        onFotoGuardada(archivo.absolutePath)
                                    }
                                    override fun onError(ex: ImageCaptureException) {
                                        alDarError("Error: ${ex.message}")
                                    }
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(id = com.miplato.app.R.drawable.ic_splash_logo),
                        contentDescription = "Capturar",
                        modifier = Modifier.size(32.dp),
                        tint = Color.Black
                    )
                }

                // Flash button
                IconButton(
                    onClick = { flashEncendido = !flashEncendido },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurface.copy(alpha = 0.8f))
                ) {
                    Icon(
                        if (flashEncendido) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Flash",
                        tint = if (flashEncendido) Mint else Color.White
                    )
                }
            }
        }
    }
}

private fun obtenerRutaDesdeUri(contexto: android.content.Context, uri: Uri): String? {
    var ruta: String? = null
    val proyeccion = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = contexto.contentResolver.query(uri, proyeccion, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val indiceColumna = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            ruta = it.getString(indiceColumna)
        }
    }
    return ruta ?: uri.path // Fallback simple
}
