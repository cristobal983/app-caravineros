package com.fabiancg.persistenciaroomsqlite.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SupervisorAccount
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

    // 4. Pantalla de Panel de Administración (Modo Admin)
    object Admin : NavigationItem("admin", "Modo Admin", Icons.Default.SupervisorAccount)

    companion object {
        // Retorna los elementos del menú según el rol (Modo Admin solo visible para Docentes)
        fun obtenerBottomBarItems(esDocente: Boolean): List<NavigationItem> {
            return if (esDocente) {
                listOf(Home, Documents, Admin, Profile)
            } else {
                listOf(Home, Documents, Profile)
            }
        }

        fun obtenerDrawerItems(esDocente: Boolean): List<NavigationItem> {
            return if (esDocente) {
                listOf(Home, Documents, Admin, Profile)
            } else {
                listOf(Home, Documents, Profile)
            }
        }

        // Elementos predeterminados
        val bottomBarItems = listOf(Home, Documents, Profile)
        val drawerItems = listOf(Home, Documents, Profile)
    }
}
