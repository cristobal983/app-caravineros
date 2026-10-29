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
 * 1. Registro completo de personas separadas por Docentes y Alumnos.
 * 2. Listado de registro de cada archivo subido por los docentes en SQLite.
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

    // Filtro para el registro de usuarios: "TODOS", "DOCENTE", "ALUMNO"
    var filtroRol by rememberSaveable { mutableIntStateOf(0) } // 0: Todos, 1: Docentes, 2: Alumnos

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
                modifier = Modifier.size(32.dp)
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
                    text = "Control de Usuarios y Registro de Archivos en SQLite",
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
                        Text("Personas (${listaUsuarios.size})", fontWeight = FontWeight.Bold)
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
                        Text("Archivos (${listaDocumentos.size})", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (tabAdministracion == 0) {
            // ========================================================
            // 1. REGISTRO DE PERSONAS SEPARADO POR DOCENTE Y ALUMNO
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

            Spacer(modifier = Modifier.height(10.dp))

            // Sub-Pestañas de Filtro: Todos / Docentes / Alumnos
            TabRow(
                selectedTabIndex = filtroRol,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(selected = filtroRol == 0, onClick = { filtroRol = 0 }, text = { Text("Todos (${listaUsuarios.size})") })
                Tab(selected = filtroRol == 1, onClick = { filtroRol = 1 }, text = { Text("Docentes (${docentes.size})") })
                Tab(selected = filtroRol == 2, onClick = { filtroRol = 2 }, text = { Text("Alumnos (${alumnos.size})") })
            }

            Spacer(modifier = Modifier.height(10.dp))

            val usuariosAMostrar = when (filtroRol) {
                1 -> docentes
                2 -> alumnos
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
                        text = "No se encontraron personas en este registro.",
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
            // 2. LISTADO DE REGISTRO DE ARCHIVOS SUBIDOS POR DOCENTES
            // ========================================================
            Text(
                text = "Historial y Registro de Archivos Cargados por Docentes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (listaDocumentos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aún no hay archivos o rúbricas registradas en la base de datos SQLite.",
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
                modifier = Modifier.size(36.dp)
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
                    text = "RUT: ${usuario.rut} | Correo: ${usuario.email}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Escalafón: ${usuario.escalafon}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

/**
 * Componente visual para cada archivo subido en la lista del registro de docentes.
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
                modifier = Modifier.size(36.dp)
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
                    text = "Tipo: ${documento.tipo} | Tamaño: ${tamanoKb} KB",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Fecha Carga: ${documento.fechaCarga} | Categoría: ${documento.categoria}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
