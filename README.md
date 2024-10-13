
<img src="screenshots/star_gazer_icon.png" alt="StarGazer icon" width="250"/>

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

*   Mejoras en la UI/UX
*   Test
