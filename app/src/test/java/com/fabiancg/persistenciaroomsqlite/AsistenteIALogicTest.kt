package com.fabiancg.persistenciaroomsqlite

import com.fabiancg.persistenciaroomsqlite.logic.AsistenteIALogic
import com.fabiancg.persistenciaroomsqlite.logic.ContextoAsistente
import org.junit.Assert.assertTrue
import org.junit.Test

class AsistenteIALogicTest {

    private val contextoBase = ContextoAsistente(
        nombreUsuario = "Cristobal",
        rolUsuario = "Alumno",
        totalDocumentos = 3,
        alertasSinLeer = 2,
        totalRetroalimentaciones = 1,
        ultimoDocumento = "Rubrica_DSY1105.pdf",
        ultimaEvaluacion = "DSY1105 - Prueba 3"
    )

    @Test
    fun saludo_incluyeNombreYResumen() {
        val resp = AsistenteIALogic.generarRespuesta("Hola", contextoBase)
        assertTrue(resp.contains("Cristobal"))
        assertTrue(resp.contains("2 alerta"))
        assertTrue(resp.contains("3 documento"))
    }

    @Test
    fun consultaAlertas_indicaCantidadPendiente() {
        val resp = AsistenteIALogic.generarRespuesta("¿Tengo alertas pendientes?", contextoBase)
        assertTrue(resp.contains("2 alerta"))
        assertTrue(resp.contains("docentes como a alumnos"))
    }

    @Test
    fun consultaDocumentos_mencionaUltimoArchivo() {
        val resp = AsistenteIALogic.generarRespuesta("¿Que documentos hay?", contextoBase)
        assertTrue(resp.contains("Rubrica_DSY1105.pdf"))
    }

    @Test
    fun consultaRetroalimentacion_mencionaUltimaEvaluacionYNotaMinima() {
        val resp = AsistenteIALogic.generarRespuesta("Ver retroalimentacion de prueba", contextoBase)
        assertTrue(resp.contains("DSY1105 - Prueba 3"))
        assertTrue(resp.contains("4.0"))
    }

    @Test
    fun consultaVacia_solicitaEscribirConsulta() {
        val resp = AsistenteIALogic.generarRespuesta("   ", contextoBase)
        assertTrue(resp.contains("Escribe una consulta"))
    }
}
