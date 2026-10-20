package com.fabiancg.persistenciaroomsqlite

import androidx.room.Entity
import androidx.room.PrimaryKey

// Crea una tabla llamada "personas".
@Entity(tableName = "personas")
data class Persona(

    // Identificador único autoincremental.
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Nombre de la persona.
    val nombre: String,

    // Edad de la persona.
    val edad: Int
)

/*
    Equivale conceptualmente a crear esta tabla SQL:

    CREATE TABLE personas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    edad INTEGER NOT NULL
    );
*/