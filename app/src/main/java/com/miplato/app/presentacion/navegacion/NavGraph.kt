package com.miplato.app.presentacion.navegacion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.miplato.app.presentacion.MainViewModel
import com.miplato.app.presentacion.auth.LoginScreen
import com.miplato.app.presentacion.auth.RegistroScreen
import com.miplato.app.presentacion.busqueda.BusquedaScreen
import com.miplato.app.presentacion.camara.CamaraScreen
import com.miplato.app.presentacion.camara.ResultadoIAScreen
import com.miplato.app.presentacion.detalle.DetalleAlimentoScreen
import com.miplato.app.presentacion.historial.HistorialScreen
import com.miplato.app.presentacion.home.HomeScreen
import com.miplato.app.presentacion.perfil.PerfilScreen
import com.miplato.app.presentacion.planes.PlanesScreen
import com.miplato.app.presentacion.splash.SplashScreen

@Composable
fun NavGraphMiPlato(
    navController: NavHostController,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val estaLogueado by mainViewModel.estaLogueado.collectAsState()
    val startDestination = if (estaLogueado) Destino.Home.ruta else Destino.Login.ruta

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 0. SPLASH (Punto de entrada) - Ya no es necesario como pantalla de Compose si usamos la de Android 12
        // pero la mantenemos por compatibilidad si el NavGraph la requiere o para transiciones personalizadas.
        composable(Destino.Splash.ruta) {
            val destino = if (estaLogueado) Destino.Home.ruta else Destino.Login.ruta
            navController.navigate(destino) {
                popUpTo(Destino.Splash.ruta) { inclusive = true }
            }
        }

        // 1. LOGIN
        composable(Destino.Login.ruta) {
            LoginScreen(
                onNavegarAHome = {
                    navController.navigate(Destino.Home.ruta) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavegarARegistro = { navController.navigate(Destino.Registro.ruta) }
            )
        }

        // 2. REGISTRO
        composable(Destino.Registro.ruta) {
            RegistroScreen(
                onNavegarAHome = {
                    navController.navigate(Destino.Home.ruta) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // 3. HOME (Dashboard Dinámico)
        composable(Destino.Home.ruta) {
            HomeScreen(
                onNavegarABusqueda = { navController.navigate(Destino.Busqueda.ruta) },
                onNavegarACamara = { navController.navigate(Destino.Camara.ruta) },
                onNavegarAPerfil = { navController.navigate(Destino.Perfil.ruta) }
            )
        }

        // 4. CÁMARA
        composable(Destino.Camara.ruta) {
            CamaraScreen(
                onAlimentoDetectado = { etiqueta ->
                    navController.navigate(Destino.ResultadoIA.crearRuta(etiqueta))
                },
                onBack = { navController.popBackStack() }
            )
        }

        // 5. RESULTADO IA
        composable(
            route = Destino.ResultadoIA.ruta,
            arguments = listOf(navArgument("etiqueta") { type = NavType.StringType })
        ) { backStackEntry ->
            val etiqueta = backStackEntry.arguments?.getString("etiqueta") ?: ""
            ResultadoIAScreen(
                etiqueta = etiqueta,
                onBack = { navController.popBackStack() },
                onAlimentoAgregado = {
                    navController.navigate(Destino.Home.ruta) {
                        popUpTo(Destino.Home.ruta) { inclusive = true }
                    }
                }
            )
        }

        // 6. BÚSQUEDA
        composable(Destino.Busqueda.ruta) {
            BusquedaScreen(
                onBack = { navController.popBackStack() },
                onAlimentoSeleccionado = { id -> 
                    navController.navigate(Destino.DetalleAlimento.crearRuta(id))
                }
            )
        }

        // 7. DETALLE ALIMENTO
        composable(
            route = Destino.DetalleAlimento.ruta,
            arguments = listOf(navArgument("alimentoId") { type = NavType.StringType })
        ) {
            DetalleAlimentoScreen(
                onBack = { navController.popBackStack() },
                onAlimentoAgregado = {
                    navController.navigate(Destino.Home.ruta) {
                        popUpTo(Destino.Home.ruta) { inclusive = true }
                    }
                }
            )
        }

        // 8. PERFIL (Logout robusto)
        composable(Destino.Perfil.ruta) {
            PerfilScreen(
                onCerrarSesion = {
                    navController.navigate(Destino.Login.ruta) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // 9. PLANES
        composable(Destino.Planes.ruta) {
            PlanesScreen(onBack = { navController.popBackStack() })
        }

        // 10. HISTORIAL
        composable(Destino.Historial.ruta) {
            HistorialScreen(onBack = { navController.popBackStack() })
        }
    }
}
