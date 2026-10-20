package com.fabiancg.persistenciaroomsqlite.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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

@Composable
fun AuthScreen(
    onLoginSuccess: (Usuario) -> Unit
) {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getDatabase(context).usuarioDao() }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    // 0: Iniciar Sesión, 1: Registrarse
    var tabSeleccionada by rememberSaveable { mutableIntStateOf(0) }

    // Estados de Login
    var loginIdentificador by rememberSaveable { mutableStateOf("") }
    var loginPassword by rememberSaveable { mutableStateOf("") }
    var loginPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var loginErrorMensaje by rememberSaveable { mutableStateOf<String?>(null) }

    // Estados de Registro
    var regNombre by rememberSaveable { mutableStateOf("") }
    var regRut by rememberSaveable { mutableStateOf("") }
    var regEmail by rememberSaveable { mutableStateOf("") }
    var regPassword by rememberSaveable { mutableStateOf("") }
    var regConfirmPassword by rememberSaveable { mutableStateOf("") }
    var regPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var regConfirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var regMensajeEstado by rememberSaveable { mutableStateOf<Pair<Boolean, String>?>(null) } // (esExito, mensaje)

    // Validaciones para formulario de Registro
    val esNombreValido = regNombre.trim().length >= 3
    val esRutValido = validarFormatoRut(regRut.trim())
    val esEmailValido = validarFormatoEmail(regEmail.trim())
    val esPasswordValida = regPassword.length >= 6
    val coincideConfirmacion = regPassword.isNotEmpty() && regPassword == regConfirmPassword

    val formularioRegistroValido = esNombreValido && esRutValido && esEmailValido && esPasswordValida && coincideConfirmacion

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

        // Encabezado Institucional ESFOCAR
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = "Emblema ESFOCAR",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(42.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "ESFOCAR - Plataforma de Estudio",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Escuela de Formación de Carabineros",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Selector de Pestañas (Iniciar Sesión vs Registrarse)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            TabRow(
                selectedTabIndex = tabSeleccionada,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .padding(6.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = tabSeleccionada == 0,
                    onClick = {
                        tabSeleccionada = 0
                        loginErrorMensaje = null
                    },
                    text = {
                        Text(
                            text = "Iniciar Sesión",
                            fontWeight = if (tabSeleccionada == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = tabSeleccionada == 1,
                    onClick = {
                        tabSeleccionada = 1
                        regMensajeEstado = null
                    },
                    text = {
                        Text(
                            text = "Registrarse",
                            fontWeight = if (tabSeleccionada == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==================== FORMULARIO DE INICIO DE SESIÓN ====================
            if (tabSeleccionada == 0) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Acceso para Aspirantes e Instructores",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = loginIdentificador,
                        onValueChange = {
                            loginIdentificador = it
                            loginErrorMensaje = null
                        },
                        label = { Text("Correo o RUT") },
                        placeholder = { Text("ejemplo@esfocar.cl o 12345678-9") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = loginPassword,
                        onValueChange = {
                            loginPassword = it
                            loginErrorMensaje = null
                        },
                        label = { Text("Contraseña") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { loginPasswordVisible = !loginPasswordVisible }) {
                                Icon(
                                    imageVector = if (loginPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Alternar contraseña"
                                )
                            }
                        },
                        visualTransformation = if (loginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        )
                    )

                    AnimatedVisibility(
                        visible = loginErrorMensaje != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = loginErrorMensaje ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val idLimpio = loginIdentificador.trim()
                            val claveLimpia = loginPassword.trim()

                            if (idLimpio.isEmpty() || claveLimpia.isEmpty()) {
                                loginErrorMensaje = "Complete todos los campos para ingresar."
                                return@Button
                            }

                            scope.launch {
                                val usuario = dao.login(idLimpio, claveLimpia)
                                if (usuario != null) {
                                    SessionManager.guardarSesion(context, usuario)
                                    onLoginSuccess(usuario)
                                } else {
                                    loginErrorMensaje = "Credenciales incorrectas o usuario no registrado."
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Iniciar Sesión",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Botón de demostración rápida para docentes / evaluación
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                // Crear un usuario de prueba si no existe
                                var usuarioPrueba = dao.buscarPorEmailORut("aspirante.demo@esfocar.cl")
                                if (usuarioPrueba == null) {
                                    val nuevo = Usuario(
                                        rut = "19.876.543-2",
                                        nombre = "Aspirante González",
                                        email = "aspirante.demo@esfocar.cl",
                                        contrasena = "esfocar123",
                                        escalafon = "Aspirante a Carabinero",
                                        fechaRegistro = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                                    )
                                    dao.registrar(nuevo)
                                    usuarioPrueba = dao.buscarPorEmailORut("aspirante.demo@esfocar.cl")
                                }
                                if (usuarioPrueba != null) {
                                    SessionManager.guardarSesion(context, usuarioPrueba)
                                    onLoginSuccess(usuarioPrueba)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "Acceso Rápido Aspirante Demo (Docente)")
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else {
                // ==================== FORMULARIO DE REGISTRO VALIDADO ====================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Registro de Nuevo Aspirante",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Todos los campos cuentan con validación en tiempo real.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 1. Nombre Completo
                    OutlinedTextField(
                        value = regNombre,
                        onValueChange = {
                            regNombre = it
                            regMensajeEstado = null
                        },
                        label = { Text("Nombre Completo") },
                        placeholder = { Text("Ej: Juan Pérez Martínez") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        isError = regNombre.isNotEmpty() && !esNombreValido,
                        supportingText = {
                            if (regNombre.isNotEmpty() && !esNombreValido) {
                                Text("Mínimo 3 caracteres requeridos")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 2. RUT
                    OutlinedTextField(
                        value = regRut,
                        onValueChange = {
                            regRut = it
                            regMensajeEstado = null
                        },
                        label = { Text("RUT") },
                        placeholder = { Text("Ej: 19876543-2") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                        isError = regRut.isNotEmpty() && !esRutValido,
                        supportingText = {
                            if (regRut.isNotEmpty() && !esRutValido) {
                                Text("Formato válido: 12345678-9 o 12.345.678-K")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 3. Email
                    OutlinedTextField(
                        value = regEmail,
                        onValueChange = {
                            regEmail = it
                            regMensajeEstado = null
                        },
                        label = { Text("Correo Institucional / Académico") },
                        placeholder = { Text("Ej: juan.perez@esfocar.cl") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        isError = regEmail.isNotEmpty() && !esEmailValido,
                        supportingText = {
                            if (regEmail.isNotEmpty() && !esEmailValido) {
                                Text("Ingrese un formato de correo válido (ej: nombre@dominio.cl)")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 4. Contraseña
                    OutlinedTextField(
                        value = regPassword,
                        onValueChange = {
                            regPassword = it
                            regMensajeEstado = null
                        },
                        label = { Text("Contraseña (mínimo 6 caracteres)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { regPasswordVisible = !regPasswordVisible }) {
                                Icon(
                                    imageVector = if (regPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (regPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = regPassword.isNotEmpty() && !esPasswordValida,
                        supportingText = {
                            if (regPassword.isNotEmpty() && !esPasswordValida) {
                                Text("La clave debe tener al menos 6 caracteres")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 5. Confirmar Contraseña
                    OutlinedTextField(
                        value = regConfirmPassword,
                        onValueChange = {
                            regConfirmPassword = it
                            regMensajeEstado = null
                        },
                        label = { Text("Confirmar Contraseña") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { regConfirmPasswordVisible = !regConfirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (regConfirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (regConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = regConfirmPassword.isNotEmpty() && !coincideConfirmacion,
                        supportingText = {
                            if (regConfirmPassword.isNotEmpty() && !coincideConfirmacion) {
                                Text("Las contraseñas no coinciden")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                    )

                    // Mensaje de estado (éxito o error al guardar en Room)
                    AnimatedVisibility(
                        visible = regMensajeEstado != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        val estado = regMensajeEstado
                        if (estado != null) {
                            val (esExito, msg) = estado
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (esExito) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Text(
                                    text = msg,
                                    modifier = Modifier.padding(12.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (esExito) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            if (!formularioRegistroValido) {
                                regMensajeEstado = Pair(false, "Por favor corrija los campos marcados antes de continuar.")
                                return@Button
                            }

                            scope.launch {
                                // Verificar si ya existe RUT o Correo en SQLite
                                val existeRut = dao.buscarPorEmailORut(regRut.trim())
                                val existeEmail = dao.buscarPorEmailORut(regEmail.trim())

                                if (existeRut != null || existeEmail != null) {
                                    regMensajeEstado = Pair(false, "El RUT o correo ingresado ya se encuentra registrado.")
                                    return@launch
                                }

                                val nuevoUsuario = Usuario(
                                    rut = regRut.trim(),
                                    nombre = regNombre.trim(),
                                    email = regEmail.trim(),
                                    contrasena = regPassword.trim(),
                                    escalafon = "Aspirante a Carabinero",
                                    fechaRegistro = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                                )

                                val rowId = dao.registrar(nuevoUsuario)
                                if (rowId > 0) {
                                    regMensajeEstado = Pair(true, "¡Registro completado exitosamente! Ya puedes iniciar sesión.")
                                    // Limpiar formulario y pasar a tab de login tras 1 segundo
                                    loginIdentificador = regEmail.trim()
                                    loginPassword = ""
                                    regNombre = ""
                                    regRut = ""
                                    regEmail = ""
                                    regPassword = ""
                                    regConfirmPassword = ""
                                    tabSeleccionada = 0
                                } else {
                                    regMensajeEstado = Pair(false, "Error al registrar en la base de datos.")
                                }
                            }
                        },
                        enabled = formularioRegistroValido,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Registrar Aspirante",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Sistema Académico Offline-First con Room SQLite",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

/**
 * Valida un RUT chileno en formato básico o formal (mínimo 8 caracteres, números + dígito verificador).
 */
private fun validarFormatoRut(rut: String): Boolean {
    val limpio = rut.replace(".", "").replace("-", "").trim()
    if (limpio.length < 8 || limpio.length > 9) return false
    val cuerpo = limpio.substring(0, limpio.length - 1)
    val dv = limpio.last().uppercaseChar()
    return cuerpo.all { it.isDigit() } && (dv.isDigit() || dv == 'K')
}

/**
 * Valida formato estándar de correo electrónico.
 */
private fun validarFormatoEmail(email: String): Boolean {
    val patron = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
    return email.matches(patron)
}
