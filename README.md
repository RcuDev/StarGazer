<img src="screenshots/star_gazer_icon.png" alt="StarGazer icon" width="250"/>

# StarGazer

[English](README.md) | [Español](docs/README.es.md)

StarGazer is a KMM application for Android and iOS built using Kotlin Multiplatform. It uses the [spaceflightnewsapi.net](https://www.spaceflightnewsapi.net/) API to display a list of articles, blogs, and reports about spaceflight.

## Features

*   Cross-platform: Available on both Android and iOS.
*   Modern UI: A clean and intuitive user interface with top filters for easy access to different sections.
*   Three sections:
    *   **Articles:** Displays a list of articles related to spaceflight.
    *   **Blogs:** Features a collection of blog posts about space exploration.
    *   **Reports:** Provides access to reports on space missions and discoveries.
*   Detailed views: Tapping any post opens a detailed view with the full content embedded in a web view.
*   Modularized:
    *   **composeApp**: Contains the executable app where in Android it runs a **MainActivity** with the general Composable of the app, and in iOS it loads the **MainViewController** similarly loading the same Composable.
    *   **posts**: Screen showing posts, settings and the post detail on webview.
    *   **ds**: Contains the app's typography, colors, and theme.
    *   **network**: Provides the client (Ktor) used to make network requests.
    *   **storage**: Uses DataStore for preferences like dark mode, selected post type, and filter by post news website.
    *   **utils**: Provides methods used across different modules, which require distinct implementations for Android and iOS.

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

*   Kotlin Multiplatform
*   Jetpack Compose (Android and iOS)
*   Compose Navigation (Type Safe)
*   Ktor (Networking)
*   Koin (Dependency Injection)
*   Coil (Images)
*   DataStore (Preferences)
*   Mokkery (Testing Mocks)

## Testing

The purpose of the changes applied to the tests is to make them fully Multiplatform compatible.

For data mocking, **MockK** was discarded because the only part compatible with Multiplatform is mockk-common, which has not been maintained since 2022, in addition to the multiple errors it causes without clear solutions.

As an updated alternative with similar functionality, I decided to use **[mokkery](https://mokkery.dev/)**, which offers the same methods and capabilities.

In the **composeApp** module, compose tests were added to validate the different states of the **TopBar**, verifying both its initial states and updates when data in **DataStore** is modified.

*   [TopBarTest (UI)](composeApp/src/commonTest/kotlin/com/rcudev/stargazer/ui/components/TopBarTest.kt)

In the **posts** module, tests were added to validate the **info** and **posts** services. Both a positive result and mapper results are validated to ensure they are correct and match the fake data. Additionally, compose tests were added to validate the **switch** that toggles between light and dark mode.

*   [InfoServiceTest](posts/src/commonTest/kotlin/com/rcudev/posts/data/remote/InfoServiceTest.kt)
*   [PostServiceTest](posts/src/commonTest/kotlin/com/rcudev/posts/data/remote/PostServiceTest.kt)
*   [DarkModeItemTest (UI)](posts/src/commonTest/kotlin/com/rcudev/posts/ui/DarkModeItemTest.kt)

For the compose tests, both composables inject preferences through **koin**, which caused failures in initial runs. To address this, I created a composable to set up a context with the necessary instances:

```kotlin
import androidx.compose.runtime.Composable
import com.rcudev.posts.di.getDiModules
import org.koin.compose.KoinApplication

@Composable
internal fun KoinTestFrame(
    content: @Composable () -> Unit
) {
    val diModules = getDiModules()

    KoinApplication(application = {
        modules(diModules)
    }) {
        content()
    }
}
```

## API

This app uses the free and open [spaceflightnewsapi.net](https://www.spaceflightnewsapi.net/) API.

## WIP

*   UI/UX improvements.
*   Testing.
*   Minor fixes.
