package com.miplato.app.presentacion.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.miplato.app.presentacion.componentes.VerdePrimario
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigate: (Boolean) -> Unit) {
    val scale = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = interimSpec()
        )
        delay(1500L)
        val estaLogueado = FirebaseAuth.getInstance().currentUser != null
        onNavigate(estaLogueado)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "MiPlato",
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                color = VerdePrimario,
                modifier = Modifier.scale(scale.value)
            )
            Text(
                text = "Cuidando de ti",
                fontSize = 16.sp,
                color = VerdePrimario.copy(alpha = 0.6f)
            )
        }
    }
}

private fun interimSpec(): AnimationSpec<Float> = spring(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessLow
)
