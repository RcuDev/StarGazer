# StarGazer - Guía Completa de Migración a Molecule

## 📋 Índice

1. [Preparación del Entorno](#fase-0-preparación-del-entorno)
2. [Reestructuración de Módulos](#fase-1-reestructuración-de-módulos)
3. [Domain Layer](#fase-2-domain-layer)
4. [Data Layer](#fase-3-data-layer)
5. [Presentation Layer - Posts List](#fase-4-presentation-layer---posts-list)
6. [Presentation Layer - Post Detail](#fase-5-presentation-layer---post-detail)
7. [Presentation Layer - Settings](#fase-6-presentation-layer---settings)
8. [Navegación y Scaffold](#fase-7-navegación-y-scaffold)
9. [Testing](#fase-8-testing)
10. [Features Avanzadas](#fase-9-features-avanzadas)
11. [Optimización Final](#fase-10-optimización-final)

---

## FASE 0: Preparación del Entorno

### ✅ Checkpoint: Proyecto compila y corre normalmente

### 0.1 Crear rama de migración
**Archivo:** Git
- Crear rama `feature/molecule-migration` desde `main`
- Hacer commit inicial antes de cambios

### 0.2 Actualizar dependencias
**Archivo:** `gradle/libs.versions.toml`
- Añadir versión de Molecule: `molecule = "2.0.0"`
- Añadir versión de Turbine: `turbine = "1.0.0"`
- Añadir Navigation Compose actualizado

**Archivo:** `build.gradle.kts` (módulo común)
- Añadir `implementation(libs.molecule.runtime)`
- Añadir `implementation(libs.androidx.lifecycle.viewmodel.compose)`
- Añadir `testImplementation(libs.turbine)`
- Sync proyecto y verificar que compila

### 0.3 Crear estructura de carpetas
**Crear directorios:**
```
feature/posts/src/commonMain/kotlin/
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
├── data/
│   ├── remote/
│   ├── local/
│   ├── repository/
│   └── mapper/
└── presentation/
    ├── list/
    ├── detail/
    └── settings/
```

---

## FASE 1: Reestructuración de Módulos

### ✅ Checkpoint: Nueva estructura creada, proyecto compila

### 1.1 Mover modelos existentes
**Acción:** Mover archivos de modelos
- `Post.kt` → `feature/posts/domain/model/Post.kt`
- `PostType.kt` → `feature/posts/domain/model/PostType.kt`
- Actualizar imports en archivos que los usan
- Compilar y verificar

### 1.2 Identificar código existente
**Archivo:** Crear `MIGRATION_MAP.md`
- Listar todos los ViewModels actuales
- Listar todos los repositorios actuales
- Listar todas las screens actuales
- Documentar dependencias entre ellos

---

## FASE 2: Domain Layer

### ✅ Checkpoint: Domain layer completo y testeado

### 2.1 Crear interfaces de repositorios
**Archivo:** `feature/posts/domain/repository/PostRepository.kt`
- Definir interface `PostRepository`
- Métodos: `getPosts()`, `getPostById()`, `searchPosts()`
- Usar `Result<T>` para manejo de errores

**Archivo:** `feature/posts/domain/repository/PreferencesRepository.kt`
- Definir interface `PreferencesRepository`
- Propiedades Flow: `isDarkMode`, `selectedPostType`, `newsWebsites`
- Métodos suspend: `setDarkMode()`, `setPostType()`, `toggleWebsite()`

### 2.2 Crear Use Cases - Posts
**Archivo:** `feature/posts/domain/usecase/GetPostsUseCase.kt`
- Constructor con `PostRepository` y `PreferencesRepository`
- Lógica: obtener posts y filtrar por websites habilitados
- Operator function `invoke(type: PostType)`

**Archivo:** `feature/posts/domain/usecase/SearchPostsUseCase.kt`
- Similar a GetPostsUseCase pero con parámetro query
- Filtrado por título y descripción

**Archivo:** `feature/posts/domain/usecase/GetPostByIdUseCase.kt`
- Obtener un post específico por ID

### 2.3 Crear Use Cases - Preferences
**Archivo:** `feature/posts/domain/usecase/GetPreferencesUseCase.kt`
- Simplemente retornar el repositorio (acceso a Flows)

**Archivo:** `feature/posts/domain/usecase/ToggleDarkModeUseCase.kt`
- Encapsular llamada a repository.setDarkMode()

**Archivo:** `feature/posts/domain/usecase/SetPostTypeUseCase.kt`
- Encapsular llamada a repository.setPostType()

**Archivo:** `feature/posts/domain/usecase/ToggleNewsWebsiteUseCase.kt`
- Encapsular llamada a repository.toggleWebsite()

### 2.4 Testing del Domain Layer
**Archivo:** `feature/posts/domain/usecase/GetPostsUseCaseTest.kt`
- Mock de repositorios con Mokkery
- Test de filtrado por websites
- Test de manejo de errores

---

## FASE 3: Data Layer

### ✅ Checkpoint: Data layer implementado, APIs funcionan

### 3.1 Crear DTOs
**Archivo:** `feature/posts/data/remote/dto/PostDto.kt`
- Data class con @Serializable
- Campos de la API con @SerialName

**Archivo:** `feature/posts/data/remote/dto/PostsResponse.kt`
- Wrapper para la respuesta de la API

### 3.2 Crear Mappers
**Archivo:** `feature/posts/data/mapper/PostMapper.kt`
- Extension function `PostDto.toDomain(type: PostType): Post`
- Transformar campos de DTO a modelo de dominio

### 3.3 Implementar API Service
**Archivo:** `feature/posts/data/remote/SpaceflightApi.kt`
- Interface con métodos suspend
- `getArticles()`, `getBlogs()`, `getReports()`

**Archivo:** `feature/posts/data/remote/SpaceflightApiImpl.kt`
- Implementación con HttpClient (Ktor)
- Función helper `safeApiCall` para Result wrapper
- Endpoints de spaceflightnewsapi.net

### 3.4 Implementar PostRepository
**Archivo:** `feature/posts/data/repository/PostRepositoryImpl.kt`
- Implementar interface del domain
- Inyectar SpaceflightApi
- Usar mappers para convertir DTOs a modelos
- Implementar `getPosts()`, `getPostById()`, `searchPosts()`

### 3.5 Implementar PreferencesRepository
**Archivo:** `feature/posts/data/repository/PreferencesRepositoryImpl.kt`
- Implementar interface del domain
- Inyectar DataStore<Preferences>
- Definir Keys para preferencias
- Implementar Flows observables
- Implementar métodos de escritura

### 3.6 Testing del Data Layer
**Archivo:** `feature/posts/data/repository/PostRepositoryImplTest.kt`
- Mock de SpaceflightApi
- Test de mapeo correcto
- Test de manejo de errores de red

---

## FASE 4: Presentation Layer - Posts List

### ✅ Checkpoint: Lista de posts funciona con Molecule

### 4.1 Crear State y Events
**Archivo:** `feature/posts/presentation/list/PostsListState.kt`
- Data class `PostsListUiState` con todos los campos necesarios
- Sealed interface `PostsListEvent` con todos los eventos

### 4.2 Crear Presenter con Molecule
**Archivo:** `feature/posts/presentation/list/PostsListPresenter.kt`
- Función @Composable `PostsListPresenter()`
- Parámetros: events Flow, use cases necesarios
- Variables de estado con `remember { mutableStateOf() }`
- LaunchedEffect para carga inicial
- LaunchedEffect para procesar eventos
- Estado derivado con `remember(dependencies)`
- Retornar PostsListUiState

### 4.3 Crear ViewModel
**Archivo:** `feature/posts/presentation/list/PostsListViewModel.kt`
- Inyectar use cases con constructor
- MutableSharedFlow para eventos con extraBufferCapacity
- StateFlow con `moleculeFlow(RecompositionMode.Immediate)`
- `.stateIn()` con WhileSubscribed(5000)
- Función `onEvent()` que emite eventos

### 4.4 Crear Screen UI
**Archivo:** `feature/posts/presentation/list/PostsListScreen.kt`
- Composable que recibe state y onEvent
- Tabs para tipos de posts (Articles, Blogs, Reports)
- SearchBar con manejo de query
- LazyColumn con items
- Loading, error y empty states
- PostCard component

**Archivo:** `feature/posts/presentation/list/components/PostCard.kt`
- Card para mostrar un post
- AsyncImage con Coil
- Textos con Material3 typography

### 4.5 Actualizar DI (Koin)
**Archivo:** `feature/posts/di/PostsModule.kt`
- Factory para cada Use Case
- Single para repositorios
- ViewModel para PostsListViewModel

### 4.6 Testing
**Archivo:** `feature/posts/presentation/list/PostsListPresenterTest.kt`
- Test de estado inicial
- Test de carga de posts
- Test de filtrado por categoría
- Test de búsqueda
- Usar moleculeFlow + Turbine

**Archivo:** `feature/posts/presentation/list/PostsListScreenTest.kt`
- Compose UI tests
- Test de clicks
- Test de búsqueda
- Test de estados (loading, error, success)

### 4.7 Integrar en app principal
**Archivo:** `composeApp/src/commonMain/kotlin/App.kt`
- Importar PostsListScreen
- Reemplazar pantalla antigua por nueva
- Ejecutar app y validar funcionamiento

---

## FASE 5: Presentation Layer - Post Detail

### ✅ Checkpoint: Detalle de post funciona con navegación

### 5.1 Crear State y Events
**Archivo:** `feature/posts/presentation/detail/PostDetailState.kt`
- Data class `PostDetailUiState`
- Sealed interface `PostDetailEvent`

### 5.2 Crear Presenter
**Archivo:** `feature/posts/presentation/detail/PostDetailPresenter.kt`
- @Composable con parámetro `postId: Int`
- Cargar post por ID en LaunchedEffect
- Manejar eventos (refresh, back)

### 5.3 Crear ViewModel
**Archivo:** `feature/posts/presentation/detail/PostDetailViewModel.kt`
- SavedStateHandle para obtener postId
- moleculeFlow con DetailPresenter
- Event handling

### 5.4 Crear Screen UI
**Archivo:** `feature/posts/presentation/detail/PostDetailScreen.kt`
- WebView para mostrar contenido del post
- Scaffold con TopBar
- Botones de navegación back, favorite, share
- Loading y error states

### 5.5 Actualizar DI
**Archivo:** `feature/posts/di/PostsModule.kt`
- Añadir PostDetailViewModel con parámetro postId

### 5.6 Testing
**Archivo:** `feature/posts/presentation/detail/PostDetailPresenterTest.kt`
- Test de carga de post
- Test de estados

---

## FASE 6: Presentation Layer - Settings

### ✅ Checkpoint: Settings funciona con preferencias persistentes

### 6.1 Crear State y Events
**Archivo:** `feature/posts/presentation/settings/SettingsState.kt`
- Data class `SettingsUiState`
- Sealed interface `SettingsEvent`

### 6.2 Crear Presenter
**Archivo:** `feature/posts/presentation/settings/SettingsPresenter.kt`
- Observar isDarkMode y newsWebsites con collectAsState
- Manejar toggles de preferencias

### 6.3 Crear ViewModel
**Archivo:** `feature/posts/presentation/settings/SettingsViewModel.kt`
- moleculeFlow con SettingsPresenter
- Inyectar use cases de preferencias

### 6.4 Crear Screen UI
**Archivo:** `feature/posts/presentation/settings/SettingsScreen.kt`
- Switch para Dark Mode
- Lista de checkboxes para News Websites
- Estilos con Material3

### 6.5 Actualizar DI
**Archivo:** `feature/posts/di/PostsModule.kt`
- Añadir SettingsViewModel

### 6.6 Testing
**Archivo:** `feature/posts/presentation/settings/SettingsPresenterTest.kt`
- Test de toggle dark mode
- Test de toggle websites

---

## FASE 7: Navegación y Scaffold

### ✅ Checkpoint: Navegación completa funciona

### 7.1 Definir rutas de navegación
**Archivo:** `composeApp/src/commonMain/kotlin/navigation/Screen.kt`
- Sealed class con @Serializable para type-safe navigation
- Screen.PostsList, Screen.PostDetail(postId), Screen.Settings

### 7.2 Crear NavHost
**Archivo:** `composeApp/src/commonMain/kotlin/navigation/AppNavHost.kt`
- NavHost con NavController
- Composable para cada ruta
- Inyección de ViewModels con koinViewModel
- Collect de states con collectAsStateWithLifecycle
- Navegación entre pantallas

### 7.3 Crear ViewModel principal
**Archivo:** `composeApp/src/commonMain/kotlin/MainViewModel.kt`
- StateFlow para selectedCategory
- Compartir estado entre TopBar y PostsList

### 7.4 Crear TopBar compartida
**Archivo:** `composeApp/src/commonMain/kotlin/ui/AppTopBar.kt`
- TopAppBar con título
- ScrollableTabRow para categorías (solo en lista)
- Botón back (solo en detalle)
- Botón settings

### 7.5 Crear Scaffold principal
**Archivo:** `composeApp/src/commonMain/kotlin/ui/MainScaffold.kt`
- Scaffold con AppTopBar
- NavHost en content con padding
- Observar ruta actual para mostrar/ocultar categorías
- Sincronizar categoría seleccionada con PostsListViewModel

### 7.6 Actualizar App.kt
**Archivo:** `composeApp/src/commonMain/kotlin/App.kt`
- Llamar a MainScaffold()
- Aplicar tema con isDarkMode

### 7.7 Testing de navegación
**Archivo:** `composeApp/src/androidTest/kotlin/AppNavigationTest.kt`
- Test de navegación lista → detalle → back
- Test de navegación a settings → back
- Test de deep links

---

## FASE 8: Testing

### ✅ Checkpoint: Cobertura de tests >70%

### 8.1 Tests unitarios de Use Cases
**Archivos:** `*UseCaseTest.kt` en cada usecase
- Mock de repositorios
- Test de lógica de filtrado
- Test de manejo de errores

### 8.2 Tests de Presenters
**Archivos:** `*PresenterTest.kt` en cada presenter
- Usar moleculeFlow + Turbine
- Test de estados iniciales
- Test de flujos de eventos
- Test de estados derivados

### 8.3 Tests de UI
**Archivos:** `*ScreenTest.kt` en cada screen
- Compose UI Testing
- Test de renders condicionales
- Test de interacciones de usuario
- Test de navegación

### 8.4 Tests de integración
**Archivo:** `composeApp/src/androidTest/kotlin/StarGazerAppTest.kt`
- Setup de Koin para testing
- Flujo completo end-to-end
- Test de persistencia

### 8.5 Crear KoinTestFrame
**Archivo:** `composeApp/src/commonTest/kotlin/KoinTestFrame.kt`
- Composable helper para tests con Koin
- Configurar módulos de DI para testing

---

## FASE 9: Features Avanzadas

### ✅ Checkpoint: Features opcionales implementadas

### 9.1 Paginación Infinita (Opcional)
**Archivo:** `feature/posts/domain/model/PaginatedPosts.kt`
- Añadir modelo de paginación

**Archivo:** `feature/posts/domain/usecase/GetPaginatedPostsUseCase.kt`
- Use case con parámetro page

**Archivo:** `feature/posts/presentation/list/PostsListPresenter.kt`
- Añadir lógica de append para loadMore
- Detectar scroll al final

**Archivo:** `feature/posts/presentation/list/PostsListScreen.kt`
- derivedStateOf para detectar scroll
- Loading indicator al final de la lista

### 9.2 Cache y Offline-First (Opcional)
**Archivo:** `feature/posts/domain/repository/CacheRepository.kt`
- Interface para cache de posts

**Archivo:** `feature/posts/data/local/PostsDao.kt`
- Room DAO para persistencia local

**Archivo:** `feature/posts/data/repository/CacheRepositoryImpl.kt`
- Implementar cache con Room y timestamps

**Archivo:** `feature/posts/domain/usecase/GetPostsWithCacheUseCase.kt`
- Lógica de cache-first
- Validación de frescura

**Archivo:** `core/network/ConnectivityObserver.kt`
- Observer de conectividad Android/iOS

**Archivo:** `feature/posts/presentation/list/PostsListPresenter.kt`
- Integrar cache y conectividad
- Mostrar estado offline

### 9.3 Búsqueda Avanzada (Opcional)
**Archivo:** `feature/posts/presentation/search/SearchPresenter.kt`
- Debounce con produceState
- Filtros adicionales

**Archivo:** `feature/posts/presentation/search/SearchScreen.kt`
- SearchBar de Material3
- Panel de filtros expandible
- Historial de búsquedas

### 9.4 Favoritos con Sincronización (Opcional)
**Archivo:** `feature/posts/domain/repository/FavoritesRepository.kt`
- Interface con Flow de favoritos

**Archivo:** `feature/posts/data/repository/FavoritesRepositoryImpl.kt`
- Persistencia local con DataStore o Room
- Sincronización con backend (si existe)

**Archivo:** `feature/posts/presentation/favorites/FavoritesPresenter.kt`
- Auto-sync periódico
- Manejo de sync errors

### 9.5 Analytics (Opcional)
**Archivo:** `core/analytics/AnalyticsTracker.kt`
- Interface de analytics

**Archivo:** `core/analytics/AnalyticsTrackerImpl.kt`
- Implementación con Firebase/Amplitude

**Archivo:** `feature/posts/presentation/list/PostsListPresenter.kt`
- Integrar tracking de eventos
- TrackScreenView helper

---

## FASE 10: Optimización Final

### ✅ Checkpoint: App optimizada y lista para producción

### 10.1 Performance Profiling
**Archivo:** `core/performance/MeasuredPresenter.kt`
- Wrapper para medir recomposiciones
- Logs en modo debug

**Acción:** Envolver Presenters con MeasuredPresenter
- Identificar recomposiciones excesivas
- Optimizar con remember() y derivedStateOf

### 10.2 Code Cleanup
**Acción:** Eliminar código legacy
- Borrar ViewModels antiguos no usados
- Borrar screens antiguas no usadas
- Limpiar imports no usados
- Formatear código con ktlint

### 10.3 Documentación
**Archivo:** `docs/ARCHITECTURE.md`
- Documentar arquitectura final
- Diagramas de flujo
- Decisiones de diseño

**Archivo:** `docs/MOLECULE_GUIDE.md`
- Guía interna para el equipo
- Patrones y convenciones
- Ejemplos de código

**Archivo:** `README.md`
- Actualizar con nueva arquitectura
- Instrucciones de setup
- Comandos de testing

### 10.4 CI/CD Updates
**Archivo:** `.github/workflows/ci.yml`
- Añadir jobs para todos los tests
- Cobertura de código
- Linting

### 10.5 Migration Validation
**Checklist final:**
- ✅ Todas las pantallas funcionan
- ✅ Navegación fluida
- ✅ Preferencias persisten
- ✅ Modo oscuro funciona
- ✅ Búsqueda y filtros funcionan
- ✅ No hay memory leaks
- ✅ Tests pasan (>70% coverage)
- ✅ App funciona offline
- ✅ Performance aceptable
- ✅ Logs limpios (sin errores)

### 10.6 Release Preparation
**Archivo:** `CHANGELOG.md`
- Documentar cambios de arquitectura
- Breaking changes (si los hay)
- Nuevas features

**Archivo:** `build.gradle.kts`
- Incrementar versionCode
- Actualizar versionName

**Acción:** Merge a main
- Pull request con descripción completa
- Code review del equipo
- Merge tras aprobación

---

## 📊 Métricas de Validación

### Por cada fase, verificar:

1. **Compilación**: El proyecto compila sin errores
2. **Tests**: Todos los tests pasan
3. **Funcionalidad**: La feature funciona en el emulador/dispositivo
4. **Performance**: No hay lag perceptible
5. **Memoria**: No hay leaks evidentes

### Herramientas recomendadas:
- Android Studio Profiler (memoria, CPU)
- LeakCanary (memory leaks)
- Compose Layout Inspector (jerarquía UI)
- Logcat (errores runtime)

---

## 🚨 Problemas Comunes y Soluciones

### Problema: Molecule no recompone
**Solución:** 
- Verificar que usas `remember { mutableStateOf() }`
- Verificar RecompositionMode.Immediate
- Check que LaunchedEffect tiene keys correctos

### Problema: Navigation no funciona
**Solución:**
- Verificar @Serializable en Screen classes
- Check que SavedStateHandle obtiene parámetros
- Verificar rutas en NavHost

### Problema: StateFlow no emite
**Solución:**
- Usar `stateIn()` con SharingStarted.WhileSubscribed
- Verificar que el scope es viewModelScope
- Check que events.tryEmit() se llama

### Problema: Tests fallan intermitentemente
**Solución:**
- Usar `runTest` de kotlinx-coroutines-test
- Añadir `advanceUntilIdle()` si es necesario
- Mock correctamente con Mokkery

### Problema: Koin no inyecta
**Solución:**
- Verificar que módulos están en startKoin()
- Check que ViewModel usa koinViewModel()
- Verificar scopes en factory/single

---

## 📅 Timeline Estimado

| Fase | Duración | Dependencias |
|------|----------|-------------|
| Fase 0 | 1 día | Ninguna |
| Fase 1 | 1 día | Fase 0 |
| Fase 2 | 2 días | Fase 1 |
| Fase 3 | 3 días | Fase 2 |
| Fase 4 | 3 días | Fase 3 |
| Fase 5 | 2 días | Fase 4 |
| Fase 6 | 2 días | Fase 4 |
| Fase 7 | 2 días | Fases 4,5,6 |
| Fase 8 | 3 días | Fases 2-7 |
| Fase 9 | 5 días (opcional) | Fase 7 |
| Fase 10 | 2 días | Todas |

**Total: 21-26 días** (dependiendo de features opcionales)

---

## ✅ Checklist Final Pre-Release

- [ ] Todas las funcionalidades del código anterior funcionan
- [ ] Tests unitarios pasan (>70% coverage)
- [ ] Tests de integración pasan
- [ ] UI tests pasan
- [ ] No hay TODOs críticos en el código
- [ ] Documentación actualizada
- [ ] CHANGELOG.md completo
- [ ] Code review aprobado
- [ ] QA testing completado
- [ ] Performance profiling aceptable
- [ ] Memory leaks verificados
- [ ] Logs de producción limpios
- [ ] Crash reporting configurado
- [ ] Analytics funcionando

---

## 🎓 Recursos Adicionales

### Documentación
- [Molecule Official Docs](https://github.com/cashapp/molecule)
- [Compose Navigation](https://developer.android.com/jetpack/compose/navigation)
- [Koin Documentation](https://insert-koin.io/)
- [Turbine Testing](https://github.com/cashapp/turbine)

### Ejemplos de Referencia
- [Now in Android](https://github.com/android/nowinandroid)
- [Molecule Samples](https://github.com/cashapp/molecule/tree/main/sample)

---

## 📝 Notas Finales

- **Hacer commits frecuentes**: Un commit por cada sub-fase completada
- **Validar continuamente**: No avanzar si algo no funciona
- **Pedir ayuda temprano**: Si algo no está claro, preguntar antes de continuar
- **Documentar decisiones**: Añadir comentarios sobre por qué se hizo algo de cierta manera
- **Mantener comunicación**: Actualizar al equipo del progreso regularmente

**¡Buena suerte con la migración! 🚀**