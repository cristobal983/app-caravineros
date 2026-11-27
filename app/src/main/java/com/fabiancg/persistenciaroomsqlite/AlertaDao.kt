package com.fabiancg.persistenciaroomsqlite

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(alerta: AlertaItem): Long

    @Query("SELECT * FROM alertas ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<AlertaItem>>

    @Query("SELECT COUNT(*) FROM alertas WHERE leida = 0")
    fun contarNoLeidas(): Flow<Int>

    @Query("UPDATE alertas SET leida = 1 WHERE id = :id")
    suspend fun marcarComoLeida(id: Int)

    @Query("UPDATE alertas SET leida = 1")
    suspend fun marcarTodasComoLeidas()

    @Query("DELETE FROM alertas WHERE id = :id")
    suspend fun eliminarPorId(id: Int)
}
