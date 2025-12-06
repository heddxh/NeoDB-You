# NeoDB You - Project Context

## Project Overview

**NeoDB You** is a native Android application for [NeoDB](https://neodb.net/), a Fediverse platform
for managing collections and reviews of cultural products (books, movies, games, etc.). It is built
using modern Android development practices, featuring **Jetpack Compose** for UI and **Material You
Expressive** design.

### Key Technologies

* **Language:** Kotlin (v2.2+)
* **UI Framework:** Jetpack Compose (Material 3/Material You Expressive)
* **Navigation:** AndroidX Navigation 3
* **Dependency Injection:** Hilt
* **Networking:** Ktorfit (Retrofit-like interface for Ktor) & Ktor Client
* **Image Loading:** Coil 3
* **Asynchronous:** Kotlin Coroutines & Flows
* **Build System:** Gradle (Kotlin DSL) with Version Catalogs

## Building and Running

### Prerequisites

* JDK 21
* Android Studio Otter

### Key Commands

No you will never build app. Always tell me what you want to do, like tell me build the project when
you done.

## Architecture & File Structure

The project follows a standard Android MVVM architecture using Hilt for dependency injection.

* **`app/src/main/kotlin/day/vitayuzu/neodb/`**: Root package.
    * **`NeoDBYouApp.kt`**: Application class, Hilt setup (`@HiltAndroidApp`).
    * **`MainActivity.kt`**: Main entry point. Sets up Navigation 3 (`NavDisplay`) and the global
      theme.
    * **`OauthActivity.kt`**: Handles OAuth2 redirection schemes.
    * **`data/`**: Data layer (Repositories, API schemas, Data Sources).
        * `NeoDBRepository.kt`: Main repository for NeoDB data.
        * `AuthRepository.kt`: Authentication logic.
    * **`ui/`**: UI layer.
        * `component/`: Reusable Compose UI components.
        * `page/`: Screen-level composables (Home, Detail, Library, Login, Settings).
        * `theme/`: Material 3 theme definitions (Color, Type, Shape).
    * **`util/`**: Utility classes (Navigation, Date/Time, Constants).

## Development Conventions

### UI & Compose

* **Modifiers:** In composable functions, `modifier: Modifier` should be the **last required
  parameter** and passed to the root layout of the composable.
* **State Management:** Use `MutableStateFlow` in ViewModels for exposing UI state. Avoid using
  `MutableState` (Compose state) directly in ViewModels.
* **Navigation:** The project uses **Navigation 3**. Routes are defined in `AppNavigator` (sealed
  interface `AppDestination`).

### Networking & Data

* **Ktorfit:** API interfaces are defined using Ktorfit annotations.
* **OAuth:** Due to Mastodon/NeoDB API limitations, the `client_secret` is currently stored in the
  app (confidential client workaround).
* **Logging:** Uses `slf4j-android` with Ktor's `Logger.ANDROID`.
* **Persistence Store:**
  Uses [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) for local
  storage such as app preferences in
  `app/src/main/kotlin/day/vitayuzu/neodb/data/AppSettingsManager.kt`

### Build & Dependencies

* Dependencies are managed in `gradle/libs.versions.toml`.
* `app/build.gradle.kts` contains the module-specific configuration.
* You can search the web to introduce in new dependencies in necessary. But you MUST wait for my
  permit before adding a new dependency.

## Git Workflow

* **Branching:** Always create a new branch for specific tasks, features, or bug fixes. Do not
  commit directly to `main` or `master` unless explicitly instructed.
    * Naming convention: `feature/feature-name`, `fix/bug-description`, `refactor/scope`.
* **Atomic Commits:** Break down large changes into smaller, self-contained commits. This aids in
  code review and debugging.
* **Commit Messages:** Use clear, descriptive commit messages following the **Conventional Commits**
  format (e.g., `feat: ...`, `fix: ...`, `docs: ...`).

## Code Style

* Strictly follow `.editorconfig` with Ktlint and compose
  rules: https://mrmans0n.github.io/compose-rules/
* Extract string resource instead of hardcoded for i18n(You don't need to translate it yourself. But
  if you update string value, please let me know).
