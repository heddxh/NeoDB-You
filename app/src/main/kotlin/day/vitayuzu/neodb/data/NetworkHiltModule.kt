package day.vitayuzu.neodb.data

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import day.vitayuzu.neodb.data.AppSettingsManager.Companion.VERBOSE_LOG
import day.vitayuzu.neodb.util.BASE_URL
import day.vitayuzu.neodb.util.toSupportedTag
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.appendIfNameAbsent
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.util.Locale
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkHiltModule {

    @Singleton
    @Provides
    fun provideApi(ktorfit: Ktorfit): NeoDbApi = ktorfit.createNeoDbApi()

    @Singleton
    @Provides
    fun provideHttpClient(
        appSettingsMgr: AppSettingsManager,
        userPreference: dagger.Lazy<UserPreferenceManager>, // avoid dependency cycle
    ): Ktorfit = ktorfit {
        val contentLanguagePlugin = createClientPlugin("ContentLanguage") {
            onRequest { request, _ ->
                val settings = appSettingsMgr.appSettings.filterNotNull().first()
                val language = when (settings.contentLanguageSource) {
                    ContentLanguageSource.Server -> if (
                        request.url.encodedPath.endsWith("/me/preference")
                    ) {
                        // This request makes the server language ready, so it cannot await itself.
                        userPreference.get().currentLanguage()
                    } else {
                        userPreference.get().serverLanguage()
                    }

                    ContentLanguageSource.App -> Locale.getDefault().toSupportedTag()

                    ContentLanguageSource.User -> settings.customContentLanguage
                }
                request.headers.appendIfNameAbsent(HttpHeaders.AcceptLanguage, language)
            }
        }
        httpClient(
            HttpClient {
                baseUrl("https://$BASE_URL/api/") // using https://neodb.social/api as default
                install(contentLanguagePlugin)
                install(Logging) {
                    logger = Logger.ANDROID
                    runBlocking {
                        level = if (appSettingsMgr.getAuthData(VERBOSE_LOG) == true) {
                            LogLevel.ALL
                        } else {
                            LogLevel.NONE
                        }
                    }
                }
                defaultRequest {
                    headers.appendIfNameAbsent(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json.toString(),
                    )
                }
                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = true
                            isLenient = true // Allow unquoted string
                            explicitNulls = false // Allow missing nullable fields
                            coerceInputValues = true // Allow to use default value when null
                        },
                    )
                }
                install(Auth) {
                    bearer {
                        // Skip auth header
                        sendWithoutRequest {
                            !it.url.host.contains("api.github.com") &&
                                !it.url.host.contains("api.neodb.app") &&
                                !it.url.encodedPath.contains("api/v1") &&
                                !it.url.encodedPath.contains("oauth/")
                        }
                        loadTokens {
                            // Will be cached until process die, use clearToken() to refresh.
                            // See: AuthRepository.kt
                            val token = appSettingsMgr.getAuthData(AppSettingsManager.ACCESS_TOKEN)
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
