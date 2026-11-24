package com.fabiancg.persistenciaroomsqlite

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas instrumentadas de la tabla "usuarios" con SQLite REAL.
 *
 * A diferencia de las pruebas unitarias (Módulo 1), estas sí usan
 * una base de datos Room en memoria que corre dentro del emulador o dispositivo.
 * Esto verifica que las consultas SQL están bien escritas.
 *
 * Para ejecutar: conectar emulador o dispositivo y correr esta clase
 * desde Android Studio, o usar:
 *   .\gradlew.bat :app:connectedDebugAndroidTest
 *
 * Patrón: @Before prepara la BD → @Test ejecuta → @After cierra la BD
 */
@RunWith(AndroidJUnit4::class)
class UsuarioDaoInstrumentedTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: UsuarioDao

    // ─── Configuración: se ejecuta ANTES de cada test ────────────────────────
    @Before
    fun prepararBaseDeDatos() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // inMemoryDatabaseBuilder: BD temporal que solo existe durante el test
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // Permitido solo en tests
            .build()
        dao = db.usuarioDao()
    }

    // ─── Limpieza: se ejecuta DESPUÉS de cada test ───────────────────────────
    @After
    fun cerrarBaseDeDatos() {
        db.close()
    }

    // ─── Helper: crea un usuario de prueba ESFOCAR ───────────────────────────
    private fun crearUsuario(
        rut: String = "12345678-9",
        email: String = "alumno@esfocar.cl",
        contrasena: String = "clave123",
        rol: String = "Alumno"
    ) = Usuario(
        rut = rut,
        nombre = "Cristóbal Pérez",
        email = email,
        contrasena = contrasena,
        rol = rol,
        escalafon = "Aspirante a Carabinero",
        fechaRegistro = "2026-11-24"
    )

    // ─── Tests de inserción ──────────────────────────────────────────────────

    @Test
    fun insertar_unUsuario_contadorEsUno() = runBlocking {
        // Arrange
        val usuario = crearUsuario()
        // Act
        dao.insertar(usuario)
        // Assert
        assertEquals("Debe haber 1 usuario en SQLite", 1, dao.contarUsuarios())
    }

    @Test
    fun insertar_dosUsuarios_contadorEsDos() = runBlocking {
        dao.insertar(crearUsuario(rut = "11111111-1", email = "uno@esfocar.cl"))
        dao.insertar(crearUsuario(rut = "22222222-2", email = "dos@esfocar.cl"))
        assertEquals(2, dao.contarUsuarios())
    }

    @Test
    fun insertar_retornaIdPositivo() = runBlocking {
        // Arrange + Act
        val id = dao.insertar(crearUsuario())
        // Assert
        assertTrue("El ID generado por SQLite debe ser mayor a 0", id > 0)
    }

    // ─── Tests de inicio de sesión ───────────────────────────────────────────

    @Test
    fun iniciarSesion_conEmailYClaveCorrectos_retornaUsuario() = runBlocking {
        // Arrange
        dao.insertar(crearUsuario(email = "alumno@esfocar.cl", contrasena = "clave123"))
        // Act
        val resultado = dao.iniciarSesion(login = "alumno@esfocar.cl", clave = "clave123")
        // Assert
        assertNotNull("Login correcto debe retornar el usuario desde SQLite", resultado)
        assertEquals("alumno@esfocar.cl", resultado?.email)
    }

    @Test
    fun iniciarSesion_conRutYClaveCorrectos_retornaUsuario() = runBlocking {
        // Arrange
        dao.insertar(crearUsuario(rut = "12345678-9", contrasena = "clave123"))
        // Act
        val resultado = dao.iniciarSesion(login = "12345678-9", clave = "clave123")
        // Assert
        assertNotNull("Login con RUT debe funcionar en SQLite real", resultado)
        assertEquals("12345678-9", resultado?.rut)
    }

    @Test
    fun iniciarSesion_claveIncorrecta_retornaNull() = runBlocking {
        // Arrange
        dao.insertar(crearUsuario(email = "alumno@esfocar.cl", contrasena = "clave123"))
        // Act
        val resultado = dao.iniciarSesion(login = "alumno@esfocar.cl", clave = "INCORRECTA")
        // Assert
        assertNull("Login con clave incorrecta debe retornar null en SQLite", resultado)
    }

    @Test
    fun iniciarSesion_usuarioNoExiste_retornaNull() = runBlocking {
        // Act (BD vacía)
        val resultado = dao.iniciarSesion(login = "fantasma@esfocar.cl", clave = "clave123")
        // Assert
        assertNull("Usuario inexistente debe retornar null", resultado)
    }

    // ─── Tests de búsqueda por ID ────────────────────────────────────────────

    @Test
    fun obtenerPorId_idExistente_retornaDatosCorrectos() = runBlocking {
        // Arrange
        val id = dao.insertar(crearUsuario(email = "alumno@esfocar.cl"))
        // Act
        val resultado = dao.obtenerPorId(id.toInt())
        // Assert
        assertNotNull(resultado)
        assertEquals("alumno@esfocar.cl", resultado?.email)
        assertEquals("Alumno", resultado?.rol)
    }

    @Test
    fun obtenerPorId_idInexistente_retornaNull() = runBlocking {
        val resultado = dao.obtenerPorId(9999)
        assertNull("ID inexistente debe retornar null en SQLite", resultado)
    }

    // ─── Tests de eliminación ────────────────────────────────────────────────

    @Test
    fun eliminarPorId_usuarioExistente_reduceCantidad() = runBlocking {
        // Arrange
        val id = dao.insertar(crearUsuario())
        // Act
        val filasEliminadas = dao.eliminarPorId(id.toInt())
        // Assert
        assertEquals("Debe eliminar exactamente 1 fila", 1, filasEliminadas)
        assertEquals("BD debe quedar vacía", 0, dao.contarUsuarios())
    }

    @Test
    fun eliminarPorId_idInexistente_noAfectaNada() = runBlocking {
        // Arrange
        dao.insertar(crearUsuario())
        // Act
        val filasEliminadas = dao.eliminarPorId(9999)
        // Assert
        assertEquals("Eliminar ID inexistente no debe afectar filas", 0, filasEliminadas)
        assertEquals("El usuario original debe seguir existiendo", 1, dao.contarUsuarios())
    }

    // ─── Tests de actualización ──────────────────────────────────────────────

    @Test
    fun actualizar_cambioNombre_persisteEnSQLite() = runBlocking {
        // Arrange
        val id = dao.insertar(crearUsuario())
        val usuarioOriginal = dao.obtenerPorId(id.toInt())!!
        val usuarioActualizado = usuarioOriginal.copy(nombre = "Nuevo Nombre ESFOCAR")
        // Act
        dao.actualizar(usuarioActualizado)
        // Assert
        val resultado = dao.obtenerPorId(id.toInt())
        assertEquals("El nombre debe actualizarse en SQLite", "Nuevo Nombre ESFOCAR", resultado?.nombre)
    }

    // ─── Tests de roles ──────────────────────────────────────────────────────

    @Test
    fun bdVacia_contadorEsCero() = runBlocking {
        assertEquals("BD nueva debe tener 0 usuarios", 0, dao.contarUsuarios())
    }

    @Test
    fun insertar_docenteYAlumno_ambosPersisten() = runBlocking {
        // Arrange
        dao.insertar(crearUsuario(rut = "11111111-1", email = "docente@esfocar.cl", rol = "Docente"))
        dao.insertar(crearUsuario(rut = "22222222-2", email = "alumno@esfocar.cl", rol = "Alumno"))
        // Assert
        assertEquals("Debe haber 2 usuarios en total", 2, dao.contarUsuarios())
    }
}
