package com.fabiancg.persistenciaroomsqlite.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fabiancg.persistenciaroomsqlite.api.Feriado
import com.fabiancg.persistenciaroomsqlite.api.RetrofitClient
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Pantalla que consume la API externa de Feriados de Chile.
 *
 * Flujo:
 * 1. Al entrar, llama a la API con el año actual.
 * 2. Mientras carga muestra un indicador.
 * 3. Si hay error muestra el mensaje.
 * 4. Si tiene datos, los lista con Cards.
 *
 * Patrón: Estado reactivo con mutableStateOf + LaunchedEffect + corrutina.
 */
@Composable
fun FeriadosScreen() {
    val scope = rememberCoroutineScope()
    val anioActual = Calendar.getInstance().get(Calendar.YEAR)

    // Estados de UI
    var feriados by remember { mutableStateOf<List<Feriado>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Función para (re)cargar datos desde la API
    fun cargarFeriados() {
        scope.launch {
            cargando = true
            error = null
            try {
                feriados = RetrofitClient.feriadosApi.obtenerFeriados(anioActual)
            } catch (e: Exception) {
                error = "No se pudo conectar a la API.\nVerifica tu conexión a internet.\n\n${e.message}"
                feriados = emptyList()
            } finally {
                cargando = false
            }
        }
    }

    // Cargar automáticamente al entrar a la pantalla
    LaunchedEffect(Unit) { cargarFeriados() }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { cargarFeriados() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Recargar feriados",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {

            // Encabezado
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Feriados Nacionales $anioActual",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Fuente: API Feriados Chile (Nager.Date / Servicio Oficial)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            when {
                // Estado: cargando
                cargando -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Consultando API de Feriados...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Estado: error
                error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "⚠️ Error al conectar con la API",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = error ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }

                // Estado: datos cargados
                else -> {
                    Text(
                        text = "${feriados.size} feriados encontrados",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(feriados) { feriado ->
                            TarjetaFeriado(feriado = feriado)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Card individual para mostrar un feriado.
 */
@Composable
private fun TarjetaFeriado(feriado: Feriado) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = feriado.nombreSeguro,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Badge de tipo (Nacional / Regional / Civil)
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (feriado.tipo == "Nacional" || feriado.tipo == "Civil")
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text(
                        text = feriado.tipo,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = if (feriado.tipo == "Nacional" || feriado.tipo == "Civil")
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "📅 ${feriado.fechaSegura}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (!feriado.leyes.isNullOrEmpty()) {
                Text(
                    text = "Ley: ${feriado.leyes.joinToString(", ")}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (!feriado.nombreIngles.isNullOrBlank() && feriado.nombreIngles != feriado.nombreSeguro) {
                Text(
                    text = "Ref: ${feriado.nombreIngles}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
