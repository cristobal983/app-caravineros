package com.fabiancg.persistenciaroomsqlite.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fabiancg.persistenciaroomsqlite.AppDatabase
import com.fabiancg.persistenciaroomsqlite.IntentoEvaluacion
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PreguntaMock(
    val id: Int,
    val materia: String,
    val enunciado: String,
    val opciones: List<String>,
    val indiceCorrecto: Int,
    val retroalimentacion: String
)

@Composable
fun QuizScreen() {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getDatabase(context).intentoEvaluacionDao() }

    // Preguntas plantilla de prueba para el esqueleto
    val preguntas = remember {
        listOf(
            PreguntaMock(
                id = 1,
                materia = "Módulo 1",
                enunciado = "¿Pregunta 1 de prueba?",
                opciones = listOf("Opción A", "Opción B", "Opción C", "Opción D"),
                indiceCorrecto = 0,
                retroalimentacion = "Respuesta correcta: Opción A."
            ),
            PreguntaMock(
                id = 2,
                materia = "Módulo 2",
                enunciado = "¿Pregunta 2 de prueba?",
                opciones = listOf("Opción A", "Opción B", "Opción C", "Opción D"),
                indiceCorrecto = 1,
                retroalimentacion = "Respuesta correcta: Opción B."
            ),
            PreguntaMock(
                id = 3,
                materia = "Módulo 3",
                enunciado = "¿Pregunta 3 de prueba?",
                opciones = listOf("Opción A", "Opción B", "Opción C", "Opción D"),
                indiceCorrecto = 2,
                retroalimentacion = "Respuesta correcta: Opción C."
            )
        )
    }

    var indiceActual by remember { mutableIntStateOf(0) }
    var opcionSeleccionada by remember { mutableStateOf<Int?>(null) }
    var aciertosCount by remember { mutableIntStateOf(0) }
    var triviaCompletada by remember { mutableStateOf(false) }
    var guardadoEnDb by remember { mutableStateOf(false) }

    val preguntaActual = preguntas[indiceActual]
    val progreso = (indiceActual + 1).toFloat() / preguntas.size

    val scrollState = rememberScrollState()

    // Guardar resultado en Room SQLite al completar
    LaunchedEffect(triviaCompletada) {
        if (triviaCompletada && !guardadoEnDb) {
            val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            val porcentaje = ((aciertosCount.toFloat() / preguntas.size) * 100).toInt()

            dao.insertar(
                IntentoEvaluacion(
                    materia = "Evaluación General",
                    fecha = fecha,
                    aciertos = aciertosCount,
                    total = preguntas.size,
                    porcentaje = porcentaje
                )
            )
            guardadoEnDb = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "Autoevaluación",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Cuestionario de preguntas de prueba.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (triviaCompletada) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Resultados de la Evaluación",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Respuestas correctas: $aciertosCount de ${preguntas.size}",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    val porcentaje = (aciertosCount.toFloat() / preguntas.size) * 100
                    Text(
                        text = "Puntaje: ${porcentaje.toInt()}%",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (guardadoEnDb) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Resultado guardado en la base de datos.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            indiceActual = 0
                            opcionSeleccionada = null
                            aciertosCount = 0
                            guardadoEnDb = false
                            triviaCompletada = false
                        }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reiniciar")
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pregunta ${indiceActual + 1} de ${preguntas.size}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = preguntaActual.materia,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progreso },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = preguntaActual.enunciado,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            preguntaActual.opciones.forEachIndexed { index, opcion ->
                val esSeleccionada = opcionSeleccionada == index

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = if (esSeleccionada) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                    onClick = {
                        if (opcionSeleccionada == null) {
                            opcionSeleccionada = index
                            if (index == preguntaActual.indiceCorrecto) {
                                aciertosCount++
                            }
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = esSeleccionada,
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = opcion,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            if (opcionSeleccionada != null) {
                Spacer(modifier = Modifier.height(14.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        val esCorrecto = opcionSeleccionada == preguntaActual.indiceCorrecto
                        Text(
                            text = if (esCorrecto) "Correcto" else "Incorrecto",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = preguntaActual.retroalimentacion,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (indiceActual + 1 < preguntas.size) {
                            indiceActual++
                            opcionSeleccionada = null
                        } else {
                            triviaCompletada = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (indiceActual + 1 < preguntas.size) "Siguiente" else "Finalizar")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}
