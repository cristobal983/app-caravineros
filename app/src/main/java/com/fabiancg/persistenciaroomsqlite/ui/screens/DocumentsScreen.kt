package com.fabiancg.persistenciaroomsqlite.ui.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.fabiancg.persistenciaroomsqlite.AppDatabase
import com.fabiancg.persistenciaroomsqlite.DocumentoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DocumentsScreen() {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getDatabase(context).documentoDao() }
    val scope = rememberCoroutineScope()

    val listaDocumentos by dao.obtenerTodos().collectAsState(initial = emptyList())
    val totalDocumentos by dao.contarTotal().collectAsState(initial = 0)

    var documentoAEliminar by remember { mutableStateOf<DocumentoItem?>(null) }
    var documentoVisualizando by remember { mutableStateOf<DocumentoItem?>(null) }
    var mensajeNotificacion by remember { mutableStateOf<String?>(null) }

    // Launcher nativo del sistema operativo para seleccionar archivos (PDF, Word, etc.)
    val selectorArchivosLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val resultado = importarArchivoNativo(context, uri)
                if (resultado != null) {
                    dao.insertar(resultado)
                    mensajeNotificacion = "Documento '${resultado.nombre}' cargado y guardado en SQLite."
                } else {
                    mensajeNotificacion = "No fue posible procesar el archivo seleccionado."
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título y descripción
        Text(
            text = "Rúbricas y Documentos Nativos",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Gestión de archivos institucionales (PDF y Word) con persistencia local.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Botones de acción: Cargar documento nativo y precargar ejemplos para evaluación
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    // Abrir selector nativo de archivos del sistema
                    val tiposMime = arrayOf(
                        "application/pdf",
                        "application/msword",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        "*/*"
                    )
                    selectorArchivosLauncher.launch(tiposMime)
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Cargar Archivo")
            }

            OutlinedButton(
                onClick = {
                    scope.launch {
                        precargarDocumentosEjemplo(context, dao)
                        mensajeNotificacion = "Rúbricas de ejemplo generadas exitosamente."
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Cargar Rúbricas")
            }
        }

        // Banner informativo / feedback
        AnimatedVisibility(
            visible = mensajeNotificacion != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = mensajeNotificacion ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { mensajeNotificacion = null }) {
                        Text("✕", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Contador de documentos
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Documentos almacenados ($totalDocumentos)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (listaDocumentos.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.InsertDriveFile,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No hay documentos cargados",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Pulsa 'Cargar Archivo' para seleccionar un PDF o Word desde tu dispositivo, o 'Cargar Rúbricas' para generar archivos de prueba.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(listaDocumentos, key = { it.id }) { doc ->
                    DocumentoCard(
                        documento = doc,
                        onVisualizar = {
                            visualizarDocumentoNativo(context, doc) {
                                // Fallback dialog si el dispositivo no tiene app asociada
                                documentoVisualizando = doc
                            }
                        },
                        onEliminar = {
                            documentoAEliminar = doc
                        }
                    )
                }
            }
        }
    }

    // Diálogo de Confirmación para Eliminar Documento
    if (documentoAEliminar != null) {
        val doc = documentoAEliminar!!
        AlertDialog(
            onDismissRequest = { documentoAEliminar = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text("Eliminar Documento")
            },
            text = {
                Text("¿Está seguro de que desea eliminar permanentemente el documento '${doc.nombre}' del almacenamiento y de la base de datos?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            // 1. Eliminar archivo físico
                            val archivo = File(doc.rutaArchivo)
                            if (archivo.exists()) {
                                archivo.delete()
                            }
                            // 2. Eliminar registro de SQLite Room
                            dao.eliminarPorId(doc.id)
                            mensajeNotificacion = "Documento eliminado correctamente."
                            documentoAEliminar = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { documentoAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo informativo de previsualización (cuando el emulador no tiene app de Word/PDF instalada)
    if (documentoVisualizando != null) {
        val doc = documentoVisualizando!!
        AlertDialog(
            onDismissRequest = { documentoVisualizando = null },
            icon = {
                Icon(
                    imageVector = if (doc.tipo == "PDF") Icons.Default.PictureAsPdf else Icons.Default.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(doc.nombre)
            },
            text = {
                Column {
                    Text("Detalles del archivo nativo:", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• Formato: ${doc.tipo}")
                    Text("• Tamaño: ${doc.tamanoBytes / 1024} KB")
                    Text("• Fecha de Carga: ${doc.fechaCarga}")
                    Text("• Categoría: ${doc.categoria}")
                    Text("• Ubicación Local: ${doc.rutaArchivo}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Nota: Este archivo fue compartido mediante FileProvider nativo de Android. Para abrirlo directamente se requiere una app lectora instalada en el dispositivo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { documentoVisualizando = null }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
private fun DocumentoCard(
    documento: DocumentoItem,
    onVisualizar: () -> Unit,
    onEliminar: () -> Unit
) {
    val esPdf = documento.tipo.contains("PDF", ignoreCase = true)
    val colorIcono = if (esPdf) Color(0xFFC62828) else Color(0xFF1565C0)
    val icono = if (esPdf) Icons.Default.PictureAsPdf else Icons.Default.Description

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onVisualizar() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(colorIcono.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = colorIcono,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = documento.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${documento.categoria} • ${documento.tamanoBytes / 1024} KB",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Cargado: ${documento.fechaCarga}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            IconButton(onClick = onVisualizar) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = "Visualizar documento",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = onEliminar) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar documento",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * Importa un archivo seleccionado con el selector nativo y lo guarda de forma permanente
 * en context.filesDir para garantizar acceso offline y compatibilidad con FileProvider.
 */
private suspend fun importarArchivoNativo(context: Context, uri: Uri): DocumentoItem? = withContext(Dispatchers.IO) {
    try {
        var nombreArchivo = "documento_${System.currentTimeMillis()}"
        var tamano: Long = 0

        // Obtener nombre y tamaño reales a través de ContentResolver
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nombreIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val tamanoIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (cursor.moveToFirst()) {
                if (nombreIndex != -1) nombreArchivo = cursor.getString(nombreIndex)
                if (tamanoIndex != -1) tamano = cursor.getLong(tamanoIndex)
            }
        }

        val tipo = when {
            nombreArchivo.endsWith(".pdf", ignoreCase = true) -> "PDF"
            nombreArchivo.endsWith(".doc", ignoreCase = true) || nombreArchivo.endsWith(".docx", ignoreCase = true) -> "WORD"
            else -> "DOCUMENTO"
        }

        // Copiar a almacenamiento interno privado
        val carpetaDestino = File(context.filesDir, "rubricas").apply { mkdirs() }
        val archivoLocal = File(carpetaDestino, "${System.currentTimeMillis()}_$nombreArchivo")

        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(archivoLocal).use { output ->
                input.copyTo(output)
            }
        }

        if (tamano == 0L) {
            tamano = archivoLocal.length()
        }

        val fechaStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

        DocumentoItem(
            nombre = nombreArchivo,
            tipo = tipo,
            tamanoBytes = tamano,
            rutaArchivo = archivoLocal.absolutePath,
            fechaCarga = fechaStr,
            categoria = "Rúbrica Institucional"
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Lanza un Intent nativo (ACTION_VIEW) para que el visor predeterminado del sistema
 * (Adobe Reader, Google Drive PDF Viewer, Microsoft Word, etc.) abra el archivo.
 */
private fun visualizarDocumentoNativo(
    context: Context,
    documento: DocumentoItem,
    onNoViewerFound: () -> Unit
) {
    val archivo = File(documento.rutaArchivo)
    if (!archivo.exists()) {
        Toast.makeText(context, "El archivo no se encuentra en el almacenamiento local.", Toast.LENGTH_SHORT).show()
        return
    }

    try {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            archivo
        )

        val mimeType = when (documento.tipo) {
            "PDF" -> "application/pdf"
            "WORD" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            else -> "*/*"
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        // En emuladores sin visor de PDF o Word instalado
        onNoViewerFound()
    } catch (e: Exception) {
        onNoViewerFound()
    }
}

/**
 * Genera archivos de ejemplo (PDF y Word) en context.filesDir y los guarda en SQLite
 * para que el profesor/docente pueda probar de inmediato la funcionalidad sin requerir archivos previos.
 */
private suspend fun precargarDocumentosEjemplo(
    context: Context,
    dao: com.fabiancg.persistenciaroomsqlite.DocumentoDao
) = withContext(Dispatchers.IO) {
    val carpeta = File(context.filesDir, "rubricas").apply { mkdirs() }
    val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

    val ejemplos = listOf(
        Triple("Rubrica_Evaluacion_Tiro_Policial.pdf", "PDF", "Rúbrica Oficial de Tiro y Precaución Armamento"),
        Triple("Guia_Procedimiento_Detencion_Flagrante.docx", "WORD", "Guía Técnica de Procedimientos Policiales"),
        Triple("Reglamento_Disciplinario_ESFOCAR.pdf", "PDF", "Reglamento Interno Institucional")
    )

    for ((nombre, tipo, cat) in ejemplos) {
        val archivo = File(carpeta, nombre)
        if (!archivo.exists()) {
            archivo.writeText("ESCUELA DE FORMACIÓN DE CARABINEROS\nDocumento: $nombre\nTipo: $tipo\nFecha: $fecha\n\nEste es un documento nativo generado para pruebas y evaluación de la Asignatura DSY1105.")
        }
        val item = DocumentoItem(
            nombre = nombre,
            tipo = tipo,
            tamanoBytes = archivo.length(),
            rutaArchivo = archivo.absolutePath,
            fechaCarga = fecha,
            categoria = cat
        )
        dao.insertar(item)
    }
}
