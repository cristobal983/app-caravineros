package com.fabiancg.persistenciaroomsqlite.logic

/**
 * Contexto resumido de la aplicacion para que el asistente responda con datos reales.
 */
data class ContextoAsistente(
    val nombreUsuario: String = "Usuario",
    val rolUsuario: String = "Alumno",
    val totalDocumentos: Int = 0,
    val alertasSinLeer: Int = 0,
    val totalRetroalimentaciones: Int = 0,
    val ultimoDocumento: String? = null,
    val ultimaEvaluacion: String? = null
)

/**
 * Motor del asistente academico ESFOCAR.
 * Procesa consultas en lenguaje natural sobre documentos, alertas,
 * evaluaciones, normativa y funciones de la aplicacion.
 */
object AsistenteIALogic {

    fun generarRespuesta(consulta: String, ctx: ContextoAsistente): String {
        val texto = consulta.trim().lowercase()
        if (texto.isBlank()) {
            return "Escribe una consulta para poder ayudarte."
        }

        return when {
            contieneAlguno(texto, "hola", "buenas", "saludos", "buenos dias", "buenas tardes") -> {
                "Hola ${ctx.nombreUsuario} (${ctx.rolUsuario}). Soy el asistente virtual de ESFOCAR. " +
                    "Actualmente tienes ${ctx.alertasSinLeer} alerta(s) sin leer y ${ctx.totalDocumentos} documento(s) disponibles. " +
                    "Puedes consultarme sobre rubricas, alertas, retroalimentaciones, notas o feriados."
            }

            contieneAlguno(texto, "alerta", "aviso", "notificacion", "pendiente") -> {
                if (ctx.alertasSinLeer == 0) {
                    "No tienes alertas pendientes por leer. Cada vez que un docente suba un documento " +
                        "o publique la retroalimentacion de una prueba, aparecera una alerta automatica."
                } else {
                    "Tienes ${ctx.alertasSinLeer} alerta(s) sin leer en la campana superior. " +
                        "Recuerda que las alertas llegan tanto a docentes como a alumnos."
                }
            }

            contieneAlguno(texto, "documento", "rubrica", "archivo", "pdf", "word") -> {
                val detalleUltimo = ctx.ultimoDocumento?.let { " El ultimo archivo cargado es '$it'." } ?: ""
                val permiso = if (ctx.rolUsuario.equals("Docente", ignoreCase = true)) {
                    "Como Docente puedes subir y eliminar documentos desde la pestana Rubricas."
                } else {
                    "Como Alumno puedes abrir y revisar todos los PDF o Word publicados."
                }
                "Hay ${ctx.totalDocumentos} documento(s) guardados en la base de datos local.$detalleUltimo $permiso"
            }

            contieneAlguno(texto, "retroalimentacion", "prueba", "nota", "evaluacion", "calificacion") -> {
                val detalleEval = ctx.ultimaEvaluacion?.let { " La mas reciente corresponde a '$it'." } ?: ""
                "Existen ${ctx.totalRetroalimentaciones} retroalimentacion(es) registradas.$detalleEval " +
                    "En la escala chilena de 1.0 a 7.0, la nota minima de aprobacion en ESFOCAR es 4.0."
            }

            contieneAlguno(texto, "feriado", "calendario", "festivo") -> {
                "Puedes revisar el calendario oficial de feriados nacionales de Chile en la pestana Feriados, " +
                    "el cual consulta en tiempo real la API publica Nager.Date."
            }

            contieneAlguno(texto, "comunicado", "spring", "servidor", "backend") -> {
                "La seccion Comunicados Institucionales se conecta al microservicio propio en Spring Boot " +
                    "(endpoint /api/comunicados). Permite listar, publicar y eliminar avisos oficiales."
            }

            contieneAlguno(texto, "ayuda", "que puedes hacer", "opciones", "menu") -> {
                "Puedo ayudarte con:\n" +
                    "- Resumen de alertas y documentos cargados\n" +
                    "- Estado de retroalimentaciones y escala de notas\n" +
                    "- Guia de permisos entre Docente y Alumno\n" +
                    "- Informacion sobre feriados y comunicados Spring Boot"
            }

            else -> {
                "Entendi tu consulta sobre '$consulta'. En este momento la app cuenta con " +
                    "${ctx.totalDocumentos} documento(s), ${ctx.totalRetroalimentaciones} retroalimentacion(es) " +
                    "y ${ctx.alertasSinLeer} alerta(s) sin leer. Si buscas algo especifico, prueba preguntando por " +
                    "'documentos', 'alertas', 'retroalimentacion', 'feriados' o 'comunicados'."
            }
        }
    }

    private fun contieneAlguno(texto: String, vararg palabras: String): Boolean {
        return palabras.any { texto.contains(it) }
    }
}
