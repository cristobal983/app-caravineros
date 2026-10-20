package com.fabiancg.persistenciaroomsqlite

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room que representa la tabla "intentos_evaluacion" en SQLite.
 * Almacena el resultado histórico de cada evaluación o trivia rendida por el aspirante.
 *
 * Mapeo conceptual SQL:
 * CREATE TABLE intentos_evaluacion (
 *     id INTEGER PRIMARY KEY AUTOINCREMENT,
 *     materia TEXT NOT NULL,
 *     fecha TEXT NOT NULL,
 *     aciertos INTEGER NOT NULL,
 *     total INTEGER NOT NULL,
 *     porcentaje INTEGER NOT NULL
 * );
 */
@Entity(tableName = "intentos_evaluacion")
data class IntentoEvaluacion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val materia: String,
    val fecha: String,
    val aciertos: Int,
    val total: Int,
    val porcentaje: Int
)
