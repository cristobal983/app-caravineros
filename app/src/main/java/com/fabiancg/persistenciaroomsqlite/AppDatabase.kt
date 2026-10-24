package com.fabiancg.persistenciaroomsqlite

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de Datos Room SQLite de la aplicación.
 * Versión simplificada y limpia para nivel académico:
 * 1. Persona (ejercicio base de clase)
 * 2. Usuario (registro, login y edición de datos de usuario)
 * 3. DocumentoItem (archivos nativos PDF y Word)
 */
@Database(
    entities = [
        Persona::class,
        Usuario::class,
        DocumentoItem::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun personaDao(): PersonaDAO
    abstract fun usuarioDao(): UsuarioDao
    abstract fun documentoDao(): DocumentoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "personas.db"
                )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}