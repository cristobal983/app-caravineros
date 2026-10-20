package com.fabiancg.persistenciaroomsqlite

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para la entidad [IntentoEvaluacion].
 * Proporciona métodos de acceso y manipulación de datos sobre la tabla "intentos_evaluacion".
 *
 * Incluye tanto métodos suspendibles para corrutinas (insertar, eliminar)
 * como flujos reactivos [Flow] para observar cambios en tiempo real desde Jetpack Compose.
 */
@Dao
interface IntentoEvaluacionDao {

    /**
     * Inserta un nuevo intento de evaluación en la base de datos.
     * Si ya existe un registro con el mismo ID, lo reemplaza.
     * Retorna el rowId generado por SQLite.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(intento: IntentoEvaluacion): Long

    /**
     * Inserta una lista de intentos (útil para precarga de datos iniciales o pruebas).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarVarios(intentos: List<IntentoEvaluacion>)

    /**
     * Observa todos los intentos de evaluación en tiempo real, ordenados de más reciente a más antiguo.
     * Al devolver [Flow], la interfaz de Compose se recompone automáticamente cada vez que los datos cambian.
     */
    @Query("SELECT * FROM intentos_evaluacion ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<IntentoEvaluacion>>

    /**
     * Lista todos los intentos mediante una llamada suspendida única.
     */
    @Query("SELECT * FROM intentos_evaluacion ORDER BY id DESC")
    suspend fun listarTodos(): List<IntentoEvaluacion>

    /**
     * Obtiene el promedio global de porcentaje de aprobación directamente mediante agregación SQL.
     */
    @Query("SELECT AVG(porcentaje) FROM intentos_evaluacion")
    fun obtenerPromedio(): Flow<Double?>

    /**
     * Obtiene el número total de evaluaciones rendidas.
     */
    @Query("SELECT COUNT(*) FROM intentos_evaluacion")
    fun contarTotal(): Flow<Int>

    /**
     * Elimina un intento específico por su identificador primario.
     * Retorna el número de filas afectadas.
     */
    @Query("DELETE FROM intentos_evaluacion WHERE id = :id")
    suspend fun eliminarPorId(id: Int): Int

    /**
     * Elimina todos los registros de la tabla de intentos.
     */
    @Query("DELETE FROM intentos_evaluacion")
    suspend fun eliminarTodos(): Int
}
