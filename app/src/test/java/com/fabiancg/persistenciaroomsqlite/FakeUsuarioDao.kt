package com.fabiancg.persistenciaroomsqlite

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * DAO simulado (Fake) para pruebas unitarias locales.
 *
 * Este archivo SOLO existe en src/test/ y NUNCA se incluye en la app real.
 * Implementa la interfaz UsuarioDao usando una lista en memoria,
 * lo que permite testear la lógica del repositorio sin necesitar
 * una base de datos SQLite real ni un emulador Android.
 *
 * Patrón utilizado: Fake Object (xUnit Test Patterns)
 */
class FakeUsuarioDao : UsuarioDao {

    // Almacén en memoria que simula la tabla "usuarios"
    val usuarios = mutableListOf<Usuario>()

    // Contador de llamadas a insertar (útil para verificar que se llamó exactamente N veces)
    var llamadasInsertar = 0

    // Contador de llamadas a iniciarSesion
    var llamadasLogin = 0

    override suspend fun insertar(usuario: Usuario): Long {
        llamadasInsertar++
        // Simular autoincremento de ID
        val nuevoId = (usuarios.maxOfOrNull { it.id } ?: 0) + 1
        val usuarioConId = usuario.copy(id = nuevoId)
        usuarios.add(usuarioConId)
        return nuevoId.toLong()
    }

    override suspend fun actualizar(usuario: Usuario): Int {
        val indice = usuarios.indexOfFirst { it.id == usuario.id }
        return if (indice >= 0) {
            usuarios[indice] = usuario
            1
        } else 0
    }

    override suspend fun iniciarSesion(login: String, clave: String): Usuario? {
        llamadasLogin++
        return usuarios.firstOrNull {
            (it.email == login || it.rut == login) && it.contrasena == clave
        }
    }

    override suspend fun obtenerPorId(id: Int): Usuario? {
        return usuarios.firstOrNull { it.id == id }
    }

    override suspend fun eliminarPorId(id: Int): Int {
        val antes = usuarios.size
        usuarios.removeAll { it.id == id }
        return antes - usuarios.size
    }

    override suspend fun contarUsuarios(): Int = usuarios.size

    override fun obtenerTodosLosUsuarios(): Flow<List<Usuario>> = flowOf(usuarios.toList())

    override fun obtenerUsuariosPorRol(rol: String): Flow<List<Usuario>> =
        flowOf(usuarios.filter { it.rol == rol })
}
