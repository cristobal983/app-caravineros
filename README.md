# PersistenciaRoomSQLite — Guía de instalación y estudio

## 1. ¿Qué hace esta aplicación?

Proyecto didáctico de **Android con Kotlin, Jetpack Compose y Room (SQLite)**. Permite:

1. **Guardar** una persona con nombre y edad.
2. **Listar personas** almacenadas en la base de datos.
3. **Eliminar todos** los registros de la tabla `personas`.

Los datos se guardan en el almacenamiento privado de la aplicación y permanecen al cerrarla y volverla a abrir, salvo que se borren los datos de la aplicación, se desinstale sin restauración o se eliminen expresamente.

## 2. Configuración identificada en este proyecto

| Componente | Versión o valor del proyecto |
| --- | --- |
| Android Gradle Plugin (AGP) | `9.2.1` |
| Kotlin | `2.2.10` |
| KSP | `2.2.10-2.0.2` |
| Room | `2.8.5` |
| Gradle Wrapper | `9.4.1` |
| Compose BOM | `2026.02.01` |
| compileSdk | Android API `37`, minor API `1` |
| minSdk / targetSdk | `33` / `36` |
| Paquete | `com.fabiancg.persistenciaroomsqlite` |
| Archivo SQLite | `personas.db` |

**Nota:** estas son las versiones *observadas en el ZIP*, no una recomendación de actualizar todos los proyectos a dichas versiones. Para reproducir el ejemplo conviene conservarlas y utilizar el SDK y JDK compatibles con AGP 9.2.1. La compilación no se ha ejecutado como parte de esta revisión.

## 3. Abrir y ejecutar el proyecto

1. Descomprimir `PersistenciaRoomSQLite.zip`.
2. Abrir Android Studio → **File > Open** y seleccionar la carpeta `PersistenciaRoomSQLite`.
3. Permitir la sincronización de Gradle (**Sync Project with Gradle Files**).
4. Verificar que Android Studio tenga instalado el SDK configurado (API 37 y minor API 1) y un JDK compatible con el AGP de la aplicación.
5. Seleccionar un emulador o dispositivo con Android 13 (API 33) o superior.
6. Ejecutar la aplicación.
7. Escribir un nombre y una edad positiva; pulsar **Guardar**. Pulsar **Listar personas** para ver los registros. Pulsar **Eliminar todos** para vaciar la tabla.

> Atención: **Eliminar todos** no solicita confirmación y la eliminación no puede deshacerse mediante esta interfaz.

## 4. Archivos que permiten utilizar Room y KSP

### A. `gradle/libs.versions.toml`: registrar KSP

En `[versions]` está definida esta versión:

```toml
kotlin = "2.2.10"
ksp = "2.2.10-2.0.2"
```

En `[plugins]` se registra el complemento:

```toml
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

**¿Qué significa?** El catálogo centraliza las versiones y crea el alias `libs.plugins.ksp`, utilizado en Gradle. No es necesario duplicar estas claves si ya existen.

### B. `app/build.gradle.kts`: activar KSP

En `plugins` el proyecto utiliza:

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp) // Habilita la generación de código con KSP
}
```

**¿Qué significa?** KSP analiza las anotaciones que utiliza Room durante la compilación y permite que el compilador de Room genere implementaciones concretas de la base de datos y del DAO.

### C. `app/build.gradle.kts`: dependencias de Room

Dentro de `dependencies` aparecen:

```kotlin
// Componentes necesarios para utilizar Room sobre SQLite.
implementation("androidx.room:room-runtime:2.8.5")

// Artefacto de compatibilidad con extensiones Kotlin de Room.
// En Room 2.8.5, las principales API de corrutinas ya están en room-runtime.
implementation("androidx.room:room-ktx:2.8.5")

// Procesador que genera el código de Room durante la compilación.
ksp("androidx.room:room-compiler:2.8.5")
```

**¿Qué significa `implementation`?** Incorpora una biblioteca que utiliza la aplicación. **¿Qué significa `ksp`?** Registra un procesador de código que se ejecuta al compilar: no agrega una base de datos ni se ejecuta al presionar los botones.

### D. `gradle.properties`: compatibilidad con Kotlin integrado en AGP 9

Este ZIP contiene:

```properties
# Permite que KSP registre las fuentes Kotlin generadas.
android.disallowKotlinSourceSets=false
```

La configuración evita, en este entorno, el error:

```text
Using kotlin.sourceSets DSL to add Kotlin sources is not allowed with built-in Kotlin.
Solution: Use android.sourceSets DSL instead.
```

**Importante:** se trata de una medida de compatibilidad para el conjunto de versiones utilizado en el proyecto, no de una propiedad que todas las aplicaciones Android deban agregar. Al actualizar AGP y KSP, conviene revisar si sigue siendo necesaria.

### E. `build.gradle.kts` de nivel proyecto

En el ZIP real se encuentra:

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
```

**No figura `alias(libs.plugins.ksp) apply false` en este archivo.** KSP ya se aplica en `app/build.gradle.kts`; no debe enseñarse como si fuera un cambio efectivamente realizado en este proyecto. Declararlo con `apply false` en la raíz puede utilizarse para organizar proyectos con varios módulos, pero no es obligatorio aquí.

## 5. ¿Dónde está cada componente de Room?

Los archivos Kotlin se encuentran en:

`app/src/main/java/com/fabiancg/persistenciaroomsqlite/`

| Archivo | Función |
| --- | --- |
| `Persona.kt` | Define la entidad `Persona` y la tabla `personas`. |
| `PersonaDAO.kt` | Define las consultas y operaciones de inserción, selección y eliminación. |
| `AppDatabase.kt` | Declara la base de datos Room y ofrece acceso a `PersonaDAO`. |
| `MainActivity.kt` | Crea/abre la base de datos y muestra la interfaz con Jetpack Compose. |

### A. `Persona.kt`: la tabla

```kotlin
@Entity(tableName = "personas")
data class Persona(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val edad: Int
)
```

- `@Entity`: indica a Room que `Persona` representa una tabla.
- `@PrimaryKey(autoGenerate = true)`: define un identificador único que SQLite genera al insertar.
- `nombre` y `edad`: columnas de la tabla.

**Equivalencia SQL conceptual:**

```sql
CREATE TABLE personas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    edad INTEGER NOT NULL
);
```

### B. `PersonaDAO.kt`: las operaciones

```kotlin
@Dao
interface PersonaDAO {
    @Insert
    suspend fun insertar(persona: Persona)

    @Query("SELECT * FROM personas")
    suspend fun listar(): List<Persona>

    @Query("DELETE FROM personas")
    suspend fun eliminarTodos()
}
```

- `@Dao`: interfaz de acceso a datos.
- `@Insert`: inserta un registro.
- `@Query("SELECT ...")`: recupera los registros.
- `@Query("DELETE ...")`: elimina **todos** los registros, pero **no** la tabla.
- `suspend`: permite llamar estas operaciones desde una corrutina.

### C. `AppDatabase.kt`: configuración de Room

```kotlin
@Database(entities = [Persona::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun personaDao(): PersonaDAO
}
```

`entities` indica las tablas y `version` la versión del esquema. Cuando cambie el esquema en futuras versiones será necesario planificar una migración (o una estrategia explícita de recreación para datos descartables).

### D. `MainActivity.kt`: interfaz y acciones

La Activity crea la base de datos mediante:

```kotlin
val db = Room.databaseBuilder(
    applicationContext,
    AppDatabase::class.java,
    "personas.db"
).build()
```

La instancia se construye **dentro de `onCreate()` pero antes de `setContent`**, por lo que las recomposiciones de Compose no vuelven a ejecutar `Room.databaseBuilder()`.

```kotlin
// Se ejecuta cuando se crea la Activity.
val db = Room.databaseBuilder(
    applicationContext,
    AppDatabase::class.java,
    "personas.db"
).build()

// Obtiene el objeto de acceso a datos (DAO).
val dao = db.personaDao()

// El DAO ya creado se entrega a la pantalla Compose.
setContent {
    Column(modifier = Modifier.padding(50.dp)) {
        PantallaPersonas(dao)
    }
}
```

El DAO se obtiene **una sola vez en `onCreate()`** y se entrega a `PantallaPersonas(dao)`. En la función `@Composable` se usan `remember` y `mutableStateOf` para el nombre, la edad y el mensaje, y `rememberCoroutineScope()` con `scope.launch { ... }` para llamar las funciones suspendibles del DAO. **Si la Activity se recrea** (por ejemplo, durante una rotación), `onCreate()` se vuelve a ejecutar y puede crear una nueva instancia de Room, pero los registros persistidos no se pierden.

**Comportamiento de los botones:**

| Botón | Código principal | Resultado |
| --- | --- | --- |
| Guardar | `dao.insertar(Persona(nombre = nombre, edad = edadNumero))` | Inserta una fila tras validar los campos. |
| Listar personas | `dao.listar()` | Consulta todos los registros y los convierte a texto. |
| Eliminar todos | `dao.eliminarTodos()` | Borra todas las filas y limpia los campos. |

El ingreso de `edad` se convierte con `toIntOrNull()`; se exige nombre no vacío y edad mayor que cero.

**Comportamiento exacto de `Listar personas` en la versión actual:**

```kotlin
val personas = dao.listar()
resultado = if (personas.isEmpty()) {
    "No existen personas registradas"
} else {
    personas.joinToString("\n") {
        "${it.nombre} - ${it.edad} años"
    }
}
```

Si la tabla está vacía, se muestra el mensaje **«No existen personas registradas»**. Si contiene personas, se presenta cada una en una línea con el formato `Nombre - Edad años`.

**Comportamiento exacto de `Eliminar todos`:** después de `dao.eliminarTodos()`, la interfaz asigna `""` a las variables `nombre` y `edad` y muestra **«Todos los registros fueron eliminados»**. No borra la tabla, sino sus filas.

**Al guardar:** el mensaje cambia a **«Persona guardada»**, pero los campos de nombre y edad **no se limpian automáticamente** en la versión actual.

## 6. ¿Dónde se guarda el archivo SQLite?

La base de datos puede encontrarse mediante **Android Studio > App Inspection > Database Inspector** mientras la aplicación está ejecutándose en un dispositivo compatible. Allí se puede revisar la tabla `personas` y sus registros.

## 7. Actividad breve de comprobación

1. Insertar `Juan, 25` y `Ana, 22`.
2. Pulsar **Listar personas** y comprobar que se muestren ambos registros.
3. Cerrar la aplicación, volver a abrirla y volver a listar; los datos deben seguir presentes.
4. Pulsar **Eliminar todos**.
5. Listar nuevamente: debe aparecer **«No existen personas registradas»**.
6. Insertar otra persona y observar el identificador en Database Inspector.

**Preguntas de reflexión:** ¿Qué diferencia hay entre `remember` y Room? ¿Qué función cumple `PersonaDAO`? ¿Por qué `DELETE FROM personas` no elimina la tabla? ¿Por qué Room necesita un procesador como KSP?

## 8. Observaciones técnicas de la versión actual

**Cambios que ya están aplicados en `MainActivity.kt`:**

1. **Room se inicializa fuera de `setContent`:** ahora la instancia se crea antes de construir la interfaz, evitando construirla otra vez por una recomposición.
2. **Se reutiliza el DAO:** se declara `val dao = db.personaDao()` y se llama `PantallaPersonas(dao)`.
3. **Listado vacío con mensaje:** cuando `dao.listar()` devuelve una lista vacía, se muestra «No existen personas registradas».
4. **Imports simplificados:** la última versión compartida ya no contiene las importaciones de `Scaffold`, `Preview`, `fillMaxSize` ni del tema de Compose que antes estaban sin usar.

**Ideas para una próxima versión (todavía no implementadas):**

- **Conservar una instancia para toda la aplicación:** hoy `onCreate()` vuelve a construir la base de datos si se recrea la Activity. Se puede implementar un Singleton o una inicialización a nivel de `Application`.
- **Limpiar los campos después de guardar:** actualmente solo se limpian al presionar «Eliminar todos».
- **Manejar errores de lectura y escritura:** incorporar `try/catch` según los objetivos de aprendizaje.
- **Confirmar eliminaciones:** antes de ejecutar `DELETE FROM personas`, mostrar un diálogo de confirmación.
- **Evitar padding duplicado:** hoy hay `50.dp` en `setContent` y `16.dp` dentro de `PantallaPersonas`. Puede eliminarse el `Column` exterior si se desea simplificar.
- **Agregar UPDATE o DELETE por ID:** completar las operaciones CRUD utilizando el identificador de cada persona.

## 9. Problemas frecuentes

**`Unresolved reference 'ksp'`**

- Comprobar la entrada `ksp` en las secciones `[versions]` y `[plugins]` de `libs.versions.toml`.
- Comprobar `alias(libs.plugins.ksp)` en `app/build.gradle.kts`.
- Sincronizar Gradle.

**`Using kotlin.sourceSets DSL ... not allowed with built-in Kotlin`**

- En **esta combinación de versiones**, comprobar que `gradle.properties` contiene `android.disallowKotlinSourceSets=false`.
- Repetir la sincronización y compilación.
- Considerar actualizar de forma coordinada AGP y KSP en proyectos nuevos.

**SDK ausente o incompatibilidad de Gradle/JDK**

- Usar el Gradle Wrapper que viene en el ZIP (`gradlew` / `gradlew.bat`).
- Instalar la plataforma Android y el JDK requeridos por la versión de AGP configurada.
- Leer el mensaje completo de compilación antes de modificar versiones.

## 10. Resumen conceptual

- **SQLite**: motor que guarda tablas y registros en el dispositivo.
- **Room**: biblioteca que simplifica el acceso a SQLite desde Kotlin.
- **Entity**: representa una tabla.
- **DAO**: declara las operaciones sobre los datos.
- **Database**: configura la base de datos Room.
- **KSP**: procesa anotaciones durante la compilación para que Room genere código.
- **Compose**: muestra la interfaz y actualiza sus componentes cuando cambia el estado.

---

*Documento actualizado con las modificaciones de `MainActivity.kt` compartidas posteriormente en la conversación. Las configuraciones de Gradle y los demás archivos siguen basados en el proyecto ZIP revisado. No se ejecutó una compilación Android de esta última versión.*
