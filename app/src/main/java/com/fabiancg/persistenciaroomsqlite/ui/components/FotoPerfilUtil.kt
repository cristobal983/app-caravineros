package com.fabiancg.persistenciaroomsqlite.ui.components

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Función que formatea automáticamente el RUT chileno con su guion
 * antes del dígito verificador a medida que el usuario escribe.
 * Ej: 123456789 -> 12345678-9
 */
fun formatearRutAutomatico(entrada: String): String {
    // Conserva sólo dígitos y k/K, hasta un máximo de 9 caracteres
    val limpio = entrada.filter { it.isDigit() || it.equals('k', ignoreCase = true) }.take(9)
    if (limpio.length <= 1) return limpio
    val cuerpo = limpio.dropLast(1)
    val dv = limpio.takeLast(1).uppercase()
    return "$cuerpo-$dv"
}

/**
 * Guarda de forma segura una imagen de perfil en el almacenamiento privado de la app.
 */
suspend fun guardarFotoEnAlmacenamiento(context: Context, uri: Uri): String? = withContext(Dispatchers.IO) {
    try {
        val carpeta = File(context.filesDir, "fotos_perfil").apply { mkdirs() }
        val archivo = File(carpeta, "perfil_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { entrada ->
            FileOutputStream(archivo).use { salida ->
                entrada.copyTo(salida)
            }
        }
        archivo.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Componente visual para mostrar la foto de perfil en forma circular.
 * Si el usuario no tiene foto, muestra un avatar con icono por defecto.
 */
@Composable
fun FotoPerfilAvatar(
    rutaFoto: String?,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp
) {
    val bitmap = remember(rutaFoto) {
        if (!rutaFoto.isNullOrBlank()) {
            val f = File(rutaFoto)
            if (f.exists()) {
                try {
                    BitmapFactory.decodeFile(f.absolutePath)?.asImageBitmap()
                } catch (e: Exception) {
                    null
                }
            } else null
        } else null
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = "Foto de Perfil",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )
    } else {
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Sin Foto",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(size * 0.75f)
            )
        }
    }
}
