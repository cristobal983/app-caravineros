package com.fabiancg.persistenciaroomsqlite.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fabiancg.persistenciaroomsqlite.AppDatabase
import com.fabiancg.persistenciaroomsqlite.ui.navigation.NavigationItem
import java.util.Locale

@Composable
fun HomeScreen(
    onNavigateToSection: (NavigationItem) -> Unit
) {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getDatabase(context).intentoEvaluacionDao() }
    val totalTrivias by dao.contarTotal().collectAsState(initial = 0)
    val promedioFlow by dao.obtenerPromedio().collectAsState(initial = null)
    val porcentajeTexto = if (promedioFlow != null) String.format(Locale.US, "%.0f%%", promedioFlow) else "0%"

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Bienvenido a la App de Estudio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Esqueleto de la aplicación para estudio, autoevaluación y registro de notas.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$totalTrivias",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Trivias rendidas",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = porcentajeTexto,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Promedio general",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Text(
            text = "Módulos disponibles",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        ModuloOpcion(
            titulo = "Materias",
            descripcion = "Módulo de contenidos y apuntes de estudio.",
            icono = Icons.Default.AutoStories,
            onClick = { onNavigateToSection(NavigationItem.Study) }
        )

        ModuloOpcion(
            titulo = "Trivias",
            descripcion = "Cuestionario de autoevaluación interactivo.",
            icono = Icons.Default.Quiz,
            onClick = { onNavigateToSection(NavigationItem.Quiz) }
        )

        ModuloOpcion(
            titulo = "Casos Prácticos",
            descripcion = "Módulo de análisis de casos y decisiones.",
            icono = Icons.AutoMirrored.Filled.Assignment,
            onClick = { onNavigateToSection(NavigationItem.Cases) }
        )

        ModuloOpcion(
            titulo = "Rúbricas y Documentos",
            descripcion = "Cargar, visualizar y eliminar archivos PDF y Word.",
            icono = Icons.AutoMirrored.Filled.InsertDriveFile,
            onClick = { onNavigateToSection(NavigationItem.Documents) }
        )

        ModuloOpcion(
            titulo = "Progreso",
            descripcion = "Historial de notas guardadas en SQLite.",
            icono = Icons.Default.Leaderboard,
            onClick = { onNavigateToSection(NavigationItem.Progress) }
        )
    }
}

@Composable
private fun ModuloOpcion(
    titulo: String,
    descripcion: String,
    icono: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
