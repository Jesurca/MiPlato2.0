package com.miplato.app.presentacion.componentes

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File

/**
 * Vista previa de cámara con CameraX y botón para guardar un JPEG en caché.
 */
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

    Column(modifier = modifier.fillMaxSize()) {
        if (!permisoConcedido) {
            Text(
                "Se necesita permiso de cámara para continuar.",
                modifier = Modifier.padding(16.dp)
            )
            return
        }

        AndroidView(
            factory = { ctx ->
                val vistaPrevia = PreviewView(ctx)
                val futuroProveedor = ProcessCameraProvider.getInstance(ctx)
                futuroProveedor.addListener({
                    val proveedor = futuroProveedor.get()
                    val captura = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()
                    capturaImagen = captura
                    val vistaCamara = androidx.camera.core.Preview.Builder().build().also {
                        it.surfaceProvider = vistaPrevia.surfaceProvider
                    }
                    try {
                        proveedor.unbindAll()
                        proveedor.bindToLifecycle(
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
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                val captura = capturaImagen ?: return@Button
                val archivo = File(contexto.cacheDir, "captura_miplato_${System.currentTimeMillis()}.jpg")
                val opciones = ImageCapture.OutputFileOptions.Builder(archivo).build()
                captura.takePicture(
                    opciones,
                    ejecutor,
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(resultado: ImageCapture.OutputFileResults) {
                            onFotoGuardada(archivo.absolutePath)
                        }

                        override fun onError(ex: ImageCaptureException) {
                            alDarError("Error al capturar: ${ex.message}")
                        }
                    }
                )
            },
            enabled = capturaImagen != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text("Capturar y analizar")
        }
    }
}
