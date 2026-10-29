package com.fabiancg.persistenciaroomsqlite.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fabiancg.persistenciaroomsqlite.R
import kotlinx.coroutines.delay

/**
 * Pantalla de Carga Inicial (Splash Screen).
 * Muestra el Escudo Oficial de ESFOCAR con una animación suave de escala
 * antes de dirigir al usuario a la pantalla de Inicio de Sesión o al Dashboard.
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val scale = remember { Animatable(0.4f) }

    LaunchedEffect(key1 = true) {
        // Animación suave de crecimiento del escudo
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
        // Tiempo de espera visible
        delay(1000)
        // Concluye la carga inicial
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B5313)), // Verde Institucional Carabineros / ESFOCAR
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Escudo oficial ESFOCAR animado
            Image(
                painter = painterResource(id = R.drawable.escudo_esfocar),
                contentDescription = "Escudo Oficial ESFOCAR",
                modifier = Modifier
                    .size(160.dp)
                    .scale(scale.value)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "ESFOCAR",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD700) // Amarillo Dorado
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Escuela de Formación de Carabineros",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )

            Text(
                text = "Sistema Académico de Rúbricas Policiales",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Indicador de carga
            CircularProgressIndicator(
                color = Color(0xFFFFD700),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
