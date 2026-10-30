package com.fabiancg.persistenciaroomsqlite.ui.screens

import android.net.Uri
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.fabiancg.persistenciaroomsqlite.AppDatabase
import com.fabiancg.persistenciaroomsqlite.SessionManager
import com.fabiancg.persistenciaroomsqlite.Usuario
import com.fabiancg.persistenciaroomsqlite.ui.components.FotoPerfilAvatar
import com.fabiancg.persistenciaroomsqlite.ui.components.guardarFotoEnAlmacenamiento
import kotlinx.coroutines.launch

/**
 * Pantalla de Perfil de Usuario.
 * Cumple con dos requerimientos clave de la pauta:
 * 1. Editar datos de usuario (nombre, correo, contraseña, foto de perfil) y guardarlos en SQLite.
 * 2. Cerrar sesión de manera adecuada y segura.
 */
@Composable
fun ProfileScreen(
    usuario: Usuario?,
    onUsuarioActualizado: (Usuario) -> Unit,
    onCerrarSesion: () -> Unit
) {
    val context = LocalContext.current
    val usuarioDao = remember { AppDatabase.getDatabase(context).usuarioDao() }
    val scope = rememberCoroutineScope()

    // Estados para la edición de datos
    var modoEdicion by remember { mutableStateOf(false) }
    var nombreEditado by remember { mutableStateOf(usuario?.nombre ?: "") }
    var emailEditado by remember { mutableStateOf(usuario?.email ?: "") }
    var claveEditada by remember { mutableStateOf(usuario?.contrasena ?: "") }
    var fotoEditada by remember { mutableStateOf(usuario?.fotoPerfil) }

    var mensajeError by remember { mutableStateOf<String?>(null) }
    var mensajeExito by remember { mutableStateOf<String?>(null) }
    var mostrarDialogoLogout by remember { mutableStateOf(false) }

    val selectorFotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val rutaGuardada = guardarFotoEnAlmacenamiento(context, uri)
                if (rutaGuardada != null) {
                    fotoEditada = rutaGuardada
                }
            }
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Avatar / Foto de Perfil del Usuario
        FotoPerfilAvatar(
            rutaFoto = if (modoEdicion) fotoEditada else usuario?.fotoPerfil,
            size = 88.dp
        )

        if (modoEdicion) {
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedButton(onClick = { selectorFotoLauncher.launch("image/*") }) {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Cambiar Foto", style = MaterialTheme.typography.labelSmall)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = usuario?.nombre ?: "Aspirante a Carabinero",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "RUT: ${usuario?.rut ?: "No disponible"}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(20.dp))

        // TARJETA DE INFORMACIÓN Y EDICIÓN DE DATOS
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (modoEdicion) "Editar Mis Datos" else "Datos del Usuario",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedButton(
                        onClick = {
                            modoEdicion = !modoEdicion
                            mensajeError = null
                            mensajeExito = null
                            if (!modoEdicion) {
                                nombreEditado = usuario?.nombre ?: ""
                                emailEditado = usuario?.email ?: ""
                                claveEditada = usuario?.contrasena ?: ""
                                fotoEditada = usuario?.fotoPerfil
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (modoEdicion) Icons.Default.Person else Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(if (modoEdicion) "Cancelar" else "Editar")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (!modoEdicion) {
                    // MODO SOLO LECTURA
                    Text("Correo: ${usuario?.email ?: ""}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Rol en el Sistema: ${usuario?.rol ?: "Alumno"}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Escalafón: ${usuario?.escalafon ?: "Aspirante a Carabinero"}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Base de datos: Room SQLite (Offline)", style = MaterialTheme.typography.bodyMedium)
                } else {
                    // MODO EDICIÓN DE DATOS
                    OutlinedTextField(
                        value = nombreEditado,
                        onValueChange = { nombreEditado = it },
                        label = { Text("Nombre") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = emailEditado,
                        onValueChange = { emailEditado = it },
                        label = { Text("Correo Institucional") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = claveEditada,
                        onValueChange = { claveEditada = it },
                        label = { Text("Nueva Contraseña") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Animación para mensajes de éxito o error
                    AnimatedVisibility(visible = mensajeError != null, enter = fadeIn(), exit = fadeOut()) {
                        Text(
                            text = mensajeError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    AnimatedVisibility(visible = mensajeExito != null, enter = fadeIn(), exit = fadeOut()) {
                        Text(
                            text = mensajeExito ?: "",
                            color = Color(0xFF1B5E20),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            mensajeError = null
                            mensajeExito = null

                            // Validaciones para la edición
                            if (nombreEditado.trim().length < 3) {
                                mensajeError = "El nombre debe tener al menos 3 caracteres."
                                return@Button
                            }
                            if (!emailEditado.contains("@") || !emailEditado.contains(".")) {
                                mensajeError = "El correo debe ser válido con @ y punto."
                                return@Button
                            }
                            if (claveEditada.length < 6) {
                                mensajeError = "La contraseña debe tener mínimo 6 caracteres."
                                return@Button
                            }

                            // Guardar cambios en SQLite
                            if (usuario != null) {
                                scope.launch {
                                    val usuarioActualizado = usuario.copy(
                                        nombre = nombreEditado.trim(),
                                        email = emailEditado.trim(),
                                        contrasena = claveEditada.trim(),
                                        fotoPerfil = fotoEditada
                                    )
                                    usuarioDao.actualizar(usuarioActualizado)
                                    SessionManager.guardarSesion(context, usuarioActualizado)
                                    onUsuarioActualizado(usuarioActualizado)
                                    mensajeExito = "¡Datos actualizados con éxito en SQLite!"
                                    modoEdicion = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Guardar Cambios")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // BOTÓN PARA CERRAR SESIÓN (REQUERIMIENTO: CERRAR SESIÓN ADECUADAMENTE)
        Button(
            onClick = { mostrarDialogoLogout = true },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar Sesión")
        }
    }

    // DIÁLOGO DE CONFIRMACIÓN PARA CERRAR SESIÓN
    if (mostrarDialogoLogout) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoLogout = false },
            title = {
                Text(
                    text = "Confirmación de Cierre",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("¿Está seguro de que desea cerrar la sesión actual de la ESFOCAR?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoLogout = false
                        SessionManager.cerrarSesion(context)
                        onCerrarSesion()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Sí, Salir")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { mostrarDialogoLogout = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
