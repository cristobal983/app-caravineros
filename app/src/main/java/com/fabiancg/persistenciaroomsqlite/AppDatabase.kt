package com.fabiancg.persistenciaroomsqlite

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Define las entidades y la versión de la base de datos local SQLite con Room.
@Database(
    entities = [
        Persona::class,
        IntentoEvaluacion::class,
        Usuario::class,
        DocumentoItem::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // DAO de personas (ejercicio y actividades previas de clase)
    abstract fun personaDao(): PersonaDAO

    // DAO de intentos de autoevaluación / trivias ESFOCAR
    abstract fun intentoEvaluacionDao(): IntentoEvaluacionDao

    // DAO de usuarios (registro y login con validaciones)
    abstract fun usuarioDao(): UsuarioDao

    // DAO de documentos y rúbricas (archivos nativos PDF y Word)
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