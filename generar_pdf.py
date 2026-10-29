import os
from reportlab.lib.pagesizes import letter
from reportlab.lib import colors
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, HRFlowable
)
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import inch

def generar_pdf():
    pdf_filename = "Guia_Presentacion_App_ESFOCAR.pdf"

    # Configuración de márgenes
    doc = SimpleDocTemplate(
        pdf_filename,
        pagesize=letter,
        leftMargin=36,
        rightMargin=36,
        topMargin=36,
        bottomMargin=36
    )

    styles = getSampleStyleSheet()

    # Colores institucionales
    VERDE_ESFOCAR = colors.HexColor("#0B5313")
    VERDE_CLARO = colors.HexColor("#E8F5E9")
    DORADO = colors.HexColor("#B78103")
    GRIS_TEXTO = colors.HexColor("#1A1A1A")
    GRIS_BORDE = colors.HexColor("#C8E6C9")

    # Estilos de párrafo en Español
    title_style = ParagraphStyle(
        "TitleStyle",
        parent=styles["Normal"],
        fontName="Helvetica-Bold",
        fontSize=18,
        leading=22,
        textColor=VERDE_ESFOCAR,
        alignment=1,
        spaceAfter=4
    )

    subtitle_style = ParagraphStyle(
        "SubTitleStyle",
        parent=styles["Normal"],
        fontName="Helvetica-Bold",
        fontSize=11,
        leading=14,
        textColor=DORADO,
        alignment=1,
        spaceAfter=10
    )

    h1_style = ParagraphStyle(
        "H1Style",
        parent=styles["Normal"],
        fontName="Helvetica-Bold",
        fontSize=12,
        leading=15,
        textColor=VERDE_ESFOCAR,
        spaceBefore=8,
        spaceAfter=6
    )

    body_style = ParagraphStyle(
        "BodyStyle",
        parent=styles["Normal"],
        fontName="Helvetica",
        fontSize=9,
        leading=12,
        textColor=GRIS_TEXTO,
        spaceAfter=3
    )

    body_bold = ParagraphStyle(
        "BodyBold",
        parent=body_style,
        fontName="Helvetica-Bold"
    )

    code_style = ParagraphStyle(
        "CodeStyle",
        parent=styles["Normal"],
        fontName="Courier-Bold",
        fontSize=8,
        leading=10,
        textColor=colors.HexColor("#0B5313")
    )

    elements = []

    # Encabezado del PDF
    elements.append(Paragraph("GUÍA DE ESTUDIO Y DEFENSE DEL PROYECTO APLICACIÓN ESFOCAR", title_style))
    elements.append(Paragraph("Escuela de Formación de Carabineros — Asignatura DSY1105 (Duoc UC)", subtitle_style))
    elements.append(HRFlowable(width="100%", thickness=2, color=VERDE_ESFOCAR, spaceAfter=8))

    # SECCIÓN 1: CONCEPTOS TÉCNICOS EN ESPAÑOL
    elements.append(Paragraph("1. Conceptos Fundamentales de la Base de Datos y Código", h1_style))

    conceptos_data = [
        [
            Paragraph("<b>Concepto</b>", body_bold),
            Paragraph("<b>¿Qué es y para qué sirve en Español?</b>", body_bold),
            Paragraph("<b>Archivo de Código</b>", body_bold)
        ],
        [
            Paragraph("<b>Base de Datos Local (Room SQLite)</b>", body_style),
            Paragraph("Almacenamiento interno en el teléfono. Permite que la app funcione <b>sin conexión a internet</b> (Modo Fuera de Línea / Offline-First).", body_style),
            Paragraph("<code>AppDatabase.kt</code>", code_style)
        ],
        [
            Paragraph("<b>Entidad (Entity / Tabla)</b>", body_style),
            Paragraph("Es la estructura o plantilla que define una <b>tabla de datos</b> con sus columnas (por ejemplo: datos de un usuario o de un documento).", body_style),
            Paragraph("<code>Usuario.kt<br/>DocumentoItem.kt</code>", code_style)
        ],
        [
            Paragraph("<b>DAO (Objeto de Acceso a Datos)</b>", body_style),
            Paragraph("Interfaz que contiene las <b>funciones de consulta</b> para guardar, leer, modificar o borrar registros de la base de datos local.", body_style),
            Paragraph("<code>UsuarioDao.kt<br/>DocumentoDao.kt</code>", code_style)
        ],
        [
            Paragraph("<b>Gestor de Sesión (SessionManager)</b>", body_style),
            Paragraph("Guarda el usuario activo en las preferencias locales del teléfono para mantener la sesión iniciada al cerrar la aplicación.", body_style),
            Paragraph("<code>SessionManager.kt</code>", code_style)
        ],
        [
            Paragraph("<b>Diseño de Interfaz (Jetpack Compose)</b>", body_style),
            Paragraph("Tecnología moderna de Android que permite diseñar la interfaz gráfica usando código dinámico en Kotlin.", body_style),
            Paragraph("<code>ui/screens/*</code>", code_style)
        ]
    ]

    t_conceptos = Table(conceptos_data, colWidths=[1.5*inch, 4.3*inch, 1.7*inch])
    t_conceptos.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), VERDE_CLARO),
        ('TEXTCOLOR', (0,0), (-1,0), VERDE_ESFOCAR),
        ('GRID', (0,0), (-1,-1), 0.5, GRIS_BORDE),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
        ('TOPPADDING', (0,0), (-1,-1), 4),
        ('BOTTOMPADDING', (0,0), (-1,-1), 4),
    ]))
    elements.append(t_conceptos)
    elements.append(Spacer(1, 8))

    # SECCIÓN 2: RESUMEN DE LAS 9 FUNCIONES EN ESPAÑOL
    elements.append(Paragraph("2. Resumen Didáctico de las 9 Funciones de la Aplicación", h1_style))

    funciones_data = [
        [
            Paragraph("<b>N°</b>", body_bold),
            Paragraph("<b>Función Requerida</b>", body_bold),
            Paragraph("<b>Explicación Didáctica (Qué hace la función)</b>", body_bold),
            Paragraph("<b>Ubicación en el Código Fuente</b>", body_bold)
        ],
        [
            Paragraph("1", body_bold),
            Paragraph("<b>Pantalla de Bienvenida</b>", body_style),
            Paragraph("Muestra la portada de presentación con el Escudo Oficial de la Escuela de Formación de Carabineros.", body_style),
            Paragraph("Encabezado en <code>AuthScreen.kt</code> con la imagen <code>escudo_esfocar</code>", body_style)
        ],
        [
            Paragraph("2", body_bold),
            Paragraph("<b>Registro de Usuarios</b>", body_style),
            Paragraph("Valida que el RUT tenga guion, el correo sea válido y guarda al nuevo aspirante en la base de datos.", body_style),
            Paragraph("Pestaña <i>Registrarse</i> en <code>AuthScreen.kt</code> llama a <code>usuarioDao.insertar(...)</code>", body_style)
        ],
        [
            Paragraph("3", body_bold),
            Paragraph("<b>Editar Datos de Usuario</b>", body_style),
            Paragraph("Permite al aspirante cambiar su Nombre Completo, Correo o Contraseña desde su Perfil.", body_style),
            Paragraph("Pantalla <code>ProfileScreen.kt</code> ejecuta <code>usuarioDao.actualizar(...)</code>", body_style)
        ],
        [
            Paragraph("4", body_bold),
            Paragraph("<b>Iniciar Sesión</b>", body_style),
            Paragraph("Verifica el usuario y contraseña contra la base de datos local y da acceso a la app.", body_style),
            Paragraph("Pestaña <i>Iniciar Sesión</i> en <code>AuthScreen.kt</code> usa <code>usuarioDao.iniciarSesion(...)</code>", body_style)
        ],
        [
            Paragraph("5", body_bold),
            Paragraph("<b>Vista de Bienvenida</b>", body_style),
            Paragraph("Pantalla principal tras ingresar. Saluda al aspirante y muestra el contador de documentos locales.", body_style),
            Paragraph("Pantalla <code>HomeScreen.kt</code> lee datos con <code>docDao.contarTotal()</code>", body_style)
        ],
        [
            Paragraph("6", body_bold),
            Paragraph("<b>Cargar Documentos</b>", body_style),
            Paragraph("Abre el navegador de archivos del teléfono para seleccionar PDFs o documentos Word.", body_style),
            Paragraph("Pantalla <code>DocumentsScreen.kt</code> usa el selector local y llama a <code>documentoDao.insertar(...)</code>", body_style)
        ],
        [
            Paragraph("7", body_bold),
            Paragraph("<b>Ver Documentos</b>", body_style),
            Paragraph("Muestra la lista de rúbricas guardadas y permite abrir el archivo en un lector externo.", body_style),
            Paragraph("Pantalla <code>DocumentsScreen.kt</code> lee la lista reactiva y abre el visor del teléfono", body_style)
        ],
        [
            Paragraph("8", body_bold),
            Paragraph("<b>Eliminar Documento</b>", body_style),
            Paragraph("Pide confirmación en pantalla y elimina el archivo de la memoria y de la base de datos.", body_style),
            Paragraph("Diálogo en <code>DocumentsScreen.kt</code> borra el archivo y llama a <code>documentoDao.eliminarPorId(...)</code>", body_style)
        ],
        [
            Paragraph("9", body_bold),
            Paragraph("<b>Cerrar Sesión</b>", body_style),
            Paragraph("Solicita confirmación, borra la sesión guardada y regresa a la pantalla de acceso.", body_style),
            Paragraph("En <code>ProfileScreen.kt</code> y Menú Lateral llama a <code>SessionManager.cerrarSesion(...)</code>", body_style)
        ]
    ]

    t_funciones = Table(funciones_data, colWidths=[0.3*inch, 1.8*inch, 2.7*inch, 2.7*inch])
    t_funciones.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), VERDE_ESFOCAR),
        ('TEXTCOLOR', (0,0), (-1,0), colors.white),
        ('GRID', (0,0), (-1,-1), 0.5, GRIS_BORDE),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
        ('TOPPADDING', (0,0), (-1,-1), 3),
        ('BOTTOMPADDING', (0,0), (-1,-1), 3),
    ]))
    elements.append(t_funciones)
    elements.append(Spacer(1, 8))

    # SECCIÓN 3: ESTRUCTURA DE NAVEGACIÓN Y RESUMEN
    elements.append(Paragraph("3. Coordinador de Navegación y Arquitectura", h1_style))

    arch_text = """
    • <b>Coordinador Principal (MainScreen.kt):</b> Es la pantalla contenedora que administra el <b>Menú Lateral Desplegable</b> (Navigation Drawer), la <b>Barra de Navegación Superior</b> (TopAppBar) y la <b>Barra Inferior</b> (NavigationBar). Transiciona de forma fluida entre pantallas.<br/>
    • <b>Rutas de Navegación (NavigationItem.kt):</b> Define las 3 secciones principales de la app (<i>Inicio</i>, <i>Rúbricas</i> y <i>Mi Perfil</i>) con inicialización segura (<code>by lazy</code>) para evitar errores.<br/>
    • <b>Proveedor de Archivos Seguros (FileProvider):</b> Configurado en el archivo del sistema <code>AndroidManifest.xml</code> para compartir de forma segura los archivos PDF y Word con el visor del teléfono.
    """
    elements.append(Paragraph(arch_text, body_style))

    # Pie de página / Recomendación para la Defensa
    elements.append(Spacer(1, 6))
    elements.append(HRFlowable(width="100%", thickness=1, color=DORADO, spaceAfter=6))

    tip_text = "<b>💡 Recomendación para la Presentación:</b> Explica al docente que la aplicación fue construida bajo el enfoque <b>Fuera de Línea (Offline-First)</b> utilizando la base de datos local <b>Room SQLite</b>, lo que asegura que los aspirantes a Carabinero puedan revisar sus rúbricas en terreno sin depender de señal de internet."

    tip_table = Table([[Paragraph(tip_text, body_style)]], colWidths=[7.5*inch])
    tip_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), VERDE_CLARO),
        ('BOX', (0,0), (-1,-1), 1, VERDE_ESFOCAR),
        ('TOPPADDING', (0,0), (-1,-1), 5),
        ('BOTTOMPADDING', (0,0), (-1,-1), 5),
        ('LEFTPADDING', (0,0), (-1,-1), 8),
        ('RIGHTPADDING', (0,0), (-1,-1), 8),
    ]))
    elements.append(tip_table)

    # Generar PDF
    doc.build(elements)
    print(f"PDF generado exitosamente en Español: {os.path.abspath(pdf_filename)}")

if __name__ == "__main__":
    generar_pdf()
