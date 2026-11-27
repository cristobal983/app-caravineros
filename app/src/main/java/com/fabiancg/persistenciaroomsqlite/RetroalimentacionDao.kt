package com.fabiancg.persistenciaroomsqlite

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RetroalimentacionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(item: RetroalimentacionItem): Long

    @Query("SELECT * FROM retroalimentaciones ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<RetroalimentacionItem>>

    @Query("DELETE FROM retroalimentaciones WHERE id = :id")
    suspend fun eliminarPorId(id: Int)
}
