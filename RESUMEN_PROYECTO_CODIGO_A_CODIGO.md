# Resumen Detallado Código por Código — App de Estudio ESFOCAR
**Asignatura:** DSY1105 — Duoc UC  
**Evaluación:** Encargo con Presentación (Nota 2)  
**Arquitectura:** MVVM + Offline-First con Room SQLite  
**Tecnologías:** Kotlin, Jetpack Compose, Material Design 3, Corrutinas & Flow, FileProvider  

---

## 1. Cumplimiento de Rúbricas y Requerimientos

| Requerimiento de la Pauta | Estado | Implementación y Archivos |
| :--- | :---: | :--- |
| **Diseño visual con Material 3** | Cumplido | Paleta institucional verde/dorado ESFOCAR en `Color.kt` y `Theme.kt`, tipografía dinámica, `Card`, `Scaffold`, `NavigationBar`. |
| **Formularios validados** | Cumplido | `AuthScreen.kt`: Validación reactiva de RUT con dígito verificador, correo institucional (regex), contraseña (mínimo 6 caracteres) y confirmación coincidente. |
| **Navegación funcional** | Cumplido | `MainScreen.kt` con triple navegación: Menú lateral (`ModalNavigationDrawer`), Barra superior dinámica (`TopAppBar`) y Barra inferior (`NavigationBar`). |
| **Gestión de estado** | Cumplido | `SessionManager.kt` para la persistencia del usuario activo, `rememberSaveable`, `mutableStateOf` y `StateFlow`/`Flow` reactivo con Room. |
| **Almacenamiento local (SQLite Room)** | Cumplido | `AppDatabase.kt` (v3) con 4 entidades: `Usuario`, `DocumentoItem`, `IntentoEvaluacion` y `Persona`. |
| **Recursos nativos (Archivos: PDF, Word)** | Cumplido | `DocumentsScreen.kt`: Carga mediante selector nativo de Android, copia local en `filesDir`, visualización con `FileProvider` y eliminación con confirmación. |
| **Animaciones** | Cumplido | `Crossfade` entre pantallas y en login, `AnimatedVisibility` para errores/éxito, y animaciones de contenido. |
| **Vistas específicas** | Cumplido | Registro, Iniciar Sesión, Cierre de Sesión / Perfil, Cargar documentos, Visualizar documentos, Eliminar documentos. |

---

## 2. Estructura y Explicación Código por Código

### A. Capa de Datos y Persistencia Local (Room SQLite)

#### 1. `Usuario.kt`
* **Ubicación:** `app/src/main/java/com/fabiancg/persistenciaroomsqlite/Usuario.kt`
* **Propósito:** Entidad Room que modela la tabla `usuarios` en SQLite para el registro de aspirantes.
* **Campos clave:** `id` (autoincremental), `rut` (con índice único), `nombre`, `email` (con índice único), `contrasena`, `escalafon` y `fechaRegistro`.
* **Explicación pedagógica:** Garantiza que los alumnos puedan crearse cuentas y autenticarse sin necesidad de conexión a internet (Offline-First).

#### 2. `UsuarioDao.kt`
* **Ubicación:** `app/src/main/java/com/fabiancg/persistenciaroomsqlite/UsuarioDao.kt`
* **Propósito:** Data Access Object para operaciones sobre la tabla `usuarios`.
* **Consultas destacadas:**
  * `registrar(usuario: Usuario)`: Inserta un nuevo aspirante.
  * `buscarPorEmailORut(identificador: String)`: Busca duplicados antes de registrar.
  * `login(identificador: String, contrasena: String)`: Valida credenciales contra la base de datos local SQLite.

#### 3. `DocumentoItem.kt`
* **Ubicación:** `app/src/main/java/com/fabiancg/persistenciaroomsqlite/DocumentoItem.kt`
* **Propósito:** Entidad Room que mapea la tabla `documentos_rubricas`.
* **Campos clave:** `id`, `nombre`, `tipo` ("PDF" o "WORD"), `tamanoBytes`, `rutaArchivo` (ruta interna privada), `fechaCarga`, `categoria`.
* **Explicación pedagógica:** Almacena los metadatos de los archivos nativos cargados por el usuario para asociar el archivo físico con la base de datos.

#### 4. `DocumentoDao.kt`
* **Ubicación:** `app/src/main/java/com/fabiancg/persistenciaroomsqlite/DocumentoDao.kt`
* **Propósito:** DAO para registrar, listar de forma reactiva (`Flow<List<DocumentoItem>>`) y eliminar documentos (`eliminarPorId`).

#### 5. `IntentoEvaluacion.kt` e `IntentoEvaluacionDao.kt`
* **Ubicación:** `app/src/main/java/com/fabiancg/persistenciaroomsqlite/IntentoEvaluacion.kt`
* **Propósito:** Almacena los resultados históricos de las trivias (materia, fecha, aciertos, total, porcentaje).
* **Consultas DAO:** Métodos reactivos `obtenerTodos()`, cálculo de promedio global mediante agregación SQL `AVG(porcentaje)` y conteo total `COUNT(*)`.

#### 6. `Persona.kt` y `PersonaDAO.kt`
* **Ubicación:** `app/src/main/java/com/fabiancg/persistenciaroomsqlite/Persona.kt`
* **Propósito:** Mantiene la compatibilidad con el ejercicio inicial del curso (nombre, edad) y la tarea de eliminación condicional por edad.

#### 7. `AppDatabase.kt`
* **Ubicación:** `app/src/main/java/com/fabiancg/persistenciaroomsqlite/AppDatabase.kt`
* **Propósito:** Clase abstracta central que hereda de `RoomDatabase`. Contiene la lista de entidades (`entities = [Persona::class, IntentoEvaluacion::class, Usuario::class, DocumentoItem::class]`), versión de esquema 3 y migración segura con `fallbackToDestructiveMigration(dropAllTables = true)`. Implementa el patrón Singleton para reutilizar la conexión SQLite.

---

### B. Capa de Sesión y Configuración Nativa

#### 8. `SessionManager.kt`
* **Ubicación:** `app/src/main/java/com/fabiancg/persistenciaroomsqlite/SessionManager.kt`
* **Propósito:** Encapsula `SharedPreferences` para persistir la sesión activa del usuario. Permite saber si hay un usuario logueado, consultar sus datos y cerrarla de forma segura.

#### 9. `AndroidManifest.xml` y `res/xml/file_paths.xml`
* **Propósito:** Declaran el `androidx.core.content.FileProvider`.
* **Explicación pedagógica:** En Android moderno (API 24 a 37), está prohibido compartir rutas `file://` con otras aplicaciones por seguridad (produce `FileUriExposedException`). `FileProvider` genera URIs seguras `content://` con permisos temporales de lectura (`FLAG_GRANT_READ_URI_PERMISSION`) para que visores externos de PDF y Word abran los documentos nativamente.

---

### C. Capa de Diseño e Interfaz de Usuario (Jetpack Compose Material 3)

#### 10. `ui/theme/Color.kt` y `ui/theme/Theme.kt`
* **Propósito:** Sistema de diseño Material Design 3 con los colores oficiales de Carabineros de Chile / ESFOCAR (verde bosque `#0D5E38`, verde contenedor `#B9F1CD`, dorado `#8B6B00` y variantes para modo oscuro y claro).

#### 11. `ui/navigation/NavigationItem.kt`
* **Propósito:** `sealed class` que define las rutas, títulos e íconos vectoriales de la aplicación (`Home`, `Study`, `Quiz`, `Cases`, `Documents`, `Progress`, `Profile`, `Demo`). Define las listas para la barra inferior (`bottomBarItems`) y para el menú lateral (`drawerItems`).

#### 12. `ui/screens/AuthScreen.kt` (Vista de Registro e Inicio de Sesión)
* **Propósito:** Pantalla de autenticación con selector de pestañas animado.
* **Formularios validados didácticamente:**
  * **Nombre:** Validación de longitud mínima (3 letras).
  * **RUT:** Valida formato chileno (8 o 9 dígitos con guión y dígito verificador).
  * **Correo:** Expresión regular RFC para verificar estructura de email institucional.
  * **Contraseña:** Mínimo 6 caracteres.
  * **Confirmación de contraseña:** Verificación de igualdad estricta con la clave.
  * **Feedback:** Campos con `isError = true` y texto de ayuda explicativo en rojo (`supportingText`).
  * **Botón Demo:** Acceso rápido para que el profesor pueda entrar con un solo toque.

#### 13. `ui/screens/ProfileScreen.kt` (Vista de Cierre de Sesión)
* **Propósito:** Muestra la ficha del aspirante activo (nombre, RUT, correo, escalafón, modalidad Offline-First).
* **Cierre de sesión:** Botón con diálogo modal de confirmación (`AlertDialog`). Al confirmar, limpia `SessionManager` y devuelve al usuario a la vista de login con animación.

#### 14. `ui/screens/DocumentsScreen.kt` (Cargar, Visualizar y Eliminar Documentos Nativos)
* **Propósito:** Gestión completa de archivos PDF y Word (rúbricas de evaluación).
* **Cargar documentos:** Usa `rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument())` para invocar el explorador de archivos nativo del teléfono, copia el stream a almacenamiento interno privado (`filesDir`) y guarda el registro en Room SQLite.
* **Visualizar documentos:** Lanza un `Intent(Intent.ACTION_VIEW)` con `FileProvider` para abrir el visor nativo del dispositivo (Adobe Reader, Google Drive, Office). Cuenta con diálogo de fallback informativo.
* **Eliminar documentos:** Diálogo de confirmación que elimina el archivo físico y borra el registro de SQLite, actualizando la lista reactivamente en tiempo real.
* **Precarga de rúbricas:** Botón "Cargar Rúbricas" que crea 3 documentos de prueba (Tiro policial, Detención en flagrancia y Reglamento) para demostración en vivo.

#### 15. `ui/screens/HomeScreen.kt`
* **Propósito:** Panel principal con tarjetas de bienvenida, métricas en tiempo real (total de trivias rendidas y porcentaje promedio calculados mediante Room) y acceso directo a todos los módulos.

#### 16. `ui/screens/QuizScreen.kt`
* **Propósito:** Módulo interactivo de autoevaluación con barra de progreso animada, feedback explicativo por pregunta y guardado automático del resultado en la tabla `intentos_evaluacion`.

#### 17. `ui/screens/CasesScreen.kt`
* **Propósito:** Módulo de análisis de situaciones y toma de decisiones táctico-policiales con retroalimentación normativa.

#### 18. `ui/screens/ProgressScreen.kt`
* **Propósito:** Historial reactivo con `Flow` que lista cada intento de evaluación guardado en Room SQLite, con cálculo de promedios y opción de vaciado con confirmación.

#### 19. `DemoPersistenciaScreen.kt` y `EliminarPorEdadActivity.kt`
* **Propósito:** Conservan el laboratorio previo de clase sobre `remember`, `rememberSaveable`, operaciones CRUD sobre `personas` y la tarea de eliminación condicional por edad.

#### 20. `ui/MainScreen.kt` (Coordinador de Navegación)
* **Propósito:** Componente raíz que orquesta:
  * Control de sesión: Si no hay usuario activo, muestra `AuthScreen`. Si está autenticado, muestra la aplicación completa.
  * `ModalNavigationDrawer`: Menú lateral con cabecera personalizada y listado de secciones.
  * `TopAppBar`: Barra superior con menú hamburguesa, título dinámico y acceso al perfil.
  * `NavigationBar`: Barra inferior con 5 accesos directos.
  * Transición de pantallas fluida con `Crossfade`.

#### 21. `MainActivity.kt`
* **Propósito:** Punto de entrada Android (`ComponentActivity`), activa `enableEdgeToEdge()` y envuelve `MainScreen` en el tema institucional `PersistenciaRoomSQLiteTheme`.

---

## 3. Instrucciones para GitHub y Trello (Rúbrica)

### A. Repositorio en GitHub
1. Crear un nuevo repositorio en GitHub:
   ```bash
   git init
   git add .
   git commit -m "Implementación de Encargo Nota 2: Material 3, Auth con validaciones, Room SQLite y Documentos nativos"
   git branch -M main
   git remote add origin https://github.com/<tu-usuario>/app-study-esfocar.git
   git push -u origin main
   ```
2. **Añadir al profesor al repositorio:**
   * Entrar al repositorio en GitHub → pestaña **Settings** (Configuración).
   * Menú izquierdo: **Collaborators** (Colaboradores).
   * Pulsar **Add people**, buscar el usuario o correo del profesor y pulsar **Add to this repository**.

### B. Tablero en Trello
1. Crear un tablero llamado: `DSY1105 - App Estudio ESFOCAR`.
2. Estructurar las columnas clásicas de Kanban:
   * **Backlog / Por Hacer:** "Diseño Material 3", "Validación formularios", "Autenticación", "Gestión PDF/Word".
   * **En Proceso:** "Pruebas de visualización con FileProvider".
   * **En Revisión:** "Verificación de persistencia SQLite".
   * **Completado / Done:** "Navegación triple (Drawer, TopBar, BottomBar)", "Login y Registro", "Carga/Borrado de rúbricas".
3. **Añadir al profesor al tablero:**
   * En la parte superior del tablero pulsar **Compartir / Share**.
   * Ingresar el correo del docente y asignar rol de miembro para que pueda evaluar la planificación ágil.
