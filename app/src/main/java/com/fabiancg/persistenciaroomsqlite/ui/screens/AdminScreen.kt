package com.fabiancg.persistenciaroomsqlite.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fabiancg.persistenciaroomsqlite.AppDatabase
import com.fabiancg.persistenciaroomsqlite.DocumentoItem
import com.fabiancg.persistenciaroomsqlite.Usuario

/**
 * Pantalla de Panel de Administración (Modo Admin).
 * Requerimientos implementados:
 * 1. Acceso exclusivo solo para Docentes (alumnos no tienen acceso ni les aparece la opción).
 * 2. Registro y listado de personas separadas por pestañas independientes para Docentes y Alumnos.
 * 3. Listado de registro de cada archivo subido con su fecha correspondiente y detalles.
 */
@Composable
fun AdminScreen(
    usuarioActivo: Usuario?
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val usuarioDao = remember { db.usuarioDao() }
    val documentoDao = remember { db.documentoDao() }

    // Listas reactivas desde SQLite Room
    val listaUsuarios by usuarioDao.obtenerTodosLosUsuarios().collectAsState(initial = emptyList())
    val listaDocumentos by documentoDao.obtenerTodos().collectAsState(initial = emptyList())

    // 0 = Registro de Personas, 1 = Archivos Subidos
    var tabAdministracion by rememberSaveable { mutableIntStateOf(0) }

    // Sub-pestañas: 0 = Docentes, 1 = Alumnos, 2 = Todos
    var subTabPersonas by rememberSaveable { mutableIntStateOf(0) }

    val docentes = remember(listaUsuarios) { listaUsuarios.filter { it.rol.equals("Docente", ignoreCase = true) } }
    val alumnos = remember(listaUsuarios) { listaUsuarios.filter { it.rol.equals("Alumno", ignoreCase = true) || !it.rol.equals("Docente", ignoreCase = true) } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // ENCABEZADO DE MODO ADMIN
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.SupervisorAccount,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Modo Administración",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Panel exclusivo para Docentes • ESFOCAR",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SECTOR DE PESTAÑAS PRINCIPALES DEL MODO ADMIN
        TabRow(
            selectedTabIndex = tabAdministracion,
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Tab(
                selected = tabAdministracion == 0,
                onClick = { tabAdministracion = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Personas Registradas (${listaUsuarios.size})", fontWeight = FontWeight.Bold)
                    }
                }
            )
            Tab(
                selected = tabAdministracion == 1,
                onClick = { tabAdministracion = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.InsertDriveFile, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Archivos Subidos (${listaDocumentos.size})", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (tabAdministracion == 0) {
            // ========================================================
            // 1. REGISTRO DE PERSONAS SEPARADO PARA DOCENTES Y ALUMNOS
            // ========================================================

            // Tarjetas de Métricas Rápidas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Métricas Docentes
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.SupervisorAccount, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Docentes", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = "${docentes.size}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Métricas Alumnos
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Text("Alumnos", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = "${alumnos.size}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sub-Pestañas para listar personas por separado: Docentes / Alumnos / Todos
            TabRow(
                selectedTabIndex = subTabPersonas,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = subTabPersonas == 0,
                    onClick = { subTabPersonas = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SupervisorAccount, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Docentes (${docentes.size})", fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = subTabPersonas == 1,
                    onClick = { subTabPersonas = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Alumnos (${alumnos.size})", fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = subTabPersonas == 2,
                    onClick = { subTabPersonas = 2 },
                    text = { Text("Todos (${listaUsuarios.size})") }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            val usuariosAMostrar = when (subTabPersonas) {
                0 -> docentes
                1 -> alumnos
                else -> listaUsuarios
            }

            if (usuariosAMostrar.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (subTabPersonas) {
                            0 -> "No hay docentes registrados en la base de datos."
                            1 -> "No hay alumnos registrados en la base de datos."
                            else -> "No hay personas registradas."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(usuariosAMostrar, key = { it.id }) { u ->
                        ItemUsuarioAdmin(usuario = u)
                    }
                }
            }

        } else {
            // ========================================================
            // 2. LISTADO DE REGISTRO DE ARCHIVOS SUBIDOS CON SUS FECHAS
            // ========================================================
            Text(
                text = "Historial y Registro de Archivos Subidos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Registro en SQLite con fecha, tamaño y tipo de cada documento",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (listaDocumentos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aún no hay archivos registrados en la base de datos SQLite.",
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
                        ItemArchivoAdmin(documento = doc)
                    }
                }
            }
        }
    }
}

/**
 * Componente visual para cada usuario en la lista de administración.
 */
@Composable
private fun ItemUsuarioAdmin(usuario: Usuario) {
    val esDocente = usuario.rol.equals("Docente", ignoreCase = true)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (esDocente) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (esDocente) Icons.Default.SupervisorAccount else Icons.Default.School,
                contentDescription = null,
                tint = if (esDocente) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(38.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = usuario.nombre,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "[${usuario.rol}]",
                        fontWeight = FontWeight.Bold,
                        color = if (esDocente) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Correo: ${usuario.email}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "RUT: ${usuario.rut} • Escalafón: ${usuario.escalafon}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )

                if (usuario.fechaRegistro.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Fecha de Registro: ${usuario.fechaRegistro}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Componente visual para cada archivo subido con su fecha correspondiente.
 */
@Composable
private fun ItemArchivoAdmin(documento: DocumentoItem) {
    val esPdf = documento.tipo.equals("PDF", ignoreCase = true)
    val tamanoKb = documento.tamanoBytes / 1024

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
            Icon(
                imageVector = if (esPdf) Icons.Default.PictureAsPdf else Icons.Default.Description,
                contentDescription = null,
                tint = if (esPdf) Color(0xFFD32F2F) else Color(0xFF1976D2),
                modifier = Modifier.size(38.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = documento.nombre,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Tipo: ${documento.tipo} • Tamaño: ${tamanoKb} KB • Categoría: ${documento.categoria}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Fecha de subida: ${documento.fechaCarga}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
