# Guía y Resumen Didáctico — App ESFOCAR (Nivel Académico)
**Asignatura:** DSY1105 — Duoc UC  
**Evaluación:** Encargo con Presentación (Nota 2)  
**Tecnologías:** Kotlin, Jetpack Compose, Material Design 3, Room SQLite, FileProvider  

---

## 1. Requerimientos Cumplidos de la Pauta

| Requerimiento | Dónde se encuentra | Cómo explicarlo ante el profesor |
| :--- | :--- | :--- |
| **Pantalla / Vista de bienvenida** | `HomeScreen.kt` | "Al ingresar se muestra la bienvenida con el nombre del aspirante, su RUT y dos tarjetas de acceso rápido a los módulos principales." |
| **Registro de usuarios** | `AuthScreen.kt` | "Formulario con nombre, RUT, correo y contraseña. Si los datos son válidos, se guardan en la tabla SQLite usando Room." |
| **Formularios validados** | `AuthScreen.kt` y `ProfileScreen.kt` | "Validamos con sentencias if sencillas: nombre de al menos 3 caracteres, RUT con guion, correo con @ y contraseña de 6+ caracteres." |
| **Iniciar sesión** | `AuthScreen.kt` | "Consulta a SQLite con email/RUT y contraseña mediante `usuarioDao.iniciarSesion()`. Si coincide, guarda la sesión y avanza." |
| **Editar datos de usuario** | `ProfileScreen.kt` | "El aspirante puede presionar 'Editar' en su perfil, modificar su nombre, correo o clave y presionar 'Guardar Cambios' para actualizar el registro en SQLite con `@Update`." |
| **Cargar documentos (rúbricas)** | `DocumentsScreen.kt` | "Usa `ActivityResultContracts.OpenDocument()` para elegir un PDF o Word del teléfono, copiarlo al almacenamiento interno privado y registrarlo en SQLite." |
| **Ver documentos** | `DocumentsScreen.kt` | "Utiliza `FileProvider` con un `Intent(ACTION_VIEW)` seguro para abrir el lector predeterminado del celular (Adobe Reader o Drive)." |
| **Eliminar documento** | `DocumentsScreen.kt` | "Muestra un diálogo de confirmación `AlertDialog`. Al aceptar, borra el archivo físico de la memoria y elimina la fila de SQLite." |
| **Cerrar sesión** | `ProfileScreen.kt` y Menú lateral | "Diálogo de confirmación que limpia la sesión en `SessionManager` y regresa fluidamente a la pantalla de Login." |
| **Menú y Barras de Navegación** | `MainScreen.kt` | "Menú lateral con `ModalNavigationDrawer`, barra superior `TopAppBar` y barra inferior `NavigationBar`." |
| **Animaciones** | En todas las pantallas | "Transiciones animadas suaves con `Crossfade` entre pantallas y `AnimatedVisibility` para mostrar/ocultar avisos de error y confirmación." |

---

## 2. Estructura de Archivos (Fácil de Explicar)

* **`AppDatabase.kt`**: Clase central de Room SQLite con 3 tablas limpias (`Persona`, `Usuario`, `DocumentoItem`).
* **`Usuario.kt` / `UsuarioDao.kt`**: Modela los alumnos con métodos para insertar, actualizar y loguear.
* **`DocumentoItem.kt` / `DocumentoDao.kt`**: Modela los archivos PDF y Word con métodos para guardar, listar y eliminar.
* **`SessionManager.kt`**: Guarda la sesión del usuario activo en `SharedPreferences` para no perderla al girar la pantalla o reabrir la app.
* **`ui/screens/AuthScreen.kt`**: Vista de Login y Registro con validaciones paso a paso.
* **`ui/screens/HomeScreen.kt`**: Vista de bienvenida con accesos directos.
* **`ui/screens/DocumentsScreen.kt`**: Módulo nativo para cargar, visualizar y eliminar rúbricas (incluye botón "Cargar Rúbricas" con archivos de prueba listos).
* **`ui/screens/ProfileScreen.kt`**: Ficha del alumno, editor de datos y botón para cerrar sesión.
* **`ui/MainScreen.kt`**: Organiza el Menú lateral, la Barra Superior y la Barra Inferior.
