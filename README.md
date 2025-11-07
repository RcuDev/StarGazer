<img src="screenshots/star_gazer_icon.png" alt="StarGazer icon" width="250"/>

# StarGazer

[English](README.md) | [Español](docs/README.es.md)

**StarGazer** is a modern Kotlin Multiplatform (KMM) application for Android and iOS that showcases space-related content using the [spaceflightnewsapi.net](https://www.spaceflightnewsapi.net/) API. Built with cutting-edge technologies and a clean architecture pattern, it demonstrates best practices for cross-platform mobile development.

## ✨ Features

*   **🌍 Cross-platform**: Native experience on both Android and iOS from a single codebase
*   **🎨 Modern UI**: Clean, intuitive interface built entirely with Jetpack Compose Multiplatform
*   **📰 Three Content Sections**:
    *   **Articles**: Curated list of space-related articles
    *   **Blogs**: Space exploration blog posts
    *   **Reports**: In-depth reports on space missions and discoveries
*   **🔍 Advanced Filtering**: Filter content by news source and content type
*   **📱 Detailed Views**: Embedded WebView for full article content
*   **🌓 Dark Mode**: System-aware theme with manual toggle
*   **⚡ Reactive Architecture**: Unidirectional Data Flow (UDF) with Molecule
*   **🧪 Comprehensive Testing**: Unit and UI tests with Turbine and Mokkery

## 🏗️ Architecture

StarGazer implements a hybrid **ViewModel + Presenter + Molecule** architecture that combines the best of both worlds:

```
┌─────────────────────────────────────────┐
│         UI Layer (Compose)              │
│  • Renders state only                   │
│  • Emits events                         │
│  • Observes StateFlow<State>            │
│  • Observes Flow<Effect> for navigation │
└──────────────┬──────────▲───────────────┘
               │ Events   │ State/Effects
┌──────────────▼──────────┴───────────────┐
│           ViewModel                      │
│  • Manages lifecycle (viewModelScope)   │
│  • Channels events from UI              │
│  • Exposes StateFlow via launchMolecule │
│  • Processes effects from Presenter     │
└──────────────┬──────────▲───────────────┘
               │ Events   │ State/Effects
┌──────────────▼──────────┴───────────────┐
│          Presenter (@Composable)         │
│  • Business logic with Molecule          │
│  • Receives Flow<Event>                  │
│  • Returns Pair<State, Flow<Effect>>    │
│  • Pure reactive logic (testable)       │
└──────────────┬──────────▲───────────────┘
               │ Calls    │ Results
┌──────────────▼──────────┴───────────────┐
│        Domain/Data Layer                 │
│  • Services, Repositories, DataStore    │
└─────────────────────────────────────────┘
```

### Why This Architecture?

1. **ViewModel** provides `viewModelScope` for automatic lifecycle management
2. **Presenter** contains pure reactive logic with Molecule (testable without UI)
3. **Hybrid approach** leverages the strengths of both without duplicating responsibilities
4. **Scalable** and allows creating complex reusable Presenters

## 📦 Module Structure

```
StarGazer/
├── composeApp/          # Main application (Android/iOS)
│   ├── App.kt           # Entry point with Koin setup
│   ├── navigation/      # Type-Safe Navigation Graph
│   └── ui/components/   # Shared components (TopBar with Presenter)
│
├── posts/               # Feature module: Posts
│   ├── data/            # Service implementations
│   ├── domain/          # Interfaces and models
│   ├── ui/
│   │   ├── posts/       # Posts screen (Presenter + ViewModel)
│   │   ├── settings/    # Settings dropdown (Presenter + ViewModel)
│   │   └── webview/     # WebView for article details
│   └── di/              # Koin dependency injection
│
├── ds/                  # Design System (colors, typography, theme)
├── network/             # Ktor HTTP client configuration
├── storage/             # DataStore for preferences
└── utils/               # Multiplatform utilities
```

## Screenshots

### Android

<div style="display: flex; justify-content: space-between;">
    <img src="screenshots/Post_Android_Light.png" alt="Android Light" width="200" style="margin-right: 16px;"/>
<img src="screenshots/Post_Android_Dark.png" alt="Android Dark" width="200" style="margin-right: 16px;">
<img src="screenshots/Detail_Android.png" alt="Android Detail" width="200" style="margin-right: 16px;">
<img src="screenshots/Settings_Android.png" alt="Android Settings" width="200">
</div>

### iOS

<div style="display: flex; justify-content: space-between;">
    <img src="screenshots/Post_iOS_Light.png" alt="iOS Light" width="200" style="margin-right: 16px;"/>
<img src="screenshots/Post_iOS_Dark.png" alt="iOS Dark" width="200" style="margin-right: 16px;">
<img src="screenshots/Detail_iOS.png" alt="iOS Detail" width="200" style="margin-right: 16px;">
<img src="screenshots/Settings_iOS.png" alt="iOS Settings" width="200">
</div>

## Tech Stack

### Core Technologies
*   **Kotlin 2.2.21** - Modern, concise, and safe programming language
*   **Kotlin Multiplatform** - Share code between Android and iOS
*   **Jetpack Compose Multiplatform 1.9.2** - Declarative UI framework

### Architecture & State Management
*   **Molecule 2.2.0** - Build a StateFlow stream using Jetpack Compose
*   **Compose Navigation** - Type-safe navigation between screens
*   **Unidirectional Data Flow (UDF)** - Predictable state management

### Networking & Data
*   **Ktor 3.3.1** - Multiplatform HTTP client
*   **Kotlinx Serialization** - JSON parsing
*   **DataStore 1.1.7** - Preferences and settings persistence

### Dependency Injection
*   **Koin 4.1.1** - Lightweight DI framework with KMM support

### UI & Resources
*   **Coil 3.x** - Image loading with caching
*   **Material3** - Modern Material Design components

### Testing
*   **Kotlin Test** - Multiplatform testing framework
*   **Turbine 1.2.1** - Testing library for Kotlin Flows
*   **Mokkery 2.10.2** - Modern mocking library (replaces unmaintained MockK)
*   **Compose UI Test** - UI testing for Compose

## Testing Strategy

StarGazer implements a comprehensive testing approach covering all architecture layers:

### 🎯 Presenter Tests (with Turbine)
Tests for business logic using Molecule and Turbine to validate state emissions and effects:

```kotlin
@Test
fun emitsLoadingStateInitiallyAndThenContentWithPosts() = runTest {
    // Given - PostService returns a list of posts
    val mockResponse = Posts(count = 2, results = mockPosts)
    every { suspend { postService.getArticles(...) } } returns Result.success(mockResponse)
    
    val flow = moleculeFlow(RecompositionMode.Immediate) {
        presenter.present(events)
    }
    
    // When - Collecting the state
    flow.test {
        // Then - First emission should be Loading
        val loadingState = awaitItem().first
        assertIs<PostState.Loading>(loadingState)
        
        // Then - Second emission should be Content with posts
        val contentState = awaitItem().first as PostState.Content
        assertEquals(2, contentState.posts.size)
    }
}
```

**Test Coverage:**
*   [PostPresenterTest](posts/src/commonTest/kotlin/com/rcudev/posts/ui/posts/PostPresenterTest.kt) - Posts screen presenter logic
*   [SettingsPresenterTest](posts/src/commonTest/kotlin/com/rcudev/posts/ui/settings/SettingsPresenterTest.kt) - Settings presenter logic
*   [TopBarPresenterTest](composeApp/src/commonTest/kotlin/com/rcudev/stargazer/ui/components/TopBarPresenterTest.kt) - TopBar presenter logic

### 🌐 Service Tests (with Mokkery)
Integration tests for API services using Mokkery for mocking:

*   [InfoServiceTest](posts/src/commonTest/kotlin/com/rcudev/posts/data/remote/InfoServiceTest.kt) - Info API validation
*   [PostServiceTest](posts/src/commonTest/kotlin/com/rcudev/posts/data/remote/PostServiceTest.kt) - Posts API validation

### 🎨 UI Component Tests
Compose UI tests validating component behavior and state changes:

*   [TopBarTest](composeApp/src/commonTest/kotlin/com/rcudev/stargazer/ui/components/TopBarTest.kt) - TopBar states and interactions
*   [DarkModeItemTest](posts/src/commonTest/kotlin/com/rcudev/posts/ui/DarkModeItemTest.kt) - Dark mode toggle

### Why Mokkery?

**MockK** was discarded because `mockk-common` (the multiplatform version) has not been maintained since 2022 and causes numerous compatibility issues. **Mokkery** is the modern, actively maintained alternative with similar API and full KMM support.

## API

This app uses the free and open [spaceflightnewsapi.net](https://www.spaceflightnewsapi.net/) API to fetch space-related content.

## 🚀 Getting Started

### Prerequisites
*   **Android Studio** Hedgehog | 2023.1.1 or newer
*   **Xcode** 15+ (for iOS development)
*   **JDK** 17 or higher
*   **Kotlin** 2.2.21

### Running the Project

#### Android
1. Open the project in Android Studio
2. Select `composeApp` configuration
3. Run on an Android device or emulator

#### iOS
1. Open the project in Android Studio
2. Select `iosApp` configuration
3. Run on an iOS simulator or device

Alternatively, open `iosApp/iosApp.xcodeproj` in Xcode and run from there.

## 📚 Key Concepts

### Contract Pattern
Each feature defines a **Contract** containing:
*   `State`: Immutable UI state (sealed interface)
*   `Event`: User interactions (sealed interface)
*   `Effect`: One-time side effects like navigation (sealed interface)

Example from `PostContract.kt`:
```kotlin
sealed interface PostState {
    data object Loading : PostState
    data object Error : PostState
    data class Content(val posts: List<Post>) : PostState
}

sealed interface PostEvent {
    data class OnPostClick(val url: String) : PostEvent
    data object LoadNextPage : PostEvent
}

sealed interface PostEffect {
    data class NavigateToWebView(val url: String) : PostEffect
}
```

### Presenter with Molecule
Presenters are `@Composable` functions that use Molecule to create reactive state:

```kotlin
@Composable
fun present(events: Flow<PostEvent>): Pair<PostState, Flow<PostEffect>> {
    var state by remember { mutableStateOf<PostState>(PostState.Loading) }
    val effects = remember { MutableSharedFlow<PostEffect>() }
    
    // Reactive business logic here
    
    return state to effects
}
```

### ViewModel as Lifecycle Manager
ViewModels delegate to Presenters but manage Android lifecycle:

```kotlin
class PostViewModel(private val presenter: PostPresenter) : ViewModel() {
    private val events = MutableSharedFlow<PostEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_LATEST
    )
    private val presentationResult = viewModelScope.launchMolecule(
        mode = RecompositionMode.Immediate
    ) {
        presenter.present(events)  // ✅ Solo se llama UNA vez
    }

    val state: StateFlow<PostState> = presentationResult
        .map { it.first }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PostState.Loading
        )

    val effects: Flow<PostEffect> = presentationResult
        .flatMapLatest { it.second }

}
```

## 🛠️ Development

### Code Style
*   Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
*   Use meaningful variable and function names
*   Add KDoc comments for public APIs
*   Keep functions small and focused

### Adding a New Feature
1. Create feature module in appropriate package
2. Define Contract (State, Event, Effect)
3. Implement Presenter with business logic
4. Create ViewModel as lifecycle bridge
5. Build Composable UI
6. Write tests (Presenter, Service, UI)
7. Add to navigation graph

## 📈 Roadmap

- [x] Hybrid ViewModel + Presenter architecture
- [x] Type-safe navigation
- [x] Comprehensive testing with Turbine
- [x] Dark mode support
- [ ] Offline caching
- [ ] Search functionality
- [ ] Favorites/Bookmarks
- [ ] Share articles
- [ ] Push notifications

## WIP

*   UI/UX improvements
*   Performance optimizations
*   Additional test coverage
*   CI/CD pipeline

## 📄 License

This project is available under the MIT License.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

---

**Built with ❤️ using Kotlin Multiplatform**
