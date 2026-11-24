package com.fabiancg.persistenciaroomsqlite.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Interfaz Retrofit para consumir la API de Feriados de Chile.
 *
 * URL base: https://apis.digital.gob.cl/fl/
 * Endpoint: feriados/{año}  →  retorna lista de Feriado en JSON
 *
 * Esta API es gratuita, sin autenticación y del Gobierno de Chile.
 * Es relevante para ESFOCAR ya que Carabineros opera con
 * calendarios institucionales que dependen de los feriados nacionales.
 */
interface FeriadosApiService {

    @GET("feriados/{anio}")
    suspend fun obtenerFeriados(
        @Path("anio") anio: Int
    ): List<Feriado>
}

/**
 * Singleton que provee la instancia configurada de Retrofit.
 * Se accede como: RetrofitClient.feriadosApi
 */
object RetrofitClient {

    private const val BASE_URL = "https://apis.digital.gob.cl/fl/"

    val feriadosApi: FeriadosApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FeriadosApiService::class.java)
    }
}
