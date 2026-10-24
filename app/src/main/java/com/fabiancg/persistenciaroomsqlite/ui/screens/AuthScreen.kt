package com.fabiancg.persistenciaroomsqlite.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fabiancg.persistenciaroomsqlite.AppDatabase
import com.fabiancg.persistenciaroomsqlite.SessionManager
import com.fabiancg.persistenciaroomsqlite.Usuario
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Pantalla de Autenticación (Login y Registro).
 * Diseñada de forma didáctica para nivel principiante:
 * - Estados simples con rememberSaveable.
 * - Validaciones directas con if/else fáciles de explicar al profesor.
 * - Guardado en SQLite usando Room.
 */
@Composable
fun AuthScreen(
    onLoginSuccess: (Usuario) -> Unit
) {
    val context = LocalContext.current
    val usuarioDao = remember { AppDatabase.getDatabase(context).usuarioDao() }
    val scope = rememberCoroutineScope()

    // 0 = Iniciar Sesión, 1 = Registrarse
    var tabSeleccionada by rememberSaveable { mutableIntStateOf(0) }

    // Estados para Inicio de Sesión
    var loginEmail by rememberSaveable { mutableStateOf("") }
    var loginClave by rememberSaveable { mutableStateOf("") }
    var loginError by rememberSaveable { mutableStateOf<String?>(null) }
    var loginClaveVisible by rememberSaveable { mutableStateOf(false) }

    // Estados para Registro de Usuario
    var regNombre by rememberSaveable { mutableStateOf("") }
    var regRut by rememberSaveable { mutableStateOf("") }
    var regEmail by rememberSaveable { mutableStateOf("") }
    var regClave by rememberSaveable { mutableStateOf("") }
    var regConfirmarClave by rememberSaveable { mutableStateOf("") }
    var regError by rememberSaveable { mutableStateOf<String?>(null) }
    var regExito by rememberSaveable { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Encabezado institucional ESFOCAR
        Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = "Escudo ESFOCAR",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.height(56.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "ESFOCAR - App de Estudio",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Sistema Académico de Rúbricas Policiales",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Selector de Pestañas: Iniciar Sesión / Registrarse
        TabRow(
            selectedTabIndex = tabSeleccionada,
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Tab(
                selected = tabSeleccionada == 0,
                onClick = {
                    tabSeleccionada = 0
                    loginError = null
                },
                text = { Text("Iniciar Sesión", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = tabSeleccionada == 1,
                onClick = {
                    tabSeleccionada = 1
                    regError = null
                    regExito = null
                },
                text = { Text("Registrarse", fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // TARJETA DE CONTENIDO (LOGIN O REGISTRO)
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (tabSeleccionada == 0) {
                    // ==========================================
                    // 1. VISTA DE INICIO DE SESIÓN
                    // ==========================================
                    Text(
                        text = "Acceso para Alumnos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Campo de Correo
                    OutlinedTextField(
                        value = loginEmail,
                        onValueChange = { loginEmail = it },
                        label = { Text("Correo Institucional o RUT") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Campo de Contraseña
                    OutlinedTextField(
                        value = loginClave,
                        onValueChange = { loginClave = it },
                        label = { Text("Contraseña") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { loginClaveVisible = !loginClaveVisible }) {
                                Icon(
                                    imageVector = if (loginClaveVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Alternar contraseña"
                                )
                            }
                        },
                        visualTransformation = if (loginClaveVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Animación para mensaje de error
                    AnimatedVisibility(
                        visible = loginError != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = loginError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón para Iniciar Sesión con SQLite
                    Button(
                        onClick = {
                            if (loginEmail.isBlank() || loginClave.isBlank()) {
                                loginError = "Por favor, complete todos los campos."
                                return@Button
                            }
                            scope.launch {
                                val usuario = usuarioDao.iniciarSesion(loginEmail.trim(), loginClave.trim())
                                if (usuario != null) {
                                    SessionManager.guardarSesion(context, usuario)
                                    onLoginSuccess(usuario)
                                } else {
                                    loginError = "Correo o contraseña incorrectos."
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ingresar")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Botón Demo para revisión rápida del profesor
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                // Usuario de prueba rápido
                                val demo = Usuario(
                                    id = 999,
                                    rut = "12345678-9",
                                    nombre = "Aspirante Cristóbal Flores",
                                    email = "aspirante@esfocar.cl",
                                    contrasena = "123456",
                                    escalafon = "Aspirante a Carabinero",
                                    fechaRegistro = "2026-10-23"
                                )
                                SessionManager.guardarSesion(context, demo)
                                onLoginSuccess(demo)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Acceso Rápido Demo (Profesor)")
                    }

                } else {
                    // ==========================================
                    // 2. VISTA DE REGISTRO DE USUARIOS
                    // ==========================================
                    Text(
                        text = "Registro de Nuevo Aspirante",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Nombre Completo
                    OutlinedTextField(
                        value = regNombre,
                        onValueChange = { regNombre = it },
                        label = { Text("Nombre Completo") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // RUT
                    OutlinedTextField(
                        value = regRut,
                        onValueChange = { regRut = it },
                        label = { Text("RUT (ej: 12345678-9)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Correo Institucional
                    OutlinedTextField(
                        value = regEmail,
                        onValueChange = { regEmail = it },
                        label = { Text("Correo Institucional") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Contraseña
                    OutlinedTextField(
                        value = regClave,
                        onValueChange = { regClave = it },
                        label = { Text("Contraseña (mínimo 6 carácteres)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Confirmar Contraseña
                    OutlinedTextField(
                        value = regConfirmarClave,
                        onValueChange = { regConfirmarClave = it },
                        label = { Text("Confirmar Contraseña") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Mensajes animados de Error o Éxito
                    AnimatedVisibility(visible = regError != null, enter = fadeIn(), exit = fadeOut()) {
                        Text(
                            text = regError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    AnimatedVisibility(visible = regExito != null, enter = fadeIn(), exit = fadeOut()) {
                        Text(
                            text = regExito ?: "",
                            color = Color(0xFF1B5E20),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón para Guardar Registro en SQLite
                    Button(
                        onClick = {
                            regError = null
                            regExito = null

                            // VALIDACIONES SENCILLAS Y DIDÁCTICAS:
                            if (regNombre.trim().length < 3) {
                                regError = "El nombre debe tener al menos 3 caracteres."
                                return@Button
                            }
                            if (!regRut.contains("-") || regRut.trim().length < 8) {
                                regError = "Ingrese un RUT válido con guion (ej: 12345678-9)."
                                return@Button
                            }
                            if (!regEmail.contains("@") || !regEmail.contains(".")) {
                                regError = "Ingrese un correo institucional válido (debe contener @ y .)."
                                return@Button
                            }
                            if (regClave.length < 6) {
                                regError = "La contraseña debe tener un mínimo de 6 caracteres."
                                return@Button
                            }
                            if (regClave != regConfirmarClave) {
                                regError = "Las contraseñas no coinciden."
                                return@Button
                            }

                            // Si todo es válido, guardamos en la base de datos Room SQLite
                            scope.launch {
                                val fechaHoy = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                                val nuevoUsuario = Usuario(
                                    rut = regRut.trim(),
                                    nombre = regNombre.trim(),
                                    email = regEmail.trim(),
                                    contrasena = regClave.trim(),
                                    escalafon = "Aspirante a Carabinero",
                                    fechaRegistro = fechaHoy
                                )
                                usuarioDao.insertar(nuevoUsuario)
                                regExito = "¡Usuario registrado con éxito en SQLite! Ahora puede iniciar sesión."

                                // Limpiamos los campos
                                regNombre = ""
                                regRut = ""
                                regEmail = ""
                                regClave = ""
                                regConfirmarClave = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Registrar Usuario")
                    }
                }
            }
        }
    }
}
