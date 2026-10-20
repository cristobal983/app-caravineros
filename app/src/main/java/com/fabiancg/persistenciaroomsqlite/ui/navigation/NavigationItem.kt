package com.fabiancg.persistenciaroomsqlite.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Storage
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Representa cada una de las secciones y pantallas de la aplicación ESFOCAR.
 */
sealed class NavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : NavigationItem("home", "Inicio", Icons.Default.Home)
    object Study : NavigationItem("study", "Materias", Icons.Default.AutoStories)
    object Quiz : NavigationItem("quiz", "Trivias", Icons.Default.Quiz)
    object Cases : NavigationItem("cases", "Casos", Icons.AutoMirrored.Filled.Assignment)
    object Documents : NavigationItem("documents", "Rúbricas / Docs", Icons.AutoMirrored.Filled.InsertDriveFile)
    object Progress : NavigationItem("progress", "Progreso", Icons.Default.Leaderboard)
    object Profile : NavigationItem("profile", "Mi Perfil", Icons.Default.AccountCircle)
    object Demo : NavigationItem("demo", "Demo Persistencia", Icons.Default.Storage)

    companion object {
        // Elementos presentes en la barra inferior (BottomNavigationBar)
        val bottomBarItems = listOf(Home, Study, Quiz, Documents, Progress)

        // Todos los elementos accesibles desde el menú lateral (NavigationDrawer)
        val drawerItems = listOf(Home, Study, Quiz, Cases, Documents, Progress, Demo, Profile)
    }
}
