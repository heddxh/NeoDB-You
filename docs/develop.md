## OpenAPI Document Location
The OpenAPI document is available at [https://neodb.social/api/docs](https://neodb.social/api/docs). It is generated by `django-ninja`.

# Android/Kotlin

## Do I need `org.jetbrains.kotlinx:kotlinx-coroutines-android`?
No. Although the [Android developer documentation](https://developer.android.com/kotlin/coroutines#dependency) suggests adding this dependency, it is already included as a transitive dependency in the library.  
See: [Stack Overflow Answer](https://stackoverflow.com/a/74861174/22591462)

## Why is the top-level `build.gradle.kts` empty?
Since this is a single-module project, all plugins and dependencies are declared in the `:app` module.

## About `Modifier`
In my composable functions, `Modifier` should be the last parameter and passed to the first child that emits UI (e.g., `Row`, `Text`, etc.).

### Why isn't the latter `Modifier.fillMaxSize` working?
In layouts like `Row`, where child composables are measured from left to right, if an earlier composable already uses `Modifier.fillMaxSize`, subsequent composables won't receive any remaining space.

## Ktor logging

Add dependencies:

```toml
slf4j-android = { group = "org.slf4j", name = "slf4j-android", version.ref = "slf4jAndroid" }
ktor-client-logging = { group = "io.ktor", name = "ktor-client-logging", version.ref = "ktor" }
```

Then change logger:

```kotlin
val ktorfit = ktorfit {
    httpClient(HttpClient {
        install(Logging) {
            logger = Logger.ANDROID // <<< DEFAULT is nor working
            level = LogLevel.ALL
        }
    })
}
```

## `MutableStateFlow` vs `MutableState`

If you don't familiar with Kotlin flow, here is the key point:

`MutableStateFlow` is from Kotlin coroutines, which is a class from language official library. But
`MutableState` is a concept from Compose framework, basically the state in Compose is built on this.

So, since it is recommended to avoid reference UI staff in ViewModel, using `MutableStateFlow` as
public UiState in ViewModel is preferred.

See
also: https://developer.android.com/develop/ui/compose/state?hl=en#use-other-types-of-state-in-jetpack-compose