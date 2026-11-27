package com.fabiancg.persistenciaroomsqlite.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import com.fabiancg.persistenciaroomsqlite.AlertaItem
import com.fabiancg.persistenciaroomsqlite.AppDatabase
import com.fabiancg.persistenciaroomsqlite.NotificacionHelper
import com.fabiancg.persistenciaroomsqlite.RetroalimentacionItem
import com.fabiancg.persistenciaroomsqlite.Usuario
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AlertasScreen(usuarioActivo: Usuario?) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val alertaDao = remember { db.alertaDao() }
    val retroDao = remember { db.retroalimentacionDao() }
    val scope = rememberCoroutineScope()

    val esDocente = remember(usuarioActivo) {
        usuarioActivo?.rol.equals("Docente", ignoreCase = true) ||
            usuarioActivo?.rol.equals("Admin", ignoreCase = true)
    }

    val alertas by alertaDao.obtenerTodas().collectAsState(initial = emptyList())
    val retroalimentaciones by retroDao.obtenerTodas().collectAsState(initial = emptyList())
    val noLeidas by alertaDao.contarNoLeidas().collectAsState(initial = 0)

    var tabSeleccionado by rememberSaveable { mutableIntStateOf(0) }
    var mostrarDialogoRetro by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            if (esDocente && tabSeleccionado == 1) {
                FloatingActionButton(
                    onClick = { mostrarDialogoRetro = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Publicar retroalimentacion",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Alertas y Evaluaciones",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Avisos compartidos para Docentes y Alumnos",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (tabSeleccionado == 0 && noLeidas > 0) {
                    TextButton(
                        onClick = {
                            scope.launch { alertaDao.marcarTodasComoLeidas() }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Marcar leidas")
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            PrimaryTabRow(selectedTabIndex = tabSeleccionado) {
                Tab(
                    selected = tabSeleccionado == 0,
                    onClick = { tabSeleccionado = 0 },
                    text = { Text("Alertas ($noLeidas)") }
                )
                Tab(
                    selected = tabSeleccionado == 1,
                    onClick = { tabSeleccionado = 1 },
                    text = { Text("Retroalimentacion (${retroalimentaciones.size})") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (tabSeleccionado == 0) {
                if (alertas.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No hay alertas registradas aun.\nSe generaran automaticamente al subir documentos o publicar retroalimentacion.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(alertas, key = { it.id }) { alerta ->
                            TarjetaAlerta(
                                alerta = alerta,
                                puedeEliminar = esDocente,
                                onMarcarLeida = {
                                    if (!alerta.leida) {
                                        scope.launch { alertaDao.marcarComoLeida(alerta.id) }
                                    }
                                },
                                onEliminar = {
                                    scope.launch { alertaDao.eliminarPorId(alerta.id) }
                                }
                            )
                        }
                    }
                }
            } else {
                if (esDocente) {
                    Button(
                        onClick = { mostrarDialogoRetro = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Publicar Retroalimentacion de Prueba")
                    }
                }

                if (retroalimentaciones.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (esDocente)
                                "Aun no has publicado retroalimentaciones de pruebas."
                            else
                                "Aun no hay retroalimentaciones publicadas por los docentes.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 84.dp)
                    ) {
                        items(retroalimentaciones, key = { it.id }) { item ->
                            TarjetaRetroalimentacion(
                                item = item,
                                puedeEliminar = esDocente,
                                onEliminar = {
                                    scope.launch { retroDao.eliminarPorId(item.id) }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogoRetro) {
        DialogoNuevaRetroalimentacion(
            onDismiss = { mostrarDialogoRetro = false },
            onGuardar = { asignatura, evaluacion, alumnoObjetivo, nota, comentario ->
                scope.launch {
                    val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                    val nombreDocente = usuarioActivo?.nombre ?: "Docente ESFOCAR"

                    retroDao.insertar(
                        RetroalimentacionItem(
                            asignatura = asignatura,
                            evaluacion = evaluacion,
                            alumnoObjetivo = alumnoObjetivo,
                            nota = nota,
                            comentario = comentario,
                            docenteAutor = nombreDocente,
                            fecha = fechaActual
                        )
                    )

                    val tituloAlerta = "Retroalimentacion disponible: $evaluacion"
                    val mensajeAlerta = "El docente $nombreDocente publico la retroalimentacion de $asignatura " +
                        "($alumnoObjetivo) - Nota: $nota. Revisala en Evaluaciones."

                    alertaDao.insertar(
                        AlertaItem(
                            titulo = tituloAlerta,
                            mensaje = mensajeAlerta,
                            tipo = "RETROALIMENTACION",
                            autor = nombreDocente,
                            destinatario = "Docentes y Alumnos",
                            fecha = fechaActual,
                            leida = false
                        )
                    )

                    NotificacionHelper.mostrarNotificacion(context, tituloAlerta, mensajeAlerta)
                    mostrarDialogoRetro = false
                }
            }
        )
    }
}

@Composable
private fun TarjetaAlerta(
    alerta: AlertaItem,
    puedeEliminar: Boolean,
    onMarcarLeida: () -> Unit,
    onEliminar: () -> Unit
) {
    val icono = when (alerta.tipo) {
        "DOCUMENTO" -> Icons.Default.Description
        "RETROALIMENTACION" -> Icons.Default.AssignmentTurnedIn
        else -> if (alerta.leida) Icons.Default.Notifications else Icons.Default.NotificationsActive
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMarcarLeida() },
        colors = CardDefaults.cardColors(
            containerColor = if (alerta.leida)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (alerta.leida) 1.dp else 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alerta.titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (alerta.leida) FontWeight.SemiBold else FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = alerta.mensaje,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Para: ${alerta.destinatario} • ${alerta.fecha}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (puedeEliminar) {
                IconButton(onClick = onEliminar) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar alerta",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaRetroalimentacion(
    item: RetroalimentacionItem,
    puedeEliminar: Boolean,
    onEliminar: () -> Unit
) {
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${item.asignatura} - ${item.evaluacion}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Dirigido a: ${item.alumnoObjetivo}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "Nota: ${item.nota}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                if (puedeEliminar) {
                    IconButton(onClick = onEliminar) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar retroalimentacion",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.comentario,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Docente: ${item.docenteAutor} • ${item.fecha}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DialogoNuevaRetroalimentacion(
    onDismiss: () -> Unit,
    onGuardar: (asignatura: String, evaluacion: String, alumnoObjetivo: String, nota: String, comentario: String) -> Unit
) {
    var asignatura by remember { mutableStateOf("DSY1105") }
    var evaluacion by remember { mutableStateOf("") }
    var alumnoObjetivo by remember { mutableStateOf("Todos los Alumnos") }
    var nota by remember { mutableStateOf("") }
    var comentario by remember { mutableStateOf("") }
    var errorForm by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Publicar Retroalimentacion", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = asignatura,
                    onValueChange = { asignatura = it; errorForm = null },
                    label = { Text("Asignatura") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = evaluacion,
                    onValueChange = { evaluacion = it; errorForm = null },
                    label = { Text("Evaluacion (ej: Prueba 3)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = alumnoObjetivo,
                    onValueChange = { alumnoObjetivo = it },
                    label = { Text("Alumno o Seccion") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = nota,
                    onValueChange = { nota = it; errorForm = null },
                    label = { Text("Nota (ej: 6.5)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = comentario,
                    onValueChange = { comentario = it; errorForm = null },
                    label = { Text("Comentario de retroalimentacion") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                errorForm?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val notaNum = nota.replace(',', '.').toDoubleOrNull()
                when {
                    asignatura.isBlank() -> errorForm = "Ingresa la asignatura"
                    evaluacion.isBlank() -> errorForm = "Ingresa el nombre de la evaluacion"
                    notaNum == null || notaNum < 1.0 || notaNum > 7.0 ->
                        errorForm = "Ingresa una nota valida entre 1.0 y 7.0"
                    comentario.isBlank() -> errorForm = "Ingresa el comentario"
                    else -> onGuardar(
                        asignatura.trim(),
                        evaluacion.trim(),
                        alumnoObjetivo.ifBlank { "Todos los Alumnos" }.trim(),
                        nota.trim(),
                        comentario.trim()
                    )
                }
            }) {
                Text("Publicar y Alertar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
