package com.fabiancg.persistenciaroomsqlite

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alertas")
data class AlertaItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val mensaje: String,
    val tipo: String, // "DOCUMENTO", "RETROALIMENTACION", "GENERAL"
    val autor: String,
    val destinatario: String = "Docentes y Alumnos",
    val fecha: String,
    val leida: Boolean = false
)
