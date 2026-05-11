@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.miplato.app.presentacion

import android.util.Base64
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.miplato.app.presentacion.navegacion.Rutas
import com.miplato.app.presentacion.pantallas.*
import com.miplato.app.presentacion.theme.TemaMiPlato
import kotlin.text.Charsets

@Composable
fun AplicacionMiPlatoContenido() {
    TemaMiPlato {
        val controladorNavegacion = rememberNavController()
        NavHost(
            navController = controladorNavegacion,
            startDestination = Rutas.Splash
        ) {
            composable(Rutas.Splash) { PantallaSplash(controladorNavegacion) }
            composable(Rutas.Login) { PantallaLogin(controladorNavegacion) }
            composable(Rutas.Registro) { PantallaRegistro(controladorNavegacion) }
            composable(Rutas.Home) { PantallaHome(controladorNavegacion) }
            composable(Rutas.Camara) { PantallaCamara(controladorNavegacion) }
            composable(
                route = "${Rutas.ResultadoIa}/{rutaCodificada}",
                arguments =
                    listOf(
                        navArgument("rutaCodificada") {
                            type = NavType.StringType
                        }
                    )
            ) { entrada ->
                val codificada =
                    entrada.arguments?.getString("rutaCodificada").orEmpty()
                val ruta = decodificarRutaNavegacion(codificada)
                PantallaResultadoIa(controladorNavegacion, ruta)
            }
            composable(
                route = "${Rutas.DetalleAlimento}/{alimentoId}",
                arguments =
                    listOf(
                        navArgument("alimentoId") {
                            type = NavType.StringType
                        }
                    )
            ) { entrada ->
                val id = entrada.arguments?.getString("alimentoId").orEmpty()
                PantallaDetalleAlimento(controladorNavegacion, id)
            }
            composable(Rutas.Busqueda) { PantallaBusqueda(controladorNavegacion) }
            composable(Rutas.Planes) { PantallaPlanes(controladorNavegacion) }
            composable(Rutas.Historial) { PantallaHistorial(controladorNavegacion) }
            composable(Rutas.Perfil) { PantallaPerfil(controladorNavegacion) }
        }
    }
}

private fun decodificarRutaNavegacion(codificada: String): String {
    if (codificada.isBlank()) return ""
    return try {
        String(
            Base64.decode(codificada, Base64.URL_SAFE or Base64.NO_WRAP),
            Charsets.UTF_8
        )
    } catch (_: IllegalArgumentException) {
        codificada
    }
}
