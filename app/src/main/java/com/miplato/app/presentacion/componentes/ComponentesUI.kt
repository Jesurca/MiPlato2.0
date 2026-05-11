package com.miplato.app.presentacion.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miplato.app.presentacion.theme.Mint
import com.miplato.app.presentacion.theme.DarkSurfaceVariant
import com.miplato.app.presentacion.theme.TextGray

@Composable
fun MiPlatoBoton(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cargando: Boolean = false,
    color: Color = Mint,
    contentColor: Color = Color.Black
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = contentColor
        ),
        enabled = !cargando
    ) {
        if (cargando) {
            CircularProgressIndicator(color = contentColor, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
        } else {
            Text(
                text = texto,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            )
        }
    }
}

@Composable
fun MiPlatoCampoTexto(
    valor: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    esPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    error: String? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = OnSurfaceVariant
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = valor,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextGray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkSurfaceVariant,
                unfocusedContainerColor = DarkSurfaceVariant,
                focusedBorderColor = Mint.copy(alpha = 0.5f),
                unfocusedBorderColor = Color.Transparent,
                cursorColor = Mint
            ),
            leadingIcon = leadingIcon?.let {
                { Icon(it, contentDescription = null, tint = TextGray) }
            },
            trailingIcon = if (esPassword) {
                {
                    val imagen = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = imagen, contentDescription = null, tint = TextGray)
                    }
                }
            } else null,
            visualTransformation = if (esPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError = error != null,
            singleLine = true
        )
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}

private val OnSurfaceVariant @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
