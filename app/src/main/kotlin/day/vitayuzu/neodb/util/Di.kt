package day.vitayuzu.neodb.util

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import day.vitayuzu.neodb.data.LocalPreferenceSource
import day.vitayuzu.neodb.data.NeoDbApi
import day.vitayuzu.neodb.data.createNeoDbApi
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.appendIfNameAndValueAbsent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import javax.inject.Qualifier
import javax.inject.Singleton

@HiltAndroidApp
class NeoDBYouApp : Application()

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Singleton
    @Provides
    fun providerDataStore(
        @ApplicationContext context: Context,
    ) = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile(USER_PREFERENCES) },
    )
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {
    @Provides
    @Singleton
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ExternalScope

@Module
@InstallIn(SingletonComponent::class)
object CoroutineScopeModule {
    @Provides @Singleton @ExternalScope
    fun provideExternalScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideApi(ktorfit: Ktorfit): NeoDbApi = ktorfit.createNeoDbApi()

    @Singleton
    @Provides
    fun provideHttpClient(preferenceSource: LocalPreferenceSource): Ktorfit = ktorfit {
        httpClient(
            HttpClient {
                baseUrl("https://$BASE_URL/api/") // using https://neodb.social/api as default
                install(Logging) {
                    logger = Logger.ANDROID
//                    level = LogLevel.NONE
                }
                defaultRequest {
                    headers.appendIfNameAndValueAbsent("Content-Type", "application/json")
                }
                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = true
                            isLenient = true // Allow unquoted string
                            explicitNulls = false // Allow missing nullable fields
                        },
                    )
                }
                install(Auth) {
                    bearer {
                        // Skip auth header
                        sendWithoutRequest {
                            !it.url.host.contains("api.github.com")
                            !it.url.encodedPath.contains("api/v1")
                            !it.url.encodedPath.contains("oauth/")
                        }
                        loadTokens {
                            // Will be cached until process die, use clearToken() to refresh.
                            // See: AuthRepository.kt
                            val token = preferenceSource.get(LocalPreferenceSource.ACCESS_TOKEN)
                            if (token != null) {
                                // No expire time, no need to refresh
                                BearerTokens(token, null)
                            } else {
                                Log.d("Ktorfit", "No token found, skipping bearer")
                                null
                            }
                        }
                    }
                }
            },
        )
    }
}
