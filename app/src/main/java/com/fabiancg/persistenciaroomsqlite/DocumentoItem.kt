package com.fabiancg.persistenciaroomsqlite

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room para la tabla "documentos_rubricas".
 * Representa los documentos institucionales (archivos nativos PDF y Word .docx)
 * cargados, visualizados o eliminados por los alumnos y docentes.
 */
@Entity(tableName = "documentos_rubricas")
data class DocumentoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,
    val tipo: String, // "PDF" o "WORD"
    val tamanoBytes: Long,
    val rutaArchivo: String, // Ruta absoluta interna en context.filesDir
    val fechaCarga: String,
    val categoria: String = "Rúbrica Institucional"
)
