package com.fabiancg.persistenciaroomsqlite

import android.content.Context
import android.content.SharedPreferences

/**
 * Gestor de sesión simple y seguro para persistir el usuario activo.
 * Permite mantener la sesión iniciada o cerrarla adecuadamente.
 */
object SessionManager {
    private const val PREFS_NAME = "esfocar_session_prefs"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_RUT = "user_rut"
    private const val KEY_USER_ROL = "user_rol"
    private const val KEY_USER_ESCALAFON = "user_escalafon"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun guardarSesion(context: Context, usuario: Usuario) {
        getPrefs(context).edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putInt(KEY_USER_ID, usuario.id)
            putString(KEY_USER_NAME, usuario.nombre)
            putString(KEY_USER_EMAIL, usuario.email)
            putString(KEY_USER_RUT, usuario.rut)
            putString(KEY_USER_ROL, usuario.rol)
            putString(KEY_USER_ESCALAFON, usuario.escalafon)
            apply()
        }
    }

    fun estaLogueado(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun obtenerUsuarioActivo(context: Context): Usuario? {
        val prefs = getPrefs(context)
        if (!prefs.getBoolean(KEY_IS_LOGGED_IN, false)) return null

        return Usuario(
            id = prefs.getInt(KEY_USER_ID, 0),
            rut = prefs.getString(KEY_USER_RUT, "") ?: "",
            nombre = prefs.getString(KEY_USER_NAME, "") ?: "",
            email = prefs.getString(KEY_USER_EMAIL, "") ?: "",
            contrasena = "", // No se almacena la contraseña en sesión por seguridad
            rol = prefs.getString(KEY_USER_ROL, "Alumno") ?: "Alumno",
            escalafon = prefs.getString(KEY_USER_ESCALAFON, "Aspirante a Carabinero") ?: "Aspirante a Carabinero",
            fechaRegistro = ""
        )
    }

    fun cerrarSesion(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}
