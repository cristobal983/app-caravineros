package com.fabiancg.persistenciaroomsqlite

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO para la tabla "usuarios" en SQLite.
 * Permite:
 * 1. Registrar usuarios (Insert)
 * 2. Iniciar sesión (Select con email/RUT y contraseña)
 * 3. Editar datos de usuario (Update)
 */
@Dao
interface UsuarioDao {

    // Registrar nuevo usuario
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(usuario: Usuario): Long

    // Editar / Actualizar datos del usuario existente
    @Update
    suspend fun actualizar(usuario: Usuario): Int

    // Iniciar sesión
    @Query("SELECT * FROM usuarios WHERE (email = :login OR rut = :login) AND contrasena = :clave LIMIT 1")
    suspend fun iniciarSesion(login: String, clave: String): Usuario?

    // Buscar por ID
    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): Usuario?

    // Contar usuarios registrados
    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contarUsuarios(): Int
}
