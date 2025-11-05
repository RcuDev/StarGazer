# Migración a Molecule - Unidirectional Data Flow (UDF)

## 📋 Índice
1. [Análisis de la Arquitectura Actual](#análisis-de-la-arquitectura-actual)
2. [Arquitectura Objetivo con Molecule](#arquitectura-objetivo-con-molecule)
3. [Pasos de Migración](#pasos-de-migración)
4. [Estructura de Archivos](#estructura-de-archivos)
5. [Ejemplos de Código](#ejemplos-de-código)

---

## 🔍 Análisis de la Arquitectura Actual

### Estado Actual
Tu aplicación StarGazer utiliza actualmente:
- **ViewModel** de AndroidX con `StateFlow` y `MutableStateFlow`
- **Koin** para inyección de dependencias
- **DataStore** para persistencia de preferencias
- **Compose** para UI multiplataforma

### Problemas Identificados
1. **PostViewModel** contiene lógica de estado mutable distribuida:
   - Múltiples `MutableStateFlow` separados
   - Lógica de actualización dispersa en diferentes funciones
   - Estado interno no encapsulado (expone `newsSites` y `newsSitesSelected` como públicos)

2. **Componentes UI mezclando lógica**:
   - `TopBar` accede directamente a `DataStore` y tiene lógica de negocio
   - `SettingsDropDown` maneja su propio estado y side effects

3. **Falta de un contrato claro de eventos/acciones**
   - Las acciones del usuario están dispersas sin un patrón unificado

---

## 🎯 Arquitectura Objetivo con Molecule

### Principios de Molecule + UDF

Molecule permite escribir **"UI as State"** usando `@Composable` functions para crear streams reactivos, aplicando UDF puro:

```
┌─────────────────────────────────────────────────────────┐
│                         VIEW                            │
│  (Compose UI - Solo renderiza y emite eventos)         │
└────────────────────┬───────────────▲────────────────────┘
                     │               │
                  Events          State
                     │               │
┌────────────────────▼───────────────┴────────────────────┐
│                      PRESENTER                          │
│    (@Composable con Molecule - Lógica de estado)       │
│                                                          │
│  ┌──────────────────────────────────────────────────┐  │
│  │  Events → Actions → State Changes → New State   │  │
│  └──────────────────────────────────────────────────┘  │
└────────────────────┬───────────────▲────────────────────┘
                     │               │
                 Use Cases       Results
                     │               │
┌────────────────────▼───────────────┴────────────────────┐
│                  DOMAIN LAYER                           │
│            (No cambia - Use Cases)                      │
└─────────────────────────────────────────────────────────┘
```

### Ventajas de Molecule
- ✅ **UDF puro**: Un único flujo de datos unidireccional
- ✅ **Inmutabilidad**: Estado como data classes inmutables
- ✅ **Composable Functions**: Reutilización de la potencia de Compose fuera de la UI
- ✅ **Testing**: Fácil testeo del estado sin necesidad de UI
- ✅ **Multiplataforma**: Compatible con KMP
- ✅ **Cancelación automática**: Manejo de scope integrado

---

## 📝 Pasos de Migración

### Fase 1: Preparación y Dependencias

#### Paso 1.1: Agregar Molecule al proyecto
Editar `gradle/libs.versions.toml`:
```toml
[versions]
molecule = "2.2.0"

[libraries]
molecule-runtime = { group = "app.cash.molecule", name = "molecule-runtime", version.ref = "molecule" }
```

Editar `posts/build.gradle.kts`:
```kotlin
commonMain.dependencies {
    // Molecule
    implementation(libs.molecule.runtime)
    
    // Mantener las existentes...
}
```

#### Paso 1.2: Sincronizar el proyecto
```bash
./gradlew sync
```

---

### Fase 2: Crear Contratos de UDF

#### Paso 2.1: Crear contratos para PostScreen

Crear archivo `posts/src/commonMain/kotlin/com/rcudev/posts/ui/posts/PostContract.kt`:

```kotlin
package com.rcudev.posts.ui.posts

import com.rcudev.posts.domain.model.Post

// Estado inmutable de la pantalla
sealed interface PostState {
    data object Loading : PostState
    data object Error : PostState
    data class Empty(val message: String) : PostState
    data class Content(
        val posts: List<Post>,
        val postTypeSelected: String,
        val newsSites: List<String>,
        val newsSitesSelected: String,
        val loadingNextPage: Boolean = false,
        val showError: Boolean = false
    ) : PostState
}

// Eventos del usuario
sealed interface PostEvent {
    data object LoadNextPage : PostEvent
    data object Retry : PostEvent
    data class FilterByNewsSite(val newsSite: String) : PostEvent
    data class ChangePostType(val postType: String) : PostEvent
}

// Side effects (navegación, snackbars, etc)
sealed interface PostEffect {
    data class ShowSnackbar(val message: String) : PostEffect
    data class NavigateToDetail(val url: String) : PostEffect
}
```

#### Paso 2.2: Crear contratos para Settings

Crear archivo `posts/src/commonMain/kotlin/com/rcudev/posts/ui/settings/SettingsContract.kt`:

```kotlin
package com.rcudev.posts.ui.settings

// Estado inmutable de settings
data class SettingsState(
    val isDarkMode: Boolean,
    val newsSites: List<String>,
    val selectedNewsSites: List<String>
)

// Eventos de settings
sealed interface SettingsEvent {
    data object ToggleDarkMode : SettingsEvent
    data class ToggleNewsSite(val newsSite: String) : SettingsEvent
    data object ConfirmSelection : SettingsEvent
    data object Dismiss : SettingsEvent
}
```

---

### Fase 3: Implementar Presenters con Molecule

#### Paso 3.1: Crear PostPresenter

Crear archivo `posts/src/commonMain/kotlin/com/rcudev/posts/ui/posts/PostPresenter.kt`:

```kotlin
package com.rcudev.posts.ui.posts

import androidx.compose.runtime.*
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.rcudev.posts.domain.InfoService
import com.rcudev.posts.domain.PostService
import com.rcudev.posts.domain.model.PostType
import com.rcudev.storage.NEWS_SITES_FILTER
import com.rcudev.storage.POST_TYPE_FILTER
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

class PostPresenter(
    private val preferences: DataStore<Preferences>,
    private val postService: PostService,
    private val infoService: InfoService,
    private val scope: CoroutineScope
) {
    
    private val events = MutableSharedFlow<PostEvent>(extraBufferCapacity = 64)
    val effects = MutableSharedFlow<PostEffect>(extraBufferCapacity = 64)
    
    val state: StateFlow<PostState> = scope.launchMolecule(
        mode = RecompositionMode.Immediate
    ) {
        PostStateProducer(events = events.asSharedFlow())
    }
    
    fun sendEvent(event: PostEvent) {
        events.tryEmit(event)
    }
    
    @Composable
    private fun PostStateProducer(
        events: SharedFlow<PostEvent>
    ): PostState {
        // Estado derivado de DataStore
        val postTypeFilter by preferences.data
            .map { it[POST_TYPE_FILTER] ?: PostType.ARTICLES.type }
            .collectAsState(PostType.ARTICLES.type)
        
        val newsSitesFilter by preferences.data
            .map { it[NEWS_SITES_FILTER] ?: "" }
            .collectAsState("")
        
        // Estado interno del presenter
        var posts by remember { mutableStateOf<List<Post>?>(null) }
        var newsSites by remember { mutableStateOf<List<String>>(emptyList()) }
        var currentPage by remember { mutableIntStateOf(0) }
        var isLoadingNextPage by remember { mutableStateOf(false) }
        var showError by remember { mutableStateOf(false) }
        
        // Cargar info inicial
        LaunchedEffect(Unit) {
            infoService.getInfo()
                .onSuccess { info -> newsSites = info.newsSites }
                .onFailure { /* Log error */ }
        }
        
        // Recargar posts cuando cambian los filtros
        LaunchedEffect(postTypeFilter, newsSitesFilter) {
            posts = null
            currentPage = 0
            loadPosts(
                page = 0,
                postType = PostType.entries.find { it.type == postTypeFilter } 
                    ?: PostType.ARTICLES,
                newsSitesFilter = newsSitesFilter
            )?.let { posts = it }
        }
        
        // Manejar eventos
        LaunchedEffect(Unit) {
            events.collect { event ->
                when (event) {
                    is PostEvent.LoadNextPage -> {
                        if (!isLoadingNextPage) {
                            isLoadingNextPage = true
                            currentPage++
                            val postType = PostType.entries.find { it.type == postTypeFilter }
                                ?: PostType.ARTICLES
                            
                            loadPosts(currentPage, postType, newsSitesFilter)?.let {
                                posts = (posts ?: emptyList()) + it
                            }
                            isLoadingNextPage = false
                            effects.emit(PostEffect.ShowSnackbar("Loading more posts"))
                        }
                    }
                    is PostEvent.Retry -> {
                        showError = false
                        posts = null
                        currentPage = 0
                        val postType = PostType.entries.find { it.type == postTypeFilter }
                            ?: PostType.ARTICLES
                        loadPosts(0, postType, newsSitesFilter)?.let { posts = it }
                    }
                    else -> { /* Otros eventos */ }
                }
            }
        }
        
        // Retornar estado actual
        return when {
            showError -> PostState.Error
            posts == null -> PostState.Loading
            posts?.isEmpty() == true -> PostState.Empty("No posts found")
            else -> PostState.Content(
                posts = posts!!,
                postTypeSelected = postTypeFilter,
                newsSites = newsSites,
                newsSitesSelected = newsSitesFilter,
                loadingNextPage = isLoadingNextPage
            )
        }
    }
    
    private suspend fun loadPosts(
        page: Int,
        postType: PostType,
        newsSitesFilter: String
    ): List<Post>? {
        val offset = page * 10
        return postService.getArticles(
            postType = postType,
            offset = offset,
            newsSites = newsSitesFilter
        ).getOrNull()?.results
    }
}
```

#### Paso 3.2: Crear SettingsPresenter

Crear archivo `posts/src/commonMain/kotlin/com/rcudev/posts/ui/settings/SettingsPresenter.kt`:

```kotlin
package com.rcudev.posts.ui.settings

import androidx.compose.runtime.*
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.rcudev.storage.DARK_MODE
import com.rcudev.storage.NEWS_SITES_FILTER
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsPresenter(
    private val preferences: DataStore<Preferences>,
    private val scope: CoroutineScope
) {
    
    private val events = MutableSharedFlow<SettingsEvent>(extraBufferCapacity = 64)
    
    val state: StateFlow<SettingsState> = scope.launchMolecule(
        mode = RecompositionMode.Immediate
    ) {
        SettingsStateProducer(events = events.asSharedFlow())
    }
    
    fun sendEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.ToggleDarkMode -> {
                scope.launch {
                    preferences.edit { prefs ->
                        val current = prefs[DARK_MODE] ?: false
                        prefs[DARK_MODE] = !current
                    }
                }
            }
            is SettingsEvent.ConfirmSelection -> {
                scope.launch {
                    val currentState = state.value
                    preferences.edit { prefs ->
                        prefs[NEWS_SITES_FILTER] = currentState.selectedNewsSites.joinToString(",")
                    }
                }
            }
            else -> events.tryEmit(event)
        }
    }
    
    @Composable
    private fun SettingsStateProducer(
        events: SharedFlow<SettingsEvent>
    ): SettingsState {
        val isDarkMode by preferences.data
            .map { it[DARK_MODE] ?: false }
            .collectAsState(false)
        
        val newsSitesFilter by preferences.data
            .map { it[NEWS_SITES_FILTER] ?: "" }
            .collectAsState("")
        
        var selectedNewsSites by remember {
            mutableStateOf(newsSitesFilter.split(",").filter { it.isNotEmpty() })
        }
        
        // Manejar eventos
        LaunchedEffect(Unit) {
            events.collect { event ->
                when (event) {
                    is SettingsEvent.ToggleNewsSite -> {
                        selectedNewsSites = if (event.newsSite in selectedNewsSites) {
                            selectedNewsSites - event.newsSite
                        } else {
                            selectedNewsSites + event.newsSite
                        }
                    }
                    else -> { /* Otros eventos */ }
                }
            }
        }
        
        return SettingsState(
            isDarkMode = isDarkMode,
            newsSites = emptyList(), // Se cargarán desde PostPresenter
            selectedNewsSites = selectedNewsSites
        )
    }
}
```

---

### Fase 4: Actualizar Componentes UI

#### Paso 4.1: Refactorizar PostRoute y PostScreen

Actualizar `posts/src/commonMain/kotlin/com/rcudev/posts/ui/posts/PostRoute.kt`:

```kotlin
package com.rcudev.posts.ui.posts

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject

@Composable
fun PostRoute(
    presenter: PostPresenter = koinInject(),
    showSettings: () -> Boolean,
    hideSettings: () -> Unit,
    onPostClick: (String) -> Unit,
    finishSplash: () -> Unit = {}
) {
    val state by presenter.state.collectAsState()
    
    // Manejar effects
    LaunchedEffect(Unit) {
        presenter.effects.collectLatest { effect ->
            when (effect) {
                is PostEffect.ShowSnackbar -> {
                    // Mostrar snackbar
                }
                is PostEffect.NavigateToDetail -> {
                    onPostClick(effect.url)
                }
            }
        }
    }
    
    // Finalizar splash cuando cargue
    LaunchedEffect(state) {
        if (state !is PostState.Loading) {
            finishSplash()
        }
    }
    
    PostScreen(
        state = state,
        showSettings = showSettings,
        hideSettings = hideSettings,
        onEvent = presenter::sendEvent,
        onPostClick = onPostClick
    )
}
```

Actualizar `posts/src/commonMain/kotlin/com/rcudev/posts/ui/posts/PostScreen.kt`:

```kotlin
package com.rcudev.posts.ui.posts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.rcudev.posts.ui.settings.SettingsDropDown

@Composable
internal fun PostScreen(
    state: PostState,
    showSettings: () -> Boolean,
    hideSettings: () -> Unit,
    onEvent: (PostEvent) -> Unit,
    onPostClick: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (state) {
            is PostState.Loading -> LoadingContent()
            is PostState.Error -> ErrorContent(
                onRetry = { onEvent(PostEvent.Retry) }
            )
            is PostState.Empty -> EmptyContent(state.message)
            is PostState.Content -> PostContent(
                posts = state.posts,
                loadingNextPage = state.loadingNextPage,
                onLoadNextPage = { onEvent(PostEvent.LoadNextPage) },
                onItemClick = onPostClick
            )
        }
        
        if (showSettings()) {
            SettingsDropDown(
                newsSites = (state as? PostState.Content)?.newsSites ?: emptyList(),
                newsSitesSelected = (state as? PostState.Content)?.newsSitesSelected ?: "",
                onDismissRequest = hideSettings
            )
        }
    }
}
```

#### Paso 4.2: Refactorizar PostContent

Actualizar `posts/src/commonMain/kotlin/com/rcudev/posts/ui/posts/PostContent.kt`:

```kotlin
package com.rcudev.posts.ui.posts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rcudev.posts.domain.model.Post

@Composable
internal fun PostContent(
    posts: List<Post>,
    loadingNextPage: Boolean,
    onLoadNextPage: () -> Unit,
    onItemClick: (String) -> Unit
) {
    val listState = rememberLazyListState()
    
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(
            items = posts,
            key = { index, post -> "$index-${post.id}" }
        ) { index, post ->
            PostItem(
                post = post,
                onItemClick = { onItemClick(post.url) }
            )
            
            // Detectar final de lista
            if (index == posts.lastIndex && !loadingNextPage) {
                LaunchedEffect(Unit) {
                    onLoadNextPage()
                }
            }
        }
        
        if (loadingNextPage) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
internal fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
internal fun ErrorContent(onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Error loading posts")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Composable
internal fun EmptyContent(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(message)
    }
}

// Mantener PostItem existente...
```

#### Paso 4.3: Refactorizar TopBar para UDF puro

Actualizar `composeApp/src/commonMain/kotlin/com/rcudev/stargazer/ui/components/TopBar.kt`:

```kotlin
package com.rcudev.stargazer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.rcudev.posts.domain.model.PostType
import com.rcudev.storage.POST_TYPE_FILTER
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

// Contrato para TopBar
sealed interface TopBarEvent {
    data class ChangePostType(val postType: String) : TopBarEvent
    data object NavigateBack : TopBarEvent
}

@Composable
internal fun TopBar(
    preferences: DataStore<Preferences> = koinInject(),
    showBackButton: Boolean,
    onEvent: (TopBarEvent) -> Unit = {}
) {
    val selectedPostType by preferences.data
        .map { it[POST_TYPE_FILTER] ?: PostType.ARTICLES.type }
        .collectAsState(PostType.ARTICLES.type)
    
    val scope = rememberCoroutineScope()
    
    TopBarContent(
        showBackButton = showBackButton,
        selectedPostType = selectedPostType,
        onBackClick = { onEvent(TopBarEvent.NavigateBack) },
        onPostTypeClick = { postType ->
            scope.launch {
                preferences.edit { it[POST_TYPE_FILTER] = postType }
            }
        }
    )
}

@Composable
private fun TopBarContent(
    showBackButton: Boolean,
    selectedPostType: String,
    onBackClick: () -> Unit,
    onPostTypeClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (showBackButton) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
            }
        }
        
        Text("Star Gazer")
        
        if (!showBackButton) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PostType.entries.forEach { type ->
                    FilterChip(
                        selected = selectedPostType == type.type,
                        onClick = { onPostTypeClick(type.type) },
                        label = { Text(type.type) }
                    )
                }
            }
        }
    }
}
```

---

### Fase 5: Actualizar Inyección de Dependencias

#### Paso 5.1: Actualizar KoinDI

Actualizar `posts/src/commonMain/kotlin/com/rcudev/posts/di/KoinDI.kt`:

```kotlin
package com.rcudev.posts.di

import androidx.compose.runtime.Composable
import com.rcudev.posts.data.remote.InfoServiceImpl
import com.rcudev.posts.data.remote.PostServiceImpl
import com.rcudev.posts.domain.InfoService
import com.rcudev.posts.domain.PostService
import com.rcudev.posts.ui.posts.PostPresenter
import com.rcudev.posts.ui.settings.SettingsPresenter
import com.rcudev.storage.dataStoragePreferences
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import kotlinx.coroutines.MainScope

@Composable
private fun preferences(): Module {
    val dataStorage = dataStoragePreferences()
    
    return module {
        single { dataStorage }
    }
}

private val services = module {
    singleOf<PostService>(::PostServiceImpl)
    singleOf<InfoService>(::InfoServiceImpl)
}

private val presenters = module {
    single {
        PostPresenter(
            preferences = get(),
            postService = get(),
            infoService = get(),
            scope = MainScope()
        )
    }
    single {
        SettingsPresenter(
            preferences = get(),
            scope = MainScope()
        )
    }
}

@Composable
fun getDiModules() = listOf(
    preferences(),
    services,
    presenters
)
```

---

### Fase 6: Eliminar código obsoleto

#### Paso 6.1: Eliminar ViewState.kt
El archivo `posts/src/commonMain/kotlin/com/rcudev/posts/ui/ViewState.kt` será reemplazado por `PostContract.kt`.

#### Paso 6.2: Eliminar PostViewModel.kt
Una vez migrado todo a `PostPresenter.kt`, eliminar el `PostViewModel.kt`.

---

### Fase 7: Testing

#### Paso 7.1: Crear tests para PostPresenter

Crear archivo `posts/src/commonTest/kotlin/com/rcudev/posts/ui/posts/PostPresenterTest.kt`:

```kotlin
package com.rcudev.posts.ui.posts

import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import com.rcudev.posts.domain.InfoService
import com.rcudev.posts.domain.PostService
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class PostPresenterTest {
    
    @Test
    fun `initial state is Loading`() = runTest {
        // Given
        val presenter = createPresenter()
        
        // When/Then
        presenter.state.test {
            assertIs<PostState.Loading>(awaitItem())
        }
    }
    
    @Test
    fun `loadNextPage event triggers loading`() = runTest {
        // Given
        val presenter = createPresenter()
        
        presenter.state.test {
            skipItems(1) // Skip initial loading
            
            // When
            presenter.sendEvent(PostEvent.LoadNextPage)
            
            // Then
            val state = awaitItem() as PostState.Content
            assertEquals(true, state.loadingNextPage)
        }
    }
    
    private fun createPresenter(): PostPresenter {
        // Mock dependencies
        // Return PostPresenter instance
    }
}
```

---

## 📁 Estructura de Archivos Final

```
posts/
├── src/commonMain/kotlin/com/rcudev/posts/
│   ├── data/                       (Sin cambios)
│   ├── domain/                     (Sin cambios)
│   └── ui/
│       ├── posts/
│       │   ├── PostContract.kt     (NUEVO - Estados, Eventos, Effects)
│       │   ├── PostPresenter.kt    (NUEVO - Lógica con Molecule)
│       │   ├── PostRoute.kt        (ACTUALIZADO - Sin VM)
│       │   ├── PostScreen.kt       (ACTUALIZADO - UI pura)
│       │   └── PostContent.kt      (ACTUALIZADO - UI pura)
│       └── settings/
│           ├── SettingsContract.kt (NUEVO - Estados, Eventos)
│           ├── SettingsPresenter.kt(NUEVO - Lógica con Molecule)
│           └── SettingsDropDown.kt (ACTUALIZADO - UI pura)
```

---

## 🎓 Principios Clave de la Migración

### 1. **Separación clara de responsabilidades**
- **Presenter**: Toda la lógica de estado y negocio
- **UI**: Solo renderiza y emite eventos
- **Contract**: Define el contrato entre UI y Presenter

### 2. **Inmutabilidad total**
- Estados como `sealed interface` con `data class`
- No exponer `MutableState` fuera del Presenter
- UI recibe `StateFlow<State>` de solo lectura

### 3. **Un solo flujo de datos**
```
User Action → Event → Presenter → State Update → UI Render
```

### 4. **Effects separados del Estado**
- Estado: Lo que se renderiza
- Effects: Side effects (navegación, snackbars, etc.)

### 5. **Testing simplificado**
- Presenters son `@Composable` functions testeables sin UI
- Usar `moleculeFlow` y `Turbine` para testing

---

## ✅ Checklist de Migración

- [ ] Fase 1: Agregar Molecule a dependencias
- [ ] Fase 2: Crear contratos (PostContract, SettingsContract)
- [ ] Fase 3: Implementar Presenters con Molecule
- [ ] Fase 4: Refactorizar UI a componentes puros
- [ ] Fase 5: Actualizar inyección de dependencias
- [ ] Fase 6: Eliminar ViewModels y código obsoleto
- [ ] Fase 7: Crear tests para Presenters
- [ ] Fase 8: Ejecutar tests existentes y validar
- [ ] Fase 9: Testing manual en Android e iOS

---

## 🚀 Ventajas Post-Migración

1. **Código más simple y predecible**: UDF puro elimina bugs de sincronización
2. **Testing más fácil**: Presenters son funciones puras testeables
3. **Mejor performance**: Molecule optimiza recomposiciones automáticamente
4. **Mantenibilidad**: Contrato claro entre capas
5. **Escalabilidad**: Fácil agregar nuevas features siguiendo el mismo patrón

---

## 📚 Referencias

- [Molecule GitHub](https://github.com/cashapp/molecule)
- [Molecule Blog Post](https://developer.squareup.com/blog/molecule/)
- [Circuit by Slack](https://github.com/slackhq/circuit) - Framework basado en Molecule
- [UDF Pattern](https://developer.android.com/topic/architecture/ui-layer#udf)

---

**Nota**: Esta migración es incremental. Puedes migrar pantalla por pantalla, manteniendo ViewModels y Presenters coexistiendo durante la transición.
