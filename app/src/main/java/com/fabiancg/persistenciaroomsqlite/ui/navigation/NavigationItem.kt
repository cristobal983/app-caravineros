package com.fabiancg.persistenciaroomsqlite.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
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

    // 5. Pantalla de Feriados (API externa — Módulo 3 PU3)
    object Feriados : NavigationItem("feriados", "Feriados", Icons.Default.CalendarMonth)

    // 6. Pantalla de Comunicados (Microservicio Spring Boot)
    object Comunicados : NavigationItem("comunicados", "Comunicados", Icons.Default.Campaign)

    // 7. Pantalla de Alertas y Retroalimentación
    object Alertas : NavigationItem("alertas", "Alertas", Icons.Default.Notifications)

    // 8. Asistente IA ESFOCAR
    object Asistente : NavigationItem("asistente", "Asistente IA", Icons.Default.AutoAwesome)

    companion object {
        // Retorna los elementos del menú según el rol (Modo Admin solo visible para Docentes)
        fun obtenerBottomBarItems(esDocente: Boolean): List<NavigationItem> {
            return if (esDocente) {
                listOf(Home, Documents, Feriados, Admin, Profile)
            } else {
                listOf(Home, Documents, Feriados, Profile)
            }
        }

        fun obtenerDrawerItems(esDocente: Boolean): List<NavigationItem> {
            return if (esDocente) {
                listOf(Home, Documents, Alertas, Asistente, Comunicados, Feriados, Admin, Profile)
            } else {
                listOf(Home, Documents, Alertas, Asistente, Comunicados, Feriados, Profile)
            }
        }

        // Elementos predeterminados
        val bottomBarItems = listOf(Home, Documents, Feriados, Profile)
        val drawerItems = listOf(Home, Documents, Alertas, Asistente, Comunicados, Feriados, Profile)
    }
}
