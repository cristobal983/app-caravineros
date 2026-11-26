package com.fabiancg.persistenciaroomsqlite.api

data class Comunicado(
    val id: Long? = null,
    val titulo: String,
    val detalle: String,
    val unidad: String = "Dirección ESFOCAR",
    val fecha: String = "",
    val prioridad: String = "Normal"
)
