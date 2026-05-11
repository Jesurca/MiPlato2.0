package com.miplato.app.presentacion.busqueda

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.miplato.app.dominio.Alimento
import com.miplato.app.presentacion.theme.Mint
import com.miplato.app.presentacion.theme.DarkSurfaceVariant
import com.miplato.app.presentacion.theme.TextGray
import com.miplato.app.presentacion.theme.OnDarkSurface
import com.miplato.app.presentacion.componentes.MiPlatoBoton
import com.miplato.app.presentacion.componentes.MiPlatoCampoTexto
import com.miplato.app.presentacion.componentes.ComponenteError
import com.miplato.app.presentacion.componentes.EstadoVacio
import com.miplato.app.presentacion.util.EstadoUI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusquedaScreen(
    onBack: () -> Unit,
    onAlimentoSeleccionado: (String) -> Unit,
    viewModel: BusquedaViewModel = hiltViewModel()
) {
    var query by remember { mutableStateOf("") }
    val estadoBusqueda by viewModel.estadoBusqueda.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Buscar alimento...", color = TextGray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Mint,
                            focusedTextColor = OnDarkSurface,
                            unfocusedTextColor = OnDarkSurface
                        ),
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Mint) },
                        singleLine = true
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = OnDarkSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            MiPlatoBoton(
                texto = "Buscar en Spoonacular",
                onClick = { viewModel.buscarAlimentos(query) },
                modifier = Modifier.padding(16.dp)
            )

            when (val estado = estadoBusqueda) {
                is EstadoUI.Cargando -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Mint)
                    }
                }
                is EstadoUI.Exito -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(estado.datos) { alimento ->
                            ItemAlimentoBusqueda(
                                alimento = alimento,
                                onClick = { 
                                    viewModel.agregarAlimento(alimento) {
                                        onBack() 
                                    }
                                }
                            )
                        }
                    }
                }
                is EstadoUI.Error -> {
                    if (estado.mensaje.contains("encontraron", ignoreCase = true)) {
                        EstadoVacio(
                            mensaje = "No encontramos resultados para \"$query\".",
                            icono = Icons.Default.Search
                        )
                    } else {
                        ComponenteError(
                            mensaje = estado.mensaje,
                            esErrorRed = estado.mensaje.contains("conexión", ignoreCase = true),
                            onReintentar = { viewModel.buscarAlimentos(query) }
                        )
                    }
                }
                else -> {
                    if (query.isEmpty()) {
                        EstadoVacio(
                            mensaje = "Escribe el nombre de un alimento para buscar su información nutricional.",
                            icono = Icons.Default.Search
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemAlimentoBusqueda(alimento: Alimento, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(alimento.nombre, fontWeight = FontWeight.Bold, color = OnDarkSurface) },
        supportingContent = { 
            Text(
                "${alimento.calorias} kcal | P: ${alimento.proteina}g G: ${alimento.grasas}g",
                color = TextGray
            ) 
        },
        trailingContent = {
            TextButton(onClick = onClick) {
                Text("Agregar", color = Mint, fontWeight = FontWeight.Bold)
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier.clickable { onClick() }
    )
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp,
        color = DarkSurfaceVariant
    )
}
