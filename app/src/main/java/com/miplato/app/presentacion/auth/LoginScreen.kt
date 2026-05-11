package com.miplato.app.presentacion.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.miplato.app.BuildConfig
import com.miplato.app.R
import com.miplato.app.presentacion.componentes.MiPlatoBoton
import com.miplato.app.presentacion.componentes.MiPlatoCampoTexto
import com.miplato.app.presentacion.componentes.VerdePrimario
import com.miplato.app.presentacion.util.EstadoUI

@Composable
fun LoginScreen(
    onNavegarAHome: () -> Unit,
    onNavegarARegistro: () -> Unit,
    viewModel: AutenticacionViewModel = hiltViewModel()
) {
    var correo by remember { mutableStateOf("") }
    var contra by remember { mutableStateOf("") }
    val estadoAuth by viewModel.estadoAuth.collectAsState()
    val contexto = LocalContext.current

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        val tarea = GoogleSignIn.getSignedInAccountFromIntent(resultado.data)
        try {
            val cuenta = tarea.getResult(ApiException::class.java)
            cuenta?.idToken?.let { token ->
                viewModel.iniciarSesionConGoogle(token)
            }
        } catch (e: ApiException) {
            // Manejar error de Google
        }
    }

    LaunchedEffect(estadoAuth) {
        if (estadoAuth is EstadoUI.Exito) {
            onNavegarAHome()
            viewModel.resetearEstado()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "MiPlato",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = VerdePrimario
        )
        Text(
            text = "Tu nutrición, bajo control",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.height(48.dp))

        MiPlatoCampoTexto(
            valor = correo,
            onValueChange = { correo = it },
            label = "Correo electrónico",
            error = if (estadoAuth is EstadoUI.Error) (estadoAuth as EstadoUI.Error).mensaje else null
        )

        Spacer(modifier = Modifier.height(16.dp))

        MiPlatoCampoTexto(
            valor = contra,
            onValueChange = { contra = it },
            label = "Contraseña",
            esPassword = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        MiPlatoBoton(
            texto = "Iniciar Sesión",
            onClick = { viewModel.iniciarSesion(correo, contra) },
            cargando = estadoAuth is EstadoUI.Cargando
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(BuildConfig.WEB_CLIENT_ID_GOOGLE)
                    .requestEmail()
                    .build()
                val cliente = GoogleSignIn.getClient(contexto, gso)
                googleSignInLauncher.launch(cliente.signInIntent)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = estadoAuth !is EstadoUI.Cargando
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Asegúrate de tener un icono de google en drawable
                Text("Continuar con Google")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavegarARegistro) {
            Text("¿No tienes cuenta? Regístrate aquí")
        }
    }
}
