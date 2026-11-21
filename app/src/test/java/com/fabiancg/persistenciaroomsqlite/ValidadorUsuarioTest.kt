package com.fabiancg.persistenciaroomsqlite

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas unitarias de lógica pura de validación de Usuario.
 *
 * NO necesitan emulador ni base de datos — se ejecutan directamente en la JVM.
 * Patrón AAA: Arrange (preparar), Act (actuar), Assert (verificar).
 *
 * Para ejecutar sin emulador en Android Studio:
 *   Clic derecho sobre esta clase → Run 'ValidadorUsuarioTest'
 * O desde terminal:
 *   .\gradlew.bat :app:testDebugUnitTest
 */
class ValidadorUsuarioTest {

    // ─── Helpers ────────────────────────────────────────────────────────────────

    /** Valida que el RUT tenga formato chileno básico: solo dígitos + guion + dígito/K */
    private fun rutEsValido(rut: String): Boolean {
        val limpio = rut.trim().uppercase()
        return Regex("^[0-9]{7,8}-[0-9K]$").matches(limpio)
    }

    /** Valida formato de email con arroba y punto */
    private fun emailEsValido(email: String): Boolean {
        return Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$").matches(email.trim())
    }

    /** La contraseña debe tener al menos 6 caracteres */
    private fun contrasenaEsValida(clave: String): Boolean = clave.length >= 6

    /** El nombre no puede estar vacío ni tener solo espacios */
    private fun nombreEsValido(nombre: String): Boolean = nombre.isNotBlank()

    // ─── Tests de RUT ───────────────────────────────────────────────────────────

    @Test
    fun rutValido_conFormatoCorrectoDiez() {
        // Arrange
        val rut = "12345678-9"
        // Act
        val resultado = rutEsValido(rut)
        // Assert
        assertTrue("El RUT con 8 dígitos y dígito verificador numérico debe ser válido", resultado)
    }

    @Test
    fun rutValido_conVerificadorK() {
        // Arrange
        val rut = "9876543-K"
        // Act
        val resultado = rutEsValido(rut)
        // Assert
        assertTrue("El RUT con verificador K debe ser válido", resultado)
    }

    @Test
    fun rutInvalido_sinGuion() {
        assertFalse("RUT sin guion debe ser inválido", rutEsValido("123456789"))
    }

    @Test
    fun rutInvalido_vacio() {
        assertFalse("RUT vacío debe ser inválido", rutEsValido(""))
    }

    @Test
    fun rutInvalido_conLetrasEnCuerpo() {
        assertFalse("RUT con letras en el cuerpo debe ser inválido", rutEsValido("ABCDE12-3"))
    }

    // ─── Tests de Email ─────────────────────────────────────────────────────────

    @Test
    fun emailValido_formatoCorrecto() {
        // Arrange
        val email = "alumno@esfocar.cl"
        // Act
        val resultado = emailEsValido(email)
        // Assert
        assertTrue("Email institucional ESFOCAR debe ser válido", resultado)
    }

    @Test
    fun emailInvalido_sinArroba() {
        assertFalse("Email sin @ debe ser inválido", emailEsValido("alumnoesfocar.cl"))
    }

    @Test
    fun emailInvalido_vacio() {
        assertFalse("Email vacío debe ser inválido", emailEsValido(""))
    }

    // ─── Tests de Contraseña ────────────────────────────────────────────────────

    @Test
    fun contrasenaValida_seisoMasCaracteres() {
        assertTrue("Contraseña de 6 caracteres debe ser válida", contrasenaEsValida("abc123"))
    }

    @Test
    fun contrasenaInvalida_menosDeSeis() {
        assertFalse("Contraseña de 5 caracteres debe ser inválida", contrasenaEsValida("12345"))
    }

    @Test
    fun contrasenaInvalida_vacia() {
        assertFalse("Contraseña vacía debe ser inválida", contrasenaEsValida(""))
    }

    // ─── Tests de Nombre ────────────────────────────────────────────────────────

    @Test
    fun nombreValido_conTextoNormal() {
        assertTrue("Nombre con texto debe ser válido", nombreEsValido("Juan Pérez"))
    }

    @Test
    fun nombreInvalido_soloEspacios() {
        assertFalse("Nombre con solo espacios debe ser inválido", nombreEsValido("   "))
    }

    @Test
    fun nombreInvalido_vacio() {
        assertFalse("Nombre vacío debe ser inválido", nombreEsValido(""))
    }
}
