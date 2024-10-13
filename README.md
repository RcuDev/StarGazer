<img src="screenshots/star_gazer_icon.png" alt="Artículos" width="200"/>

# StarGazer

StarGazer es una aplicación móvil multiplataforma para Android e iOS construida utilizando Kotlin Multiplatform. Utiliza la API de [spaceflightnewsapi.net](https://www.spaceflightnewsapi.net/) para mostrar una lista de artículos, blogs y reportes sobre vuelos espaciales.

## Características

*   Multiplataforma: Disponible tanto en Android como en iOS.
*   Interfaz moderna: Una interfaz de usuario limpia e intuitiva con unos filtros superiores para acceder fácilmente a las diferentes secciones.
*   Tres secciones:
    *   **Artículos:** Muestra una lista de artículos relacionados con los vuelos espaciales.
    *   **Blogs:** Presenta una colección de publicaciones de blogs sobre la exploración espacial.
    *   **Reportes:** Proporciona acceso a informes sobre misiones y descubrimientos espaciales.
*   Vistas detalladas: Al tocar cualquier publicación, se abre una vista detallada con el contenido completo en una web embedida.
*   Modularizado:
    *   **composeApp**: Contiene la aplicación ejecutable donde en Android se ejecuta una **MainActivity** con el Composable general de la app y en iOS se carga el **MainViewController** cargando igualmente el mismo Composable.
    *   **posts**: Pantalla donde se muestran los post, settings y el detalle del post (webview).
    *   **ds**: Contiene la tipografía, colores y Theme de la aplicación.
    *   **network**: Proporciona el cliente (Ktor), que servirá para para realizar las peticiones.
    *   **storage**: Uso de DataStore para las preferencias como el modo oscuro, tipo de post seleccionado y filtro por web de la noticia del post.
    *   **utils**: proporciona métodos que pueden ser usados en varios módulos y cuya característica es que necesitan implementaciones distintas para Android e iOS.

## Capturas de pantalla

### Android

<img src="screenshots/Post_Android_Light.png" alt="Artículos" width="200"/>
<img src="screenshots/Post_Android_Dark.png" alt="Artículos" width="200">
<img src="screenshots/Detail_Android.png" alt="Artículos" width="200">
<img src="screenshots/Settings_Android.png" alt="Artículos" width="200">

### iOS

<img src="screenshots/Post_iOS_Light.png" alt="Artículos" width="200"/>
<img src="screenshots/Post_iOS_Dark.png" alt="Artículos" width="200">
<img src="screenshots/Detail_iOS.png" alt="Artículos" width="200">
<img src="screenshots/Settings_iOS.png" alt="Artículos" width="200">

## Tecnologías

*   Kotlin Multiplatform
*   Jetpack Compose (Android e iOS)
*   Compose Navigation (Tipado Seguro)
*   Ktor (Redes)
*   Koin (Inyección de Dependencias)
*   Coil (Imágenes)
*   DataStore (Preferencias)

## API

Esta aplicación utiliza la API gratuita y abierta [spaceflightnewsapi.net](https://www.spaceflightnewsapi.net/).

## WIP

*   Mejoras en la UI.
