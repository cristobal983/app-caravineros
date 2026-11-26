package com.fabiancg.persistenciaroomsqlite.api

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ComunicadosApiService {

    @GET("api/comunicados")
    suspend fun listar(): List<Comunicado>

    @POST("api/comunicados")
    suspend fun crear(@Body comunicado: Comunicado): Comunicado

    @DELETE("api/comunicados/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Unit>
}

object BackendClient {

    // 10.0.2.2 apunta al localhost del computador desde el emulador Android
    const val DEFAULT_BASE_URL = "http://10.0.2.2:8080/"

    fun crearServicio(baseUrl: String = DEFAULT_BASE_URL): ComunicadosApiService {
        val urlNormalizada = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        return Retrofit.Builder()
            .baseUrl(urlNormalizada)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ComunicadosApiService::class.java)
    }
}
