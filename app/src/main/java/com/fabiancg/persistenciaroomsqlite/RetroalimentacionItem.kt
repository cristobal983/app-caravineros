package com.fabiancg.persistenciaroomsqlite

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "retroalimentaciones")
data class RetroalimentacionItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val asignatura: String,
    val evaluacion: String,
    val alumnoObjetivo: String = "Todos los Alumnos",
    val nota: String,
    val comentario: String,
    val docenteAutor: String,
    val fecha: String
)
