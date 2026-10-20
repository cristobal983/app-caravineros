package com.fabiancg.persistenciaroomsqlite

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Pantalla que conserva el ejercicio didáctico de clase sobre:
 * 1. remember (persistencia temporal en recomposición)
 * 2. rememberSaveable (persistencia ante rotación / recreación)
 * 3. Room SQLite (persistencia permanente en base de datos)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoPersistenciaScreen(dao: PersonaDAO) {
    var pestañaSeleccionada by rememberSaveable { mutableIntStateOf(0) }
    val títulosPestañas = listOf("remember", "rememberSaveable", "Room SQLite")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Demostración de Persistencia")
                },
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
        ) {
            TabRow(
                selectedTabIndex = pestañaSeleccionada,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                títulosPestañas.forEachIndexed { index, titulo ->
                    Tab(
                        selected = pestañaSeleccionada == index,
                        onClick = { pestañaSeleccionada = index },
                        text = {
                            Text(
                                text = titulo,
                                fontWeight = if (pestañaSeleccionada == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (pestañaSeleccionada) {
                    0 -> ModuloRemember()
                    1 -> ModuloRememberSaveable()
                    2 -> ModuloRoom(dao = dao)
                }
            }
        }
    }
}

@Composable
fun ModuloRemember() {
    var numero by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "1. Persistencia Temporal: remember",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Guarda el estado en la memoria del Composable.\n" +
                            "• Se conserva durante recomposiciones simples.\n" +
                            "• Si giras el dispositivo, la Activity se recrea y el valor vuelve a 0.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Valor del Contador", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "$numero",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { numero++ }) {
                        Text("Sumar (+1)")
                    }
                    OutlinedButton(onClick = { numero = 0 }) {
                        Text("Reiniciar")
                    }
                }
            }
        }
    }
}

@Composable
fun ModuloRememberSaveable() {
    var numero by rememberSaveable { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "2. Persistencia ante Rotación: rememberSaveable",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Guarda el estado en el Bundle (SavedState) de Android.\n" +
                            "• Sobrevive a la recreación de la pantalla al girarla.\n" +
                            "• Solo se pierde al cerrar la app por completo.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Valor del Contador (Saveable)", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "$numero",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { numero++ }) {
                        Text("Sumar (+1)")
                    }
                    OutlinedButton(onClick = { numero = 0 }) {
                        Text("Reiniciar")
                    }
                }
            }
        }
    }
}

@Composable
fun ModuloRoom(dao: PersonaDAO) {
    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()

    var nombre by rememberSaveable { mutableStateOf("") }
    var edad by rememberSaveable { mutableStateOf("") }
    var resultado by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "3. Persistencia en Base de Datos: Room SQLite",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Almacena los datos en 'personas.db'. Los registros permanecen aunque se reinicie la app.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = edad,
            onValueChange = { edad = it },
            label = { Text("Edad") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    val edadNumero = edad.toIntOrNull()
                    if (nombre.isNotBlank() && edadNumero != null && edadNumero > 0) {
                        scope.launch {
                            dao.insertar(Persona(nombre = nombre.trim(), edad = edadNumero))
                            resultado = "Persona guardada: $nombre ($edadNumero años)"
                            nombre = ""
                            edad = ""
                        }
                    } else {
                        resultado = "Ingrese un nombre y edad válidos."
                    }
                }
            ) {
                Text("Guardar")
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    scope.launch {
                        val personas = dao.listar()
                        resultado = if (personas.isEmpty()) {
                            "No existen personas registradas."
                        } else {
                            "Personas registradas (${personas.size}):\n" +
                                    personas.joinToString("\n") { "• ${it.nombre} (${it.edad} años)" }
                        }
                    }
                }
            ) {
                Text("Listar")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                onClick = {
                    scope.launch {
                        dao.eliminarTodos()
                        nombre = ""
                        edad = ""
                        resultado = "Todos los registros eliminados."
                    }
                }
            ) {
                Text("Eliminar Todos")
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    val intent = Intent(contexto, EliminarPorEdadActivity::class.java)
                    contexto.startActivity(intent)
                }
            ) {
                Text("Eliminar x Edad ➔")
            }
        }

        if (resultado.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Resultado:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = resultado, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
