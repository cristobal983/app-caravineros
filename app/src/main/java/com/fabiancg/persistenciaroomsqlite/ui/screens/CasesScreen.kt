package com.fabiancg.persistenciaroomsqlite.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class DecisionCaso(
    val texto: String,
    val esOptima: Boolean,
    val justificacion: String
)

data class CasoSimulado(
    val id: Int,
    val codigo: String,
    val titulo: String,
    val situacion: String,
    val alternativas: List<DecisionCaso>
)

@Composable
fun CasesScreen() {
    val scrollState = rememberScrollState()

    // Esqueleto de caso práctico para rellenar con datos reales
    val casoActual = remember {
        CasoSimulado(
            id = 1,
            codigo = "CASO-01",
            titulo = "Caso Práctico 1",
            situacion = "Descripción de la situación o procedimiento a evaluar. Contenido pendiente de entrega por la institución.",
            alternativas = listOf(
                DecisionCaso(
                    texto = "Opción de procedimiento 1",
                    esOptima = true,
                    justificacion = "Fundamento de la decisión adecuada según normativa."
                ),
                DecisionCaso(
                    texto = "Opción de procedimiento 2",
                    esOptima = false,
                    justificacion = "Fundamento de por qué esta decisión no corresponde."
                ),
                DecisionCaso(
                    texto = "Opción de procedimiento 3",
                    esOptima = false,
                    justificacion = "Fundamento de por qué esta decisión no corresponde."
                )
            )
        )
    }

    var decisionElegida by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "Casos Prácticos",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Módulo de análisis y toma de decisiones.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "${casoActual.codigo}: ${casoActual.titulo}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = casoActual.situacion,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Seleccione una alternativa:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        casoActual.alternativas.forEachIndexed { index, opcion ->
            val seleccionada = decisionElegida == index

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                border = if (seleccionada) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                onClick = {
                    if (decisionElegida == null) {
                        decisionElegida = index
                    }
                }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = seleccionada,
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = opcion.texto,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (decisionElegida != null && (seleccionada || opcion.esOptima)) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (opcion.esOptima) "✔ Correcta: ${opcion.justificacion}" else "✖ ${opcion.justificacion}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if (decisionElegida != null) {
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = { decisionElegida = null },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reiniciar Caso")
            }
        }
    }
}
