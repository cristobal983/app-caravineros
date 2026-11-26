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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.fabiancg.persistenciaroomsqlite.Usuario
import com.fabiancg.persistenciaroomsqlite.api.BackendClient
import com.fabiancg.persistenciaroomsqlite.api.Comunicado
import kotlinx.coroutines.launch

@Composable
fun ComunicadosScreen(usuarioActivo: Usuario?) {
    val scope = rememberCoroutineScope()
    val esDocente = usuarioActivo?.rol.equals("Docente", ignoreCase = true) ||
        usuarioActivo?.rol.equals("Admin", ignoreCase = true)

    var baseUrl by remember { mutableStateOf(BackendClient.DEFAULT_BASE_URL) }
    var comunicados by remember { mutableStateOf<List<Comunicado>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var errorConexion by remember { mutableStateOf<String?>(null) }

    var mostrarDialogoNuevo by remember { mutableStateOf(false) }
    var mostrarDialogoUrl by remember { mutableStateOf(false) }

    fun cargarComunicados() {
        scope.launch {
            cargando = true
            errorConexion = null
            try {
                comunicados = BackendClient.crearServicio(baseUrl).listar()
            } catch (e: Exception) {
                errorConexion = "No se pudo conectar con el servidor Spring Boot en $baseUrl.\n" +
                    "Asegurate de iniciar el backend en el puerto 8080."
                comunicados = emptyList()
            } finally {
                cargando = false
            }
        }
    }

    LaunchedEffect(baseUrl) {
        cargarComunicados()
    }

    Scaffold(
        floatingActionButton = {
            if (esDocente) {
                FloatingActionButton(
                    onClick = { mostrarDialogoNuevo = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Nuevo comunicado",
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
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Comunicados Institucionales",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Microservicio Spring Boot",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Row {
                    IconButton(onClick = { mostrarDialogoUrl = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configurar servidor"
                        )
                    }
                    IconButton(onClick = { cargarComunicados() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualizar"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when {
                cargando -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Conectando con Spring Boot...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                errorConexion != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Sin conexion con el backend",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = errorConexion ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = { cargarComunicados() }) {
                                        Text("Reintentar")
                                    }
                                    OutlinedButton(onClick = { mostrarDialogoUrl = true }) {
                                        Text("Cambiar URL")
                                    }
                                }
                            }
                        }
                    }
                }

                comunicados.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No hay comunicados registrados.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 84.dp)
                    ) {
                        items(comunicados, key = { it.id ?: it.hashCode().toLong() }) { item ->
                            TarjetaComunicado(
                                comunicado = item,
                                puedeEliminar = esDocente,
                                onEliminar = {
                                    val id = item.id ?: return@TarjetaComunicado
                                    scope.launch {
                                        try {
                                            val resp = BackendClient.crearServicio(baseUrl).eliminar(id)
                                            if (resp.isSuccessful) {
                                                cargarComunicados()
                                            }
                                        } catch (_: Exception) {
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogoNuevo) {
        DialogoNuevoComunicado(
            onDismiss = { mostrarDialogoNuevo = false },
            onGuardar = { titulo, detalle, unidad, prioridad ->
                scope.launch {
                    try {
                        BackendClient.crearServicio(baseUrl).crear(
                            Comunicado(
                                titulo = titulo,
                                detalle = detalle,
                                unidad = unidad,
                                prioridad = prioridad
                            )
                        )
                        mostrarDialogoNuevo = false
                        cargarComunicados()
                    } catch (e: Exception) {
                        mostrarDialogoNuevo = false
                        errorConexion = "No se pudo publicar el comunicado: ${e.message}"
                    }
                }
            }
        )
    }

    if (mostrarDialogoUrl) {
        DialogoConfigurarUrl(
            urlActual = baseUrl,
            onDismiss = { mostrarDialogoUrl = false },
            onGuardar = { nuevaUrl ->
                baseUrl = nuevaUrl.trim()
                mostrarDialogoUrl = false
            }
        )
    }
}

@Composable
private fun TarjetaComunicado(
    comunicado: Comunicado,
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
                Text(
                    text = comunicado.titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (comunicado.prioridad.equals("Alta", ignoreCase = true))
                            MaterialTheme.colorScheme.errorContainer
                        else
                            MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = comunicado.prioridad,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                if (puedeEliminar) {
                    IconButton(onClick = onEliminar) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar comunicado",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = comunicado.detalle,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${comunicado.unidad} • ${comunicado.fecha}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DialogoNuevoComunicado(
    onDismiss: () -> Unit,
    onGuardar: (titulo: String, detalle: String, unidad: String, prioridad: String) -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var detalle by remember { mutableStateOf("") }
    var unidad by remember { mutableStateOf("Dirección ESFOCAR") }
    var prioridad by remember { mutableStateOf("Normal") }
    var errorForm by remember { mutableStateOf<String?>(null) }

    val prioridades = listOf("Normal", "Alta")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Publicar comunicado", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = {
                        titulo = it
                        errorForm = null
                    },
                    label = { Text("Titulo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = detalle,
                    onValueChange = {
                        detalle = it
                        errorForm = null
                    },
                    label = { Text("Detalle") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = unidad,
                    onValueChange = { unidad = it },
                    label = { Text("Unidad emisora") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    prioridades.forEachIndexed { index, opcion ->
                        SegmentedButton(
                            selected = prioridad == opcion,
                            onClick = { prioridad = opcion },
                            shape = SegmentedButtonDefaults.itemShape(index, prioridades.size)
                        ) {
                            Text(opcion)
                        }
                    }
                }
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
                when {
                    titulo.isBlank() -> errorForm = "Ingresa un titulo"
                    detalle.isBlank() -> errorForm = "Ingresa el detalle del comunicado"
                    else -> onGuardar(titulo.trim(), detalle.trim(), unidad.trim(), prioridad)
                }
            }) {
                Text("Publicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun DialogoConfigurarUrl(
    urlActual: String,
    onDismiss: () -> Unit,
    onGuardar: (String) -> Unit
) {
    var url by remember { mutableStateOf(urlActual) }
    var errorUrl by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("URL del servidor Spring Boot") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "En emulador usa http://10.0.2.2:8080/ y en celular fisico la IP de tu PC en la red local.",
                    style = MaterialTheme.typography.bodySmall
                )
                OutlinedTextField(
                    value = url,
                    onValueChange = {
                        url = it
                        errorUrl = null
                    },
                    label = { Text("URL base") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                errorUrl?.let {
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
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    errorUrl = "Debe iniciar con http:// o https://"
                } else {
                    onGuardar(url)
                }
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
