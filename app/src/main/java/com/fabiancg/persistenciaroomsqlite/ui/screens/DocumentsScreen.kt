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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
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

/**
 * Pantalla de Gestión de Rúbricas y Documentos (PDF y Word).
 * Cumple con los requerimientos específicos:
 * 1. Cargar documentos
 * 2. Ver documentos (con visor nativo mediante FileProvider)
 * 3. Eliminar documento (con confirmación y borrado de SQLite)
 */
@Composable
fun DocumentsScreen() {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getDatabase(context).documentoDao() }
    val scope = rememberCoroutineScope()

    // Lista reactiva de documentos leídos desde Room SQLite
    val listaDocumentos by dao.obtenerTodos().collectAsState(initial = emptyList())

    // Estados para alertas y diálogos
    var documentoAEliminar by remember { mutableStateOf<DocumentoItem?>(null) }
    var mensajeAviso by remember { mutableStateOf<String?>(null) }
    var mostrarDialogoVisorNoEncontrado by remember { mutableStateOf(false) }

    // 1. CARGAR DOCUMENTOS: Selector nativo de Android
    val selectorArchivosLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val nuevoDoc = copiarArchivoASqLite(context, uri)
                if (nuevoDoc != null) {
                    dao.insertar(nuevoDoc)
                    mensajeAviso = "¡Archivo '${nuevoDoc.nombre}' cargado con éxito en SQLite!"
                } else {
                    mensajeAviso = "No se pudo cargar el archivo seleccionado."
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Rúbricas y Documentos",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Archivos nativos (PDF / Word) guardados en SQLite",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        // BOTONES DE ACCIÓN (CARGAR Y PRECARGAR RÚBRICAS)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    // Abre el explorador de archivos nativo para PDF y Word
                    selectorArchivosLauncher.launch(
                        arrayOf(
                            "application/pdf",
                            "application/msword",
                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                        )
                    )
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Cargar Archivo")
            }

            OutlinedButton(
                onClick = {
                    scope.launch {
                        precargarRubricasEjemplo(context, dao)
                        mensajeAviso = "¡Rúbricas de ejemplo creadas y guardadas en SQLite!"
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Cargar Rúbricas")
            }
        }

        // MENSAJE ANIMADO DE CONFIRMACIÓN
        AnimatedVisibility(
            visible = mensajeAviso != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = mensajeAviso ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { mensajeAviso = null }) {
                        Text("OK")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Documentos Guardados (${listaDocumentos.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // LISTADO DE DOCUMENTOS EN ROOM SQLITE
        if (listaDocumentos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aún no hay documentos cargados.\nPresione 'Cargar Archivo' o 'Cargar Rúbricas' para comenzar.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listaDocumentos, key = { it.id }) { doc ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Icono según tipo (PDF en rojo, Word en azul)
                            Icon(
                                imageVector = if (doc.tipo == "PDF") Icons.Default.PictureAsPdf else Icons.Default.Description,
                                contentDescription = null,
                                tint = if (doc.tipo == "PDF") Color(0xFFD32F2F) else Color(0xFF1976D2),
                                modifier = Modifier.size(36.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = doc.nombre,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Tipo: ${doc.tipo} | Fecha: ${doc.fechaCarga}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // 2. VER DOCUMENTO: Botón para abrir el visor nativo
                            IconButton(onClick = {
                                abrirDocumentoNativo(context, doc) {
                                    mostrarDialogoVisorNoEncontrado = true
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = "Ver documento",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            // 3. ELIMINAR DOCUMENTO: Botón para borrar de SQLite
                            IconButton(onClick = { documentoAEliminar = doc }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // DIÁLOGO DE CONFIRMACIÓN PARA ELIMINAR DOCUMENTO
    if (documentoAEliminar != null) {
        val doc = documentoAEliminar!!
        AlertDialog(
            onDismissRequest = { documentoAEliminar = null },
            title = { Text("Eliminar Documento") },
            text = { Text("¿Desea eliminar '${doc.nombre}' de la base de datos local SQLite?") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            // Borra el archivo físico del almacenamiento interno
                            val archivo = File(doc.rutaArchivo)
                            if (archivo.exists()) archivo.delete()

                            // Borra el registro de SQLite
                            dao.eliminarPorId(doc.id)
                            mensajeAviso = "Documento '${doc.nombre}' eliminado correctamente."
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

    // DIÁLOGO INFORMATIVO SI EL EMULADOR NO TIENE UN VISOR INSTALADO
    if (mostrarDialogoVisorNoEncontrado) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoVisorNoEncontrado = false },
            title = { Text("Apertura de Documento") },
            text = {
                Text("El archivo se encuentra guardado correctamente en la memoria interna privada de la aplicación.\n\nPara visualizarlo externamente se requiere una app lectora (como Adobe Reader o Google Drive Viewer en el teléfono).")
            },
            confirmButton = {
                Button(onClick = { mostrarDialogoVisorNoEncontrado = false }) {
                    Text("Entendido")
                }
            }
        )
    }
}

/**
 * Función que copia el archivo seleccionado a la carpeta interna privada de la app
 * y retorna el objeto DocumentoItem para guardarlo en Room SQLite.
 */
private suspend fun copiarArchivoASqLite(context: Context, uri: Uri): DocumentoItem? = withContext(Dispatchers.IO) {
    try {
        var nombreArchivo = "documento_${System.currentTimeMillis()}"
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nombreIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nombreIndex != -1) {
                nombreArchivo = cursor.getString(nombreIndex)
            }
        }

        val tipo = if (nombreArchivo.endsWith(".docx", ignoreCase = true) || nombreArchivo.endsWith(".doc", ignoreCase = true)) {
            "WORD"
        } else {
            "PDF"
        }

        val carpeta = File(context.filesDir, "documentos").apply { mkdirs() }
        val archivoLocal = File(carpeta, nombreArchivo)

        context.contentResolver.openInputStream(uri)?.use { entrada ->
            FileOutputStream(archivoLocal).use { salida ->
                entrada.copyTo(salida)
            }
        }

        val fechaStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

        DocumentoItem(
            nombre = nombreArchivo,
            tipo = tipo,
            tamanoBytes = archivoLocal.length(),
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
 * Abre el documento nativo usando Intent(ACTION_VIEW) y FileProvider.
 */
private fun abrirDocumentoNativo(context: Context, doc: DocumentoItem, onNoViewer: () -> Unit) {
    val archivo = File(doc.rutaArchivo)
    if (!archivo.exists()) {
        Toast.makeText(context, "El archivo no se encuentra en el almacenamiento.", Toast.LENGTH_SHORT).show()
        return
    }

    try {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
        val mimeType = if (doc.tipo == "PDF") "application/pdf" else "application/vnd.openxmlformats-officedocument.wordprocessingml.document"

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        onNoViewer()
    } catch (e: Exception) {
        onNoViewer()
    }
}

/**
 * Precarga 2 rúbricas de ejemplo para demostración rápida en clase ante el profesor.
 */
private suspend fun precargarRubricasEjemplo(
    context: Context,
    dao: com.fabiancg.persistenciaroomsqlite.DocumentoDao
) = withContext(Dispatchers.IO) {
    val carpeta = File(context.filesDir, "documentos").apply { mkdirs() }
    val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

    val ejemplos = listOf(
        Pair("Rubrica_Evaluacion_Tiro_Policial.pdf", "PDF"),
        Pair("Pauta_Detencion_en_Flagrancia.docx", "WORD")
    )

    for ((nombre, tipo) in ejemplos) {
        val archivo = File(carpeta, nombre)
        if (!archivo.exists()) {
            archivo.writeText("ESCUELA DE FORMACIÓN DE CARABINEROS\nRúbrica Oficial: $nombre\nTipo: $tipo\nFecha: $fecha\n\nDocumento institucional guardado en Room SQLite.")
        }
        val item = DocumentoItem(
            nombre = nombre,
            tipo = tipo,
            tamanoBytes = archivo.length(),
            rutaArchivo = archivo.absolutePath,
            fechaCarga = fecha,
            categoria = "Rúbrica Institucional"
        )
        dao.insertar(item)
    }
}
