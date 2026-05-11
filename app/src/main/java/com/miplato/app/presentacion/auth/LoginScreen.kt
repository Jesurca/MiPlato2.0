package com.miplato.app.presentacion.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.miplato.app.presentacion.theme.Mint
import com.miplato.app.presentacion.theme.DarkSurface
import com.miplato.app.presentacion.theme.TextGray
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

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                // Asumiendo que ic_splash_logo existe
                Icon(
                    painter = painterResource(id = R.drawable.ic_splash_logo),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "MiPlato",
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                color = Mint
            )

            Text(
                text = "Optimiza tu nutrición con la precisión de la ciencia.",
                textAlign = TextAlign.Center,
                color = TextGray,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Card Form
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(DarkSurface)
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = "Iniciar sesión",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    MiPlatoCampoTexto(
                        valor = correo,
                        onValueChange = { correo = it },
                        label = "Correo electrónico",
                        placeholder = "ejemplo@correo.com",
                        leadingIcon = Icons.Default.Email
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MiPlatoCampoTexto(
                        valor = contra,
                        onValueChange = { contra = it },
                        label = "Contraseña",
                        placeholder = "........",
                        esPassword = true,
                        leadingIcon = Icons.Default.Lock
                    )

                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        color = Mint,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 8.dp)
                            .clickable { /* TODO */ }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    MiPlatoBoton(
                        texto = "Iniciar sesión",
                        onClick = { viewModel.iniciarSesion(correo, contra) },
                        cargando = estadoAuth is EstadoUI.Cargando
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Social Login Divider
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.1f))
                        Text(
                            text = "O TAMBIÉN",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextGray,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.1f))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SocialButton(
                            icon = R.drawable.ic_google, // Asegúrate de tenerlo
                            text = "Google",
                            modifier = Modifier.weight(1f)
                        ) {
                            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(BuildConfig.WEB_CLIENT_ID_GOOGLE)
                                .requestEmail()
                                .build()
                            val cliente = GoogleSignIn.getClient(contexto, gso)
                            googleSignInLauncher.launch(cliente.signInIntent)
                        }
                        SocialButton(
                            icon = R.drawable.ic_apple, // Asegúrate de tenerlo
                            text = "Apple",
                            modifier = Modifier.weight(1f)
                        ) { /* TODO */ }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Row {
                Text(text = "¿No tienes cuenta aún? ", color = TextGray)
                Text(
                    text = "Registrarse",
                    color = Mint,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavegarARegistro() }
                )
            }
        }
    }
}

@Composable
fun SocialButton(
    icon: Int,
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.White,
            containerColor = Color.Transparent
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
        }
    }
}
