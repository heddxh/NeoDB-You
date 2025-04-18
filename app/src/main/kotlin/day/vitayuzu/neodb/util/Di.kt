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
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.publicvalue.multiplatform.oidc.appsupport.AndroidCodeAuthFlowFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@HiltAndroidApp
class NeoDBYouApp : Application()

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Singleton
    @Provides
    fun providerAuthFlowFactory() = AndroidCodeAuthFlowFactory(useWebView = false)
}

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

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideApi(preferenceSource: LocalPreferenceSource): NeoDbApi {
        val ktorfit = ktorfit {
            Log.d("RemoteSource", "Initializing Ktorfit client...")
            baseUrl(BASEURL)
            httpClient(
                HttpClient {
                    install(ContentNegotiation) {
                        json(Json { ignoreUnknownKeys = true })
                    }
                    install(Logging) {
                        logger = Logger.ANDROID
                        level = LogLevel.NONE
                    }
                    install(Auth) {
                        bearer {
                            loadTokens {
                                val token = preferenceSource.getAccessToken()
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
        return ktorfit.createNeoDbApi()
    }
}
