package com.fabiancg.persistenciaroomsqlite.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Interfaz Retrofit para consumir la API pública y estable de Feriados de Chile (Nager.Date).
 *
 * URL base: https://date.nager.at/api/v3/
 * Endpoint: PublicHolidays/{anio}/CL  →  retorna la lista de feriados oficiales en formato JSON
 *
 * Esta API es pública, de alta disponibilidad, gratuita y no requiere clave (API Key).
 * Es relevante para ESFOCAR ya que Carabineros opera con calendarios de servicio
 * y turnos institucionales sujetos a feriados nacionales y regionales.
 */
interface FeriadosApiService {

    @GET("PublicHolidays/{anio}/CL")
    suspend fun obtenerFeriados(
        @Path("anio") anio: Int
    ): List<Feriado>
}

/**
 * Singleton que provee la instancia configurada de Retrofit.
 * Se accede como: RetrofitClient.feriadosApi
 */
object RetrofitClient {

    private const val BASE_URL = "https://date.nager.at/api/v3/"

    val feriadosApi: FeriadosApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FeriadosApiService::class.java)
    }
}

