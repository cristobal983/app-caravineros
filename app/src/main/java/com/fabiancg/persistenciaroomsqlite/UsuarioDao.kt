package com.fabiancg.persistenciaroomsqlite

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para operaciones de autenticación y gestión de usuarios en SQLite.
 */
@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun registrar(usuario: Usuario): Long

    @Query("SELECT * FROM usuarios WHERE LOWER(email) = LOWER(:identificador) OR rut = :identificador LIMIT 1")
    suspend fun buscarPorEmailORut(identificador: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE (LOWER(email) = LOWER(:identificador) OR rut = :identificador) AND contrasena = :contrasena LIMIT 1")
    suspend fun login(identificador: String, contrasena: String): Usuario?

    @Query("SELECT * FROM usuarios ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<Usuario>>

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contarUsuarios(): Int
}
