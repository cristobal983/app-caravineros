package com.fabiancg.persistenciaroomsqlite

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PersonaDAO {

    // Inserta una persona en la tabla.
    @Insert
    suspend fun insertar(persona: Persona)

    // Recupera todas las personas.
    @Query("SELECT * FROM personas")
    suspend fun listar(): List<Persona>

    // Elimina todos los registros de la tabla personas.
    @Query("DELETE FROM personas")
    suspend fun eliminarTodos()

    // Eliminar personas por edad
    @Query("DELETE FROM personas WHERE edad = :edad")
    suspend fun eliminarPorEdad(edad: Int): Int
}