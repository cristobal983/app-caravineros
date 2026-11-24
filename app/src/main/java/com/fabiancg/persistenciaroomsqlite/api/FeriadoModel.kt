package com.fabiancg.persistenciaroomsqlite.api

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos para la API de Feriados de Chile.
 * Fuente oficial: https://apis.digital.gob.cl/fl/feriados/{año}
 *
 * Cada objeto representa un feriado con su fecha, nombre, tipo y ley que lo establece.
 * @SerializedName mapea el nombre del JSON al campo Kotlin.
 */
data class Feriado(
    @SerializedName("fecha")
    val fecha: String,           // Ej: "2026-01-01"

    @SerializedName("nombre")
    val nombre: String,          // Ej: "Año Nuevo"

    @SerializedName("tipo")
    val tipo: String,            // Ej: "Civil" o "Religioso"

    @SerializedName("leyes")
    val leyes: List<String>      // Leyes que establecen el feriado
)
