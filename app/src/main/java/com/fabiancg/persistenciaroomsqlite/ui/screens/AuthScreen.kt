package com.fabiancg.persistenciaroomsqlite.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fabiancg.persistenciaroomsqlite.AppDatabase
import com.fabiancg.persistenciaroomsqlite.R
import com.fabiancg.persistenciaroomsqlite.SessionManager
import com.fabiancg.persistenciaroomsqlite.Usuario
import kotlinx.coroutines.launch
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Función auxiliar para generar automáticamente el correo institucional
 * según el nombre y el rol seleccionado.
 * Ejemplo: Cristóbal Flores (Alumno) -> cflores@esfocar.cl o alumno@esfocar.cl
 */
private fun generarCorreoInstitucional(nombre: String, rol: String): String {
    val dominio = "esfocar.cl"
    val sufijoRol = if (rol.equals("Docente", ignoreCase = true)) "docente" else "alumno"

    val nombreLimpio = Normalizer.normalize(nombre.trim().lowercase(Locale.getDefault()), Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
        .replace("[^a-z0-9 ]".toRegex(), "")
        .trim()

    val partes = nombreLimpio.split("\\s+".toRegex()).filter { it.isNotBlank() }
    return if (partes.size >= 2) {
        val inicial = partes[0].take(1)
        val apellido = partes[1]
        "$inicial$apellido@$dominio"
    } else if (partes.size == 1 && partes[0].length >= 3) {
        "${partes[0]}@$dominio"
    } else {
        "$sufijoRol@$dominio"
    }
}

/**
 * Pantalla de Autenticación (Login y Registro).
 * Diseñada de forma didáctica para nivel principiante:
 * - Generación automática de correo institucional (@esfocar.cl) según nombre y rol.
 * - Mensaje y Diálogo emergente con Logo y Agradecimiento tras registrarse.
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
    var regRol by rememberSaveable { mutableStateOf("Alumno") } // "Alumno" o "Docente"
    var regNombre by rememberSaveable { mutableStateOf("") }
    var regRut by rememberSaveable { mutableStateOf("") }
    var regEmail by rememberSaveable { mutableStateOf("alumno@esfocar.cl") }
    var regClave by rememberSaveable { mutableStateOf("") }
    var regConfirmarClave by rememberSaveable { mutableStateOf("") }
    var regError by rememberSaveable { mutableStateOf<String?>(null) }
    var regExito by rememberSaveable { mutableStateOf<String?>(null) }

    // Diálogo emergente de agradecimiento post-registro
    var mostrarAgradecimiento by rememberSaveable { mutableStateOf(false) }
    var nombreRecienRegistrado by rememberSaveable { mutableStateOf("") }
    var correoRecienRegistrado by rememberSaveable { mutableStateOf("") }
    var rolRecienRegistrado by rememberSaveable { mutableStateOf("") }

    val scrollState = rememberScrollState()

    // ========================================================
    // DIÁLOGO DE AGRADECIMIENTO CON LOGO Y NOMBRE DE LA APP
    // ========================================================
    if (mostrarAgradecimiento) {
        AlertDialog(
            onDismissRequest = { mostrarAgradecimiento = false },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarAgradecimiento = false
                        tabSeleccionada = 0 // Redirigir a iniciar sesión
                        loginEmail = correoRecienRegistrado
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Iniciar Sesión Ahora")
                }
            },
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.escudo_esfocar),
                        contentDescription = "Logo ESFOCAR",
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "ESFOCAR - App de Estudio",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "¡Muchas gracias por registrarte!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Bienvenido(a) a la Escuela de Formación de Carabineros.\n\n" +
                                "• Usuario: $nombreRecienRegistrado\n" +
                                "• Rol: $rolRecienRegistrado\n" +
                                "• Correo asignado: $correoRecienRegistrado\n\n" +
                                "Tu cuenta fue guardada con éxito en la base de datos local SQLite.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Encabezado institucional con Escudo Oficial ESFOCAR
        Image(
            painter = painterResource(id = R.drawable.escudo_esfocar),
            contentDescription = "Escudo Oficial ESFOCAR",
            modifier = Modifier.height(84.dp)
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
                        text = "Acceso Institucional",
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

                    // Botones Demo para revisión rápida del profesor
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    val demoDocente = Usuario(
                                        id = 998,
                                        rut = "11223344-5",
                                        nombre = "Prof. Carlos Gómez",
                                        email = "docente@esfocar.cl",
                                        contrasena = "123456",
                                        rol = "Docente",
                                        escalafon = "Profesor Instructor ESFOCAR",
                                        fechaRegistro = "2026-10-23"
                                    )
                                    SessionManager.guardarSesion(context, demoDocente)
                                    onLoginSuccess(demoDocente)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Demo Docente", style = MaterialTheme.typography.labelSmall)
                        }

                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    val demoAlumno = Usuario(
                                        id = 999,
                                        rut = "12345678-9",
                                        nombre = "Aspirante Cristóbal Flores",
                                        email = "alumno@esfocar.cl",
                                        contrasena = "123456",
                                        rol = "Alumno",
                                        escalafon = "Aspirante a Carabinero",
                                        fechaRegistro = "2026-10-23"
                                    )
                                    SessionManager.guardarSesion(context, demoAlumno)
                                    onLoginSuccess(demoAlumno)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Demo Alumno", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                } else {
                    // ==========================================
                    // 2. VISTA DE REGISTRO DE USUARIOS
                    // ==========================================
                    Text(
                        text = "Registro de Nuevo Usuario",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // SELECCIÓN DE ROL (DOCENTE O ALUMNO)
                    Text(
                        text = "¿Eres Docente o Alumno?",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Tarjeta Rol Alumno
                        OutlinedCard(
                            onClick = {
                                regRol = "Alumno"
                                regEmail = generarCorreoInstitucional(regNombre, "Alumno")
                            },
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(
                                width = if (regRol == "Alumno") 2.dp else 1.dp,
                                color = if (regRol == "Alumno") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                            ),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = if (regRol == "Alumno") MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f) else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = if (regRol == "Alumno") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Alumno",
                                    fontWeight = if (regRol == "Alumno") FontWeight.Bold else FontWeight.Medium,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (regRol == "Alumno") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Aspirante ESFOCAR",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Tarjeta Rol Docente
                        OutlinedCard(
                            onClick = {
                                regRol = "Docente"
                                regEmail = generarCorreoInstitucional(regNombre, "Docente")
                            },
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(
                                width = if (regRol == "Docente") 2.dp else 1.dp,
                                color = if (regRol == "Docente") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                            ),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = if (regRol == "Docente") MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f) else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SupervisorAccount,
                                    contentDescription = null,
                                    tint = if (regRol == "Docente") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Docente",
                                    fontWeight = if (regRol == "Docente") FontWeight.Bold else FontWeight.Medium,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (regRol == "Docente") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Profesor Instructor",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Nombre Completo (Genera el correo automáticamente)
                    OutlinedTextField(
                        value = regNombre,
                        onValueChange = { nuevoNombre ->
                            regNombre = nuevoNombre
                            regEmail = generarCorreoInstitucional(nuevoNombre, regRol)
                        },
                        label = { Text("Nombre Completo") },
                        placeholder = { Text("Ej: Cristóbal Flores") },
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

                    // Correo Institucional generado automáticamente
                    OutlinedTextField(
                        value = regEmail,
                        onValueChange = { regEmail = it },
                        label = { Text("Correo Institucional (Generado automáticamente)") },
                        supportingText = {
                            Text(
                                text = "Generado según tu nombre y rol (@esfocar.cl)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
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
                                val escalafonSegunRol = if (regRol == "Docente") {
                                    "Profesor Instructor ESFOCAR"
                                } else {
                                    "Aspirante a Carabinero"
                                }

                                val emailFinal = regEmail.trim()
                                val nombreFinal = regNombre.trim()
                                val rolFinal = regRol

                                val nuevoUsuario = Usuario(
                                    rut = regRut.trim(),
                                    nombre = nombreFinal,
                                    email = emailFinal,
                                    contrasena = regClave.trim(),
                                    rol = rolFinal,
                                    escalafon = escalafonSegunRol,
                                    fechaRegistro = fechaHoy
                                )
                                usuarioDao.insertar(nuevoUsuario)

                                // Activamos el mensaje y diálogo emergente de agradecimiento con el logo
                                nombreRecienRegistrado = nombreFinal
                                correoRecienRegistrado = emailFinal
                                rolRecienRegistrado = rolFinal
                                mostrarAgradecimiento = true

                                regExito = "¡Usuario ($rolFinal) registrado con éxito en SQLite!"

                                // Limpiamos los campos
                                regNombre = ""
                                regRut = ""
                                regEmail = "alumno@esfocar.cl"
                                regClave = ""
                                regConfirmarClave = ""
                                regRol = "Alumno"
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
