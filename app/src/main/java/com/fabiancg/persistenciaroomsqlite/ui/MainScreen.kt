package com.fabiancg.persistenciaroomsqlite.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fabiancg.persistenciaroomsqlite.AppDatabase
import com.fabiancg.persistenciaroomsqlite.DemoPersistenciaScreen
import com.fabiancg.persistenciaroomsqlite.SessionManager
import com.fabiancg.persistenciaroomsqlite.Usuario
import com.fabiancg.persistenciaroomsqlite.ui.navigation.NavigationItem
import com.fabiancg.persistenciaroomsqlite.ui.screens.AuthScreen
import com.fabiancg.persistenciaroomsqlite.ui.screens.CasesScreen
import com.fabiancg.persistenciaroomsqlite.ui.screens.DocumentsScreen
import com.fabiancg.persistenciaroomsqlite.ui.screens.HomeScreen
import com.fabiancg.persistenciaroomsqlite.ui.screens.ProfileScreen
import com.fabiancg.persistenciaroomsqlite.ui.screens.ProgressScreen
import com.fabiancg.persistenciaroomsqlite.ui.screens.QuizScreen
import com.fabiancg.persistenciaroomsqlite.ui.screens.StudyScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val personaDao = remember { AppDatabase.getDatabase(context).personaDao() }

    // Estado reactivo de sesión del usuario (si es null, muestra pantalla de Login / Registro)
    var usuarioActivo by remember { mutableStateOf<Usuario?>(SessionManager.obtenerUsuarioActivo(context)) }

    // Ruta actual de la aplicación
    var seccionActual by rememberSaveable { mutableStateOf("home") }

    // Animación de transición entre estado no autenticado (Login/Registro) y estado autenticado
    Crossfade(targetState = usuarioActivo != null, label = "AuthTransition") { estaAutenticado ->
        if (!estaAutenticado) {
            AuthScreen(
                onLoginSuccess = { usuarioLogueado ->
                    usuarioActivo = usuarioLogueado
                    seccionActual = NavigationItem.Home.route
                }
            )
        } else {
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

            // Menú lateral (Navigation Drawer)
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        drawerContainerColor = MaterialTheme.colorScheme.surface
                    ) {
                        // Encabezado institucional del menú lateral
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(20.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Security,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "ESFOCAR",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = "Carabineros de Chile",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = usuarioActivo?.nombre ?: "Aspirante a Carabinero",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "${usuarioActivo?.escalafon} • ${usuarioActivo?.rut}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Opciones de navegación del menú lateral
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

                        // Opción de Cierre de Sesión en el menú lateral
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
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = obtenerTituloBarra(seccionActual),
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Abrir menú lateral"
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = { seccionActual = NavigationItem.Profile.route }) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "Ver Perfil de Usuario"
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
                    bottomBar = {
                        NavigationBar {
                            NavigationItem.bottomBarItems.forEach { item ->
                                val seleccionado = seccionActual == item.route
                                NavigationBarItem(
                                    selected = seleccionado,
                                    onClick = { seccionActual = item.route },
                                    icon = {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.title
                                        )
                                    },
                                    label = {
                                        Text(text = item.title)
                                    }
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
                        Crossfade(targetState = seccionActual, label = "ScreenTransition") { targetRoute ->
                            when (targetRoute) {
                                NavigationItem.Home.route -> HomeScreen(
                                    onNavigateToSection = { destino -> seccionActual = destino.route }
                                )
                                NavigationItem.Study.route -> StudyScreen()
                                NavigationItem.Quiz.route -> QuizScreen()
                                NavigationItem.Cases.route -> CasesScreen()
                                NavigationItem.Documents.route -> DocumentsScreen()
                                NavigationItem.Progress.route -> ProgressScreen()
                                NavigationItem.Profile.route -> ProfileScreen(
                                    usuario = usuarioActivo,
                                    onCerrarSesion = {
                                        usuarioActivo = null
                                        seccionActual = NavigationItem.Home.route
                                    }
                                )
                                NavigationItem.Demo.route -> DemoPersistenciaScreen(dao = personaDao)
                                else -> HomeScreen(
                                    onNavigateToSection = { destino -> seccionActual = destino.route }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Retorna el título dinámico a mostrar en la TopAppBar según la ruta actual.
 */
private fun obtenerTituloBarra(ruta: String): String {
    return when (ruta) {
        NavigationItem.Home.route -> "ESFOCAR - Inicio"
        NavigationItem.Study.route -> "Materias de Estudio"
        NavigationItem.Quiz.route -> "Trivias y Autoevaluación"
        NavigationItem.Cases.route -> "Casos Prácticos"
        NavigationItem.Documents.route -> "Rúbricas y Documentos"
        NavigationItem.Progress.route -> "Historial y Progreso"
        NavigationItem.Profile.route -> "Perfil del Aspirante"
        NavigationItem.Demo.route -> "Laboratorio de Persistencia"
        else -> "App de Estudio ESFOCAR"
    }
}
