package com.miplato.app.presentacion.pantallas

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.compose.ui.text.font.FontWeight
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.miplato.app.BuildConfig
import com.miplato.app.presentacion.componentes.MiPlatoBoton
import com.miplato.app.presentacion.componentes.MiPlatoCampoTexto
import com.miplato.app.presentacion.componentes.PantallaCarga
import com.miplato.app.presentacion.navegacion.Rutas
import com.miplato.app.presentacion.navegacion.navegarAlInicioTrasAutenticacion
import com.miplato.app.presentacion.theme.Mint
import com.miplato.app.presentacion.theme.OnDarkSurface
import com.miplato.app.presentacion.theme.TextGray
import com.miplato.app.presentacion.viewmodels.SesionViewModel

@Composable
fun PantallaLogin(
    navController: NavHostController,
    modeloSesion: SesionViewModel = hiltViewModel()
) {
    val estado by modeloSesion.estado.collectAsState()
    val contexto = LocalContext.current
    
    val clienteGoogle = remember(contexto) {
        val idWeb = BuildConfig.WEB_CLIENT_ID_GOOGLE
        if (idWeb.isBlank()) {
            null
        } else {
            val opciones = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(idWeb)
                .requestEmail()
                .build()
            GoogleSignIn.getClient(contexto, opciones)
        }
    }

    val lanzadorGoogle = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        val tarea = GoogleSignIn.getSignedInAccountFromIntent(resultado.data)
        try {
            val cuenta = tarea.getResult(ApiException::class.java)
            val token = cuenta?.idToken
            if (token != null) {
                modeloSesion.iniciarSesionConGoogle(token)
            } else {
                modeloSesion.mostrarError("No se recibió token de Google")
            }
        } catch (error: ApiException) {
            if (error.statusCode != 12501) { // 12501 es cancelación del usuario
                modeloSesion.mostrarError("Error de Google Sign-In: ${error.message}")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "MiPlato",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Black,
            color = Mint
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Tu nutrición inteligente",
            style = MaterialTheme.typography.bodyLarge,
            color = TextGray
        )
        
        Spacer(modifier = Modifier.height(48.dp))

        MiPlatoCampoTexto(
            valor = estado.correo,
            onValueChange = modeloSesion::actualizarCorreo,
            label = "Correo electrónico",
            placeholder = "ejemplo@correo.com",
            error = if (estado.error.contains("correo", ignoreCase = true)) estado.error else null
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        MiPlatoCampoTexto(
            valor = estado.clave,
            onValueChange = modeloSesion::actualizarClave,
            label = "Contraseña",
            placeholder = "••••••••",
            esPassword = true,
            error = if (estado.error.contains("contraseña", ignoreCase = true) || estado.error.contains("clave", ignoreCase = true)) estado.error else null
        )

        if (estado.error.isNotBlank() && !estado.error.contains("correo", ignoreCase = true) && !estado.error.contains("contraseña", ignoreCase = true)) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = estado.error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        MiPlatoBoton(
            texto = "INICIAR SESIÓN",
            onClick = modeloSesion::iniciarSesion,
            cargando = estado.cargando
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = {
                if (clienteGoogle == null) {
                    modeloSesion.mostrarError("Google Sign-In no configurado. Verifica local.properties")
                } else {
                    lanzadorGoogle.launch(clienteGoogle.signInIntent)
                }
            },
            enabled = !estado.cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("CONTINUAR CON GOOGLE", color = OnDarkSurface, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("¿No tienes cuenta?", color = TextGray)
            TextButton(onClick = { navController.navigate(Rutas.Registro) }) {
                Text("Regístrate aquí", color = Mint, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (estado.cargando) {
        PantallaCarga("Verificando credenciales...")
    }

    LaunchedEffect(estado.autenticado) {
        if (estado.autenticado) {
            navController.navegarAlInicioTrasAutenticacion()
        }
    }
}

@Composable
private fun Row(
    verticalAlignment: Alignment.Vertical,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.Row(
        verticalAlignment = verticalAlignment,
        content = { content() }
    )
}
