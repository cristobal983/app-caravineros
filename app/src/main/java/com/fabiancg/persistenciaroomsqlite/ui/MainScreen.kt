package com.fabiancg.persistenciaroomsqlite.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fabiancg.persistenciaroomsqlite.R
import com.fabiancg.persistenciaroomsqlite.SessionManager
import com.fabiancg.persistenciaroomsqlite.Usuario
import com.fabiancg.persistenciaroomsqlite.ui.navigation.NavigationItem
import com.fabiancg.persistenciaroomsqlite.ui.screens.AdminScreen
import com.fabiancg.persistenciaroomsqlite.ui.screens.AuthScreen
import com.fabiancg.persistenciaroomsqlite.ui.screens.DocumentsScreen
import com.fabiancg.persistenciaroomsqlite.ui.screens.HomeScreen
import com.fabiancg.persistenciaroomsqlite.ui.screens.ProfileScreen
import com.fabiancg.persistenciaroomsqlite.ui.screens.SplashScreen
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Control de la Pantalla de Carga Inicial (SplashScreen)
    var mostrarSplash by rememberSaveable { mutableStateOf(true) }

    // Estado reactivo de usuario autenticado
    var usuarioActivo by remember { mutableStateOf<Usuario?>(SessionManager.obtenerUsuarioActivo(context)) }

    // Ruta actual de navegación
    var seccionActual by rememberSaveable { mutableStateOf("home") }

    if (mostrarSplash) {
        // Pantalla de Carga Inicial con Escudo de ESFOCAR
        SplashScreen(
            onSplashFinished = {
                mostrarSplash = false
            }
        )
    } else {
        // Transición animada entre pantalla de Login/Registro y la App Autenticada
        Crossfade(targetState = usuarioActivo != null, label = "AuthCrossfade") { estaLogueado ->
            if (!estaLogueado) {
                // VISTA 1 & 2: INICIAR SESIÓN Y REGISTRO DE USUARIOS
                AuthScreen(
                    onLoginSuccess = { usuario ->
                        usuarioActivo = usuario
                        seccionActual = NavigationItem.Home.route
                    }
                )
            } else {
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

            // REQUERIMIENTO: MENÚ LATERAL (Navigation Drawer)
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet {
                        // Encabezado del menú
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(24.dp)
                        ) {
                            Column {
                                Image(
                                    painter = painterResource(id = R.drawable.escudo_esfocar),
                                    contentDescription = "Escudo Oficial ESFOCAR",
                                    modifier = Modifier.height(52.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = usuarioActivo?.nombre ?: "Usuario ESFOCAR",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = usuarioActivo?.escalafon ?: "Aspirante a Carabinero",
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Opciones de navegación en el menú lateral
                        NavigationItem.drawerItems.forEach { item ->
                            val seleccionado = seccionActual == item.route
                            NavigationDrawerItem(
                                icon = { Icon(item.icon, contentDescription = item.title) },
                                label = { Text(item.title, fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal) },
                                selected = seleccionado,
                                onClick = {
                                    seccionActual = item.route
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp))

                        // Opción para Cerrar Sesión desde el menú lateral
                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = "Cerrar Sesión",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            },
                            label = {
                                Text(
                                    text = "Cerrar Sesión",
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            selected = false,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    seccionActual = NavigationItem.Profile.route
                                }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            ) {
                Scaffold(
                    // REQUERIMIENTO: BARRA DE NAVEGACIÓN SUPERIOR
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = when (seccionActual) {
                                        NavigationItem.Home.route -> "Inicio - ESFOCAR"
                                        NavigationItem.Documents.route -> "Rúbricas y Documentos"
                                        NavigationItem.Admin.route -> "Modo Administración"
                                        NavigationItem.Profile.route -> "Mi Perfil de Usuario"
                                        else -> "ESFOCAR App"
                                    },
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Abrir Menú Lateral"
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = { seccionActual = NavigationItem.Profile.route }) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "Ver Perfil"
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    },
                    // REQUERIMIENTO: BARRA DE NAVEGACIÓN INFERIOR
                    bottomBar = {
                        NavigationBar {
                            NavigationItem.bottomBarItems.forEach { item ->
                                val seleccionado = seccionActual == item.route
                                NavigationBarItem(
                                    selected = seleccionado,
                                    onClick = { seccionActual = item.route },
                                    icon = { Icon(item.icon, contentDescription = item.title) },
                                    label = { Text(item.title) }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Animación suave de cambio de pantalla
                        Crossfade(targetState = seccionActual, label = "MainContentTransition") { ruta ->
                            when (ruta) {
                                NavigationItem.Home.route -> {
                                    HomeScreen(
                                        usuario = usuarioActivo,
                                        onIrADocumentos = { seccionActual = NavigationItem.Documents.route },
                                        onIrAPerfil = { seccionActual = NavigationItem.Profile.route }
                                    )
                                }
                                NavigationItem.Documents.route -> {
                                    DocumentsScreen(usuarioActivo = usuarioActivo)
                                }
                                NavigationItem.Admin.route -> {
                                    AdminScreen(usuarioActivo = usuarioActivo)
                                }
                                NavigationItem.Profile.route -> {
                                    ProfileScreen(
                                        usuario = usuarioActivo,
                                        onUsuarioActualizado = { nuevo ->
                                            usuarioActivo = nuevo
                                        },
                                        onCerrarSesion = {
                                            usuarioActivo = null
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
}
