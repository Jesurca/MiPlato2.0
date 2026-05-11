# MiPlato 2.0 - Nutrición Inteligente

<p align="center">
  <img src="https://media.licdn.com/dms/image/v2/D4E03AQG-kSwRbs1hKw/profile-displayphoto-crop_800_800/B4EZy9XjYfHkAI-/0/1772703596179?e=1779926400&v=beta&t=smPr9D9YQrGRTwgfcDWTRD3Vp9F82N38-vWCfHAWPxw" width="200">
</p>

**MiPlato** es una aplicación de nutrición moderna diseñada para ayudar a los usuarios a llevar un seguimiento de su alimentación mediante el uso de Inteligencia Artificial y una experiencia de usuario fluida.

## 🚀 Características

- **Identificación de Alimentos con IA**: Captura fotos de tu comida y deja que la IA identifique los alimentos y su valor nutricional.
- **Historial de Nutrición**: Seguimiento detallado de las comidas consumidas a lo largo del tiempo.
- **Planes Personalizados**: Visualización de planes de alimentación.
- **Sincronización en la Nube**: Integración con Firebase para mantener tus datos seguros y sincronizados.
- **Modo Offline**: Gracias a Room, la app funciona sin conexión y sincroniza los cambios cuando recuperas el acceso a internet.

## 🛠️ Tecnologías y Arquitectura

La aplicación sigue los principios de **Clean Architecture** y las recomendaciones oficiales de Android:

- **UI**: Jetpack Compose (Modern Toolkit).
- **Arquitectura**: MVVM (Model-View-ViewModel).
- **Inyección de Dependencias**: Hilt / Dagger.
- **Base de Datos**: Room (Persistencia Local).
- **Backend**: Firebase (Auth, Firestore, Storage).
- **Red**: Retrofit + OkHttp.
- **IA**: ML Kit para análisis de imágenes.
- **Cámara**: CameraX.

## 📁 Estructura del Proyecto

- `com.miplato.app.dominio`: Entidades, Repositorios (Interfaces) y Casos de Uso.
- `com.miplato.app.datos`: Implementaciones de repositorios, bases de datos local y remota (Firebase/API).
- `com.miplato.app.presentacion`: Pantallas (Compose), ViewModels y lógica de navegación.

## ✒️ Autor
**Jesús David Urbiñez Caselles**
- [LinkedIn](https://www.linkedin.com/in/jes%C3%BAs-david-urbi%C3%B1ez-caselles-a2ba633b3/)

---
*Este proyecto está en constante evolución siguiendo las mejores prácticas de desarrollo Android moderno.*
