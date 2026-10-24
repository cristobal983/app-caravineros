package com.fabiancg.persistenciaroomsqlite.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Rutas de navegación simples para la aplicación.
 * Define las secciones que se mostrarán en la Barra Superior,
 * Barra Inferior y en el Menú Lateral.
 */
sealed class NavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    // 1. Pantalla de Inicio / Bienvenida
    object Home : NavigationItem("home", "Inicio", Icons.Default.Home)

    // 2. Pantalla de Rúbricas y Documentos (PDF y Word nativos)
    object Documents : NavigationItem("documents", "Rúbricas", Icons.AutoMirrored.Filled.InsertDriveFile)

    // 3. Pantalla de Perfil y Cierre de Sesión
    object Profile : NavigationItem("profile", "Mi Perfil", Icons.Default.AccountCircle)

    companion object {
        // Elementos de la barra inferior (NavigationBar)
        val bottomBarItems = listOf(Home, Documents, Profile)

        // Elementos del menú lateral (Drawer)
        val drawerItems = listOf(Home, Documents, Profile)
    }
}
