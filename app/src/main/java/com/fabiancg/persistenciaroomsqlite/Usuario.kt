package com.fabiancg.persistenciaroomsqlite

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Room que representa la tabla "usuarios" en SQLite.
 * Modela a los alumnos / aspirantes de la Escuela de Formación de Carabineros (ESFOCAR).
 * Permite el registro e inicio de sesión local (Offline-first).
 */
@Entity(
    tableName = "usuarios",
    indices = [
        Index(value = ["email"], unique = true),
        Index(value = ["rut"], unique = true)
    ]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val rut: String,
    val nombre: String,
    val email: String,
    val contrasena: String,
    val rol: String = "Alumno", // "Docente" o "Alumno"
    val escalafon: String = "Aspirante a Carabinero",
    val fechaRegistro: String
)
