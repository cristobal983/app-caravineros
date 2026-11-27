package com.fabiancg.persistenciaroomsqlite.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fabiancg.persistenciaroomsqlite.AppDatabase
import com.fabiancg.persistenciaroomsqlite.Usuario
import com.fabiancg.persistenciaroomsqlite.logic.AsistenteIALogic
import com.fabiancg.persistenciaroomsqlite.logic.ContextoAsistente

private data class MensajeChat(
    val id: Long,
    val texto: String,
    val esUsuario: Boolean
)

@Composable
fun AsistenteIAScreen(usuarioActivo: Usuario?) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }

    val documentos by db.documentoDao().obtenerTodos().collectAsState(initial = emptyList())
    val alertasNoLeidas by db.alertaDao().contarNoLeidas().collectAsState(initial = 0)
    val retroalimentaciones by db.retroalimentacionDao().obtenerTodas().collectAsState(initial = emptyList())

    val contextoActual = remember(usuarioActivo, documentos, alertasNoLeidas, retroalimentaciones) {
        ContextoAsistente(
            nombreUsuario = usuarioActivo?.nombre ?: "Usuario",
            rolUsuario = usuarioActivo?.rol ?: "Alumno",
            totalDocumentos = documentos.size,
            alertasSinLeer = alertasNoLeidas,
            totalRetroalimentaciones = retroalimentaciones.size,
            ultimoDocumento = documentos.firstOrNull()?.nombre,
            ultimaEvaluacion = retroalimentaciones.firstOrNull()?.let { "${it.asignatura} - ${it.evaluacion}" }
        )
    }

    val mensajes = remember {
        mutableStateListOf(
            MensajeChat(
                id = 1L,
                texto = "Hola ${usuarioActivo?.nombre ?: "Usuario"}, soy el Asistente IA de ESFOCAR. " +
                    "Preguntame sobre rubricas, alertas, retroalimentacion de pruebas, feriados o comunicados.",
                esUsuario = false
            )
        )
    }

    var textoConsulta by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val sugerencias = listOf(
        "¿Que alertas tengo?",
        "Resumen de documentos",
        "Ver retroalimentacion",
        "Proximos feriados",
        "Comunicados Spring Boot"
    )

    fun enviarConsulta(consulta: String) {
        val limpia = consulta.trim()
        if (limpia.isBlank()) return
        val baseId = System.currentTimeMillis()
        mensajes.add(MensajeChat(id = baseId, texto = limpia, esUsuario = true))
        val respuesta = AsistenteIALogic.generarRespuesta(limpia, contextoActual)
        mensajes.add(MensajeChat(id = baseId + 1, texto = respuesta, esUsuario = false))
        textoConsulta = ""
    }

    LaunchedEffect(mensajes.size) {
        if (mensajes.isNotEmpty()) {
            listState.animateScrollToItem(mensajes.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Asistente IA ESFOCAR",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Consultas academicas y estado del sistema",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            sugerencias.forEach { sugerencia ->
                AssistChip(
                    onClick = { enviarConsulta(sugerencia) },
                    label = { Text(sugerencia) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(mensajes, key = { it.id }) { msg ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (msg.esUsuario) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (msg.esUsuario)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.widthIn(max = 300.dp)
                    ) {
                        Text(
                            text = msg.texto,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (msg.esUsuario)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textoConsulta,
                onValueChange = { textoConsulta = it },
                placeholder = { Text("Escribe tu consulta...") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilledIconButton(
                onClick = { enviarConsulta(textoConsulta) },
                enabled = textoConsulta.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Enviar consulta"
                )
            }
        }
    }
}
