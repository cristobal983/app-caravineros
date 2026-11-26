# ESFOCAR App — Gestión Académica y Comunicados

Aplicación móvil Android desarrollada para la Escuela de Formación de Carabineros (ESFOCAR) en el marco de la asignatura **DSY1105 — Desarrollo de Aplicaciones Móviles (Duoc UC)**.

---

## 1. Arquitectura y Tecnologías

- **Lenguaje:** Kotlin + Jetpack Compose (Material Design 3)
- **Persistencia local:** Room SQLite (`personas.db`) con enfoque *Offline-first*
- **Sesión:** `SharedPreferences` mediante `SessionManager`
- **Consumo de APIs:** Retrofit 2 + Gson + Corrutinas
- **Backend propio:** Microservicio REST en **Spring Boot 3.5** (Java 21 + Spring Data JPA + H2)
- **Pruebas:** JUnit 4 (unitarias locales con Fake DAO) y AndroidJUnit4 (instrumentadas con SQLite real)

---

## 2. Módulos Implementados

### Persistencia Local (Room SQLite)
- **Entidades:** `Usuario`, `DocumentoItem`, `Persona`
- **DAOs:** `UsuarioDao`, `DocumentoDao`, `PersonaDAO`
- **Funciones:** Registro e inicio de sesión por email o RUT, edición de perfil con foto local, carga y visualización nativa de rúbricas (PDF/Word) y panel de administración exclusivo para docentes.

### Consumo de API Externa
- **Servicio:** `FeriadosApiService` (`https://date.nager.at/api/v3/PublicHolidays/{anio}/CL`)
- **Pantalla:** `FeriadosScreen.kt`
- Muestra el calendario oficial de feriados nacionales de Chile con manejo de estados de carga, error y actualización manual.

### Microservicio Spring Boot (`backend/`)
- **Ubicación:** carpeta `backend/` en la raíz del repositorio.
- **Endpoints REST (`/api/comunicados`):**
  - `GET /api/comunicados` — Lista los comunicados institucionales.
  - `POST /api/comunicados` — Publica un nuevo comunicado (disponible para rol Docente).
  - `DELETE /api/comunicados/{id}` — Elimina un comunicado por ID.
- **Pantalla Android:** `ComunicadosScreen.kt` conectada mediante `BackendClient`.
  - En emulador apunta por defecto a `http://10.0.2.2:8080/`.
  - Incluye opción en pantalla para cambiar la URL base si se prueba desde un teléfono físico en la misma red WiFi.

---

## 3. Cómo ejecutar el proyecto

### A. Levantar el microservicio Spring Boot
Desde la carpeta `backend/`:
```powershell
cd backend
..\gradlew.bat bootRun
```
El servicio queda escuchando en `http://localhost:8080/api/comunicados`.

### B. Ejecutar la aplicación Android
1. Abrir la carpeta raíz en **Android Studio**.
2. Esperar la sincronización de Gradle (**Sync Project with Gradle Files**).
3. Seleccionar un emulador o teléfono físico con Android 13 (API 33) o superior y presionar **Run**.

---

## 4. Pruebas Automatizadas

### Pruebas unitarias locales (sin emulador)
Ubicación: `app/src/test/java/com/fabiancg/persistenciaroomsqlite/`
- `ValidadorUsuarioTest.kt`: validación de RUT, correo, contraseña y nombre.
- `UsuarioRepositoryTest.kt`: pruebas de registro, login, búsqueda, eliminación y filtro por rol usando `FakeUsuarioDao.kt`.

Ejecución:
```powershell
.\gradlew.bat :app:testDebugUnitTest
```

### Pruebas instrumentadas (SQLite real en emulador/dispositivo)
Ubicación: `app/src/androidTest/java/com/fabiancg/persistenciaroomsqlite/`
- `UsuarioDaoInstrumentedTest.kt`: 14 pruebas sobre `AppDatabase` usando `Room.inMemoryDatabaseBuilder`.

Ejecución:
```powershell
.\gradlew.bat :app:connectedDebugAndroidTest
```

---

## 5. Generación de APK Firmado (Release)

El proyecto cuenta con configuración `signingConfigs` en `app/build.gradle.kts` asociada al keystore `app/esfocar-release.jks`.

Para generar el APK firmado:
```powershell
.\gradlew.bat :app:assembleRelease
```
El archivo generado queda en:
`app/build/outputs/apk/release/app-release.apk`
