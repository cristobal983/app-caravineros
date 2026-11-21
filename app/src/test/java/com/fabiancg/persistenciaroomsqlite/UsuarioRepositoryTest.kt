package com.fabiancg.persistenciaroomsqlite

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Pruebas unitarias del comportamiento del repositorio de usuarios.
 *
 * Usa FakeUsuarioDao para aislar la lógica de negocio de la base de datos real.
 * NO necesitan emulador — se ejecutan en la JVM local.
 *
 * Patrón: Fake DAO + AAA (Arrange, Act, Assert)
 *
 * Para ejecutar sin emulador:
 *   .\gradlew.bat :app:testDebugUnitTest
 */
class UsuarioRepositoryTest {

    // ─── Helper: crea un usuario de prueba ──────────────────────────────────────
    private fun usuarioEsfocar(
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
        fechaRegistro = "2026-11-21"
    )

    // ─── Tests de registro (insertar) ───────────────────────────────────────────

    @Test
    fun insertarUsuario_incrementaContador() = runBlocking {
        // Arrange
        val fake = FakeUsuarioDao()
        // Act
        fake.insertar(usuarioEsfocar())
        // Assert
        assertEquals("Debe haber exactamente 1 usuario registrado", 1, fake.contarUsuarios())
    }

    @Test
    fun insertarDosUsuarios_contadorEsDos() = runBlocking {
        // Arrange
        val fake = FakeUsuarioDao()
        // Act
        fake.insertar(usuarioEsfocar(rut = "11111111-1", email = "uno@esfocar.cl"))
        fake.insertar(usuarioEsfocar(rut = "22222222-2", email = "dos@esfocar.cl"))
        // Assert
        assertEquals(2, fake.contarUsuarios())
    }

    @Test
    fun insertarUsuario_registraUnaLlamada() = runBlocking {
        // Arrange
        val fake = FakeUsuarioDao()
        // Act
        fake.insertar(usuarioEsfocar())
        // Assert
        assertEquals("Insertar debe llamarse exactamente una vez", 1, fake.llamadasInsertar)
    }

    // ─── Tests de inicio de sesión ──────────────────────────────────────────────

    @Test
    fun iniciarSesion_conEmailYClaveCorrectos_retornaUsuario() = runBlocking {
        // Arrange
        val fake = FakeUsuarioDao()
        fake.insertar(usuarioEsfocar(email = "alumno@esfocar.cl", contrasena = "clave123"))
        // Act
        val resultado = fake.iniciarSesion(login = "alumno@esfocar.cl", clave = "clave123")
        // Assert
        assertNotNull("Login con credenciales correctas debe retornar el usuario", resultado)
        assertEquals("alumno@esfocar.cl", resultado?.email)
    }

    @Test
    fun iniciarSesion_conRutYClaveCorrectos_retornaUsuario() = runBlocking {
        // Arrange
        val fake = FakeUsuarioDao()
        fake.insertar(usuarioEsfocar(rut = "12345678-9", contrasena = "clave123"))
        // Act
        val resultado = fake.iniciarSesion(login = "12345678-9", clave = "clave123")
        // Assert
        assertNotNull("Login con RUT correcto debe retornar el usuario", resultado)
    }

    @Test
    fun iniciarSesion_conClaveIncorrecta_retornaNull() = runBlocking {
        // Arrange
        val fake = FakeUsuarioDao()
        fake.insertar(usuarioEsfocar(email = "alumno@esfocar.cl", contrasena = "clave123"))
        // Act
        val resultado = fake.iniciarSesion(login = "alumno@esfocar.cl", clave = "claveMAL")
        // Assert
        assertNull("Login con clave incorrecta debe retornar null", resultado)
    }

    @Test
    fun iniciarSesion_sinUsuariosRegistrados_retornaNull() = runBlocking {
        // Arrange
        val fake = FakeUsuarioDao()
        // Act
        val resultado = fake.iniciarSesion(login = "nadie@esfocar.cl", clave = "clave123")
        // Assert
        assertNull("Login sin usuarios en BD debe retornar null", resultado)
        assertEquals("Debe registrarse 1 llamada al DAO aunque no haya usuarios", 1, fake.llamadasLogin)
    }

    // ─── Tests de búsqueda por ID ───────────────────────────────────────────────

    @Test
    fun obtenerPorId_idExistente_retornaUsuario() = runBlocking {
        // Arrange
        val fake = FakeUsuarioDao()
        fake.insertar(usuarioEsfocar())
        // Act
        val resultado = fake.obtenerPorId(1)
        // Assert
        assertNotNull("Buscar por ID existente debe retornar el usuario", resultado)
    }

    @Test
    fun obtenerPorId_idInexistente_retornaNull() = runBlocking {
        // Arrange
        val fake = FakeUsuarioDao()
        // Act
        val resultado = fake.obtenerPorId(999)
        // Assert
        assertNull("Buscar por ID inexistente debe retornar null", resultado)
    }

    // ─── Tests de eliminación ───────────────────────────────────────────────────

    @Test
    fun eliminarPorId_idExistente_reduceCantidad() = runBlocking {
        // Arrange
        val fake = FakeUsuarioDao()
        fake.insertar(usuarioEsfocar())
        // Act
        val filas = fake.eliminarPorId(1)
        // Assert
        assertEquals("Eliminar debe afectar 1 fila", 1, filas)
        assertEquals("Después de eliminar, el contador debe ser 0", 0, fake.contarUsuarios())
    }

    @Test
    fun eliminarPorId_idInexistente_noAfectaNada() = runBlocking {
        // Arrange
        val fake = FakeUsuarioDao()
        fake.insertar(usuarioEsfocar())
        // Act
        val filas = fake.eliminarPorId(999)
        // Assert
        assertEquals("Eliminar ID inexistente debe afectar 0 filas", 0, filas)
        assertEquals("El usuario original debe seguir existiendo", 1, fake.contarUsuarios())
    }

    // ─── Tests de roles (Alumno / Docente) ──────────────────────────────────────

    @Test
    fun filtrarPorRol_retornaSoloAlumnos() = runBlocking {
        // Arrange
        val fake = FakeUsuarioDao()
        fake.insertar(usuarioEsfocar(email = "alumno@esfocar.cl", rol = "Alumno"))
        fake.insertar(usuarioEsfocar(email = "docente@esfocar.cl", rol = "Docente", rut = "99999999-9"))
        // Act — collect del Flow (simplificado con runBlocking)
        var listaAlumnos = emptyList<Usuario>()
        fake.obtenerUsuariosPorRol("Alumno").collect { listaAlumnos = it }
        // Assert
        assertEquals("Solo debe haber 1 alumno", 1, listaAlumnos.size)
        assertEquals("Alumno", listaAlumnos.first().rol)
    }
}
