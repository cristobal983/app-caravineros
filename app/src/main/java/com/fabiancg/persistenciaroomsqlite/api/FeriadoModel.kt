package com.fabiancg.persistenciaroomsqlite.api

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos para la API de Feriados de Chile (Nager.Date API).
 * Fuente: https://date.nager.at/api/v3/PublicHolidays/{año}/CL
 *
 * Mapea los feriados oficiales de Chile de forma segura (Null-Safety) ante serializadores Gson.
 */
data class Feriado(
    @SerializedName("date")
    val fecha: String? = null,

    @SerializedName("localName")
    val nombre: String? = null,

    @SerializedName("name")
    val nombreIngles: String? = null,

    @SerializedName("global")
    val esNacional: Boolean? = true,

    @SerializedName("types")
    val types: List<String>? = null,

    @SerializedName("leyes")
    val leyes: List<String>? = null
) {
    val fechaSegura: String
        get() = fecha ?: ""

    val nombreSeguro: String
        get() = nombre ?: nombreIngles ?: "Feriado Oficial"

    // Clasificación amigable para la interfaz gráfica
    val tipo: String
        get() = if (esNacional != false) "Nacional" else "Regional"
}
