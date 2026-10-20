package com.fabiancg.persistenciaroomsqlite

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para la gestión local de documentos y rúbricas (PDF / Word).
 */
@Dao
interface DocumentoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(documento: DocumentoItem): Long

    @Query("SELECT * FROM documentos_rubricas ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<DocumentoItem>>

    @Query("SELECT * FROM documentos_rubricas WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Int): DocumentoItem?

    @Query("DELETE FROM documentos_rubricas WHERE id = :id")
    suspend fun eliminarPorId(id: Int): Int

    @Query("SELECT COUNT(*) FROM documentos_rubricas")
    fun contarTotal(): Flow<Int>
}
