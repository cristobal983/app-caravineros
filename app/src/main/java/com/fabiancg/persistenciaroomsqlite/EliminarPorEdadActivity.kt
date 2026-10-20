package com.fabiancg.persistenciaroomsqlite

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fabiancg.persistenciaroomsqlite.ui.theme.PersistenciaRoomSQLiteTheme
import kotlinx.coroutines.launch

class EliminarPorEdadActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val dao = AppDatabase.getDatabase(applicationContext).personaDao()

        setContent {
            PersistenciaRoomSQLiteTheme {
                PantallaEliminarPorEdad(dao = dao)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEliminarPorEdad(dao: PersonaDAO) {
    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()

    var edadInput by remember { mutableStateOf("") }
    var mensajeResultado by remember { mutableStateOf("") }
    var mostrarAlerta by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Eliminar por Edad (Tarea)") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Tarea: Eliminación Condicional en SQLite",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Ingresa una edad para eliminar todos los registros de personas que coincidan con dicho valor en la base de datos.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            OutlinedTextField(
                value = edadInput,
                onValueChange = { edadInput = it },
                label = { Text("Edad a eliminar") },
                placeholder = { Text("Ej: 25") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = {
                    val edadNumero = edadInput.toIntOrNull()
                    if (edadNumero != null && edadNumero > 0) {
                        scope.launch {
                            // Ejecuta la consulta de eliminación por edad
                            val cantidad = dao.eliminarPorEdad(edadNumero)
                            mensajeResultado = if (cantidad > 0) {
                                "Se eliminaron $cantidad persona(s) con $edadNumero años."
                            } else {
                                "No se encontraron personas con $edadNumero años."
                            }
                            mostrarAlerta = true
                            edadInput = ""
                        }
                    } else {
                        mensajeResultado = "Por favor, ingrese una edad válida mayor a 0."
                        mostrarAlerta = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Eliminar Personas con esta Edad")
            }

            OutlinedButton(
                onClick = {
                    // Regresar a la actividad principal
                    (contexto as? Activity)?.finish()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al Menú Principal")
            }

            if (mensajeResultado.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(
                        text = mensajeResultado,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }

    if (mostrarAlerta) {
        AlertDialog(
            onDismissRequest = { mostrarAlerta = false },
            title = { Text("Resultado de Eliminación") },
            text = { Text(mensajeResultado) },
            confirmButton = {
                TextButton(onClick = { mostrarAlerta = false }) {
                    Text("Aceptar")
                }
            }
        )
    }
}