# Actividad - Retrofit y Room (REST Countries)

Aplicación Android (Jetpack Compose) que consume la API pública de **REST Countries** y persiste la información localmente con **Room**, soportando **modo offline**, **búsqueda/filtrado**, **pantalla de detalle**, **paginación (scroll infinito)** e **indicador online/offline**.

## API seleccionada

- **API**: REST Countries (`https://restcountries.com/`)
- **Métodos HTTP**: `GET`
- **Tipo de respuesta**: JSON (listas de países)

### Endpoints usados (mínimo 4)

1. **Listado base**: `GET /v3.1/all?fields=...`
2. **Búsqueda por nombre**: `GET /v3.1/name/{name}?fields=...`
3. **Filtro por región**: `GET /v3.1/region/{region}?fields=...`
4. **Detalle por código**: `GET /v3.1/alpha/{code}?fields=...`

Implementación: `app/src/main/java/com/example/actividadretrofitroom/data/remote/RestCountriesApi.kt`

## Requerimientos del documento y cómo se cumplen

### Consumo de API (Retrofit + modelos Kotlin)

- **Retrofit obligatorio**: configurado en `NetworkModule` y usado en `RestCountriesApi`.
  - `app/src/main/java/com/example/actividadretrofitroom/di/NetworkModule.kt`
  - `app/src/main/java/com/example/actividadretrofitroom/data/remote/RestCountriesApi.kt`
- **Modelos Kotlin (mapeo)**: DTOs con Moshi.
  - `app/src/main/java/com/example/actividadretrofitroom/data/remote/dto/CountryDto.kt`

### UI (Compose)

- **Lista con información básica**: `CountriesListScreen`.
  - `app/src/main/java/com/example/actividadretrofitroom/ui/screens/list/CountriesListScreen.kt`
- **Filtrar/buscar por al menos 2 campos usando endpoint**:
  - **Nombre**: usa `GET /v3.1/name/{name}` cuando hay internet.
  - **Región**: usa `GET /v3.1/region/{region}` cuando hay internet.
  - Si está offline, se hace fallback a consulta local (Room) con los datos existentes.
  - Lógica en `CountriesListViewModel` + `CountryRepositoryImpl`.
    - `app/src/main/java/com/example/actividadretrofitroom/ui/screens/list/CountriesListViewModel.kt`
    - `app/src/main/java/com/example/actividadretrofitroom/data/repository/CountryRepositoryImpl.kt`
- **Pantalla de detalle (otro endpoint)**: `CountryDetailScreen` + `GET /v3.1/alpha/{code}`.
  - `app/src/main/java/com/example/actividadretrofitroom/ui/screens/detail/CountryDetailScreen.kt`
  - `app/src/main/java/com/example/actividadretrofitroom/ui/screens/detail/CountryDetailViewModel.kt`

### Persistencia local (Room)

- **Base de datos local (SQLite con Room)**:
  - Entidad: `CountryEntity` con PK `cca3`.
  - DAO con `@Upsert` para evitar redundancia y mantener consistencia.
  - DB: `AppDatabase`.
  - `app/src/main/java/com/example/actividadretrofitroom/data/local/entity/CountryEntity.kt`
  - `app/src/main/java/com/example/actividadretrofitroom/data/local/CountryDao.kt`
  - `app/src/main/java/com/example/actividadretrofitroom/data/local/AppDatabase.kt`
- **Modo offline**:
  - La UI siempre consume la lista desde Room (PagingSource). Si no hay internet, se siguen mostrando datos persistidos.
  - Repositorio refresca desde red solo cuando hay conectividad.

### Flujo esperado online/offline

- **Si hay conexión**:
  - Consume API y hace `upsert` en Room (`cca3` como llave).
  - Muestra datos en lista (desde BD, con paginación).
- **Si no hay conexión**:
  - Carga y muestra los datos desde Room.

### Requerimientos obligatorios adicionales

- **Indicador online/offline**:
  - Observador con `ConnectivityManager` y `StateFlow`.
  - Se muestra un chip en la barra superior (lista y detalle).
  - `app/src/main/java/com/example/actividadretrofitroom/core/connectivity/ConnectivityObserverImpl.kt`
- **Paginación (scroll infinito)**:
  - Paging3 + `PagingSource` desde Room.
  - `CountryDao.pagingFiltered(...)` + `Pager(...)`.
  - `CountriesListScreen` usa `collectAsLazyPagingItems()`.
  - `app/src/main/java/com/example/actividadretrofitroom/data/local/CountryDao.kt`
  - `app/src/main/java/com/example/actividadretrofitroom/data/repository/CountryRepositoryImpl.kt`

### Arquitectura por capas + Hilt (recomendación del documento)

- **UI**: `ui/*`
- **ViewModel**: `CountriesListViewModel`, `CountryDetailViewModel`
- **Repository**: `CountryRepository` + `CountryRepositoryImpl`
- **Domain**: `domain/model/*` y `domain/repository/*`
- **Fuentes de datos**:
  - **API**: Retrofit (`RestCountriesApi`)
  - **BD**: Room (`AppDatabase`, `CountryDao`)
- **Hilt**:
  - `@HiltAndroidApp`: `ActividadRetrofitRoomApp`
  - `@AndroidEntryPoint`: `MainActivity`
  - Módulos: `NetworkModule`, `DatabaseModule`, `RepositoryModule`, `ConnectivityModule`

## Cómo compilar / ejecutar

### Requisitos

- Android Studio (Jellyfish o superior) o Gradle CLI
- JDK 11+

### Build

```bash
./gradlew clean :app:assembleDebug
```

## Modelado de BD (resumen)

- **Tabla**: `countries`
- **PK**: `cca3`
- **Estrategia anti-redundancia**: `@Upsert` (actualiza si existe, inserta si no).

## Evidencias para la entrega (PDF)

El documento pide un PDF con:

- Link del repo en GitHub.
- Párrafo explicando API y endpoints (sección “API seleccionada” de este README).
- Modelado SQLite/Room (sección “Modelado de BD” + tabla `countries`).
- Capturas de pantalla (lista, filtros, detalle, indicador online/offline, paginación).
- Explicación de uso de IA (puedes describir que se usó Cursor/Agente para scaffolding, integración Retrofit/Room/Hilt y debugging de Gradle).

