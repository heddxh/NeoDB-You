package day.vitayuzu.neodb.data

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.LocaleManagerCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import day.vitayuzu.neodb.AppScope
import day.vitayuzu.neodb.data.schema.UserPreferenceSchema
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.Locale
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Repo subscribed to [AuthRepository.accountStatus]
 * and fetch user preference when login or switching account.
 */
@Singleton
class UserPreferenceManager @Inject constructor(
    @AppLocaleLanguage private val language: String,
    private val remoteSource: RemoteSource,
    authRepo: AuthRepository,
    @AppScope scope: CoroutineScope,
) {

    val preference: StateFlow<UserPreferenceSchema>
        field = MutableStateFlow(UserPreferenceSchema.getDefault(language))

    init {
        authRepo.accountStatus
            .distinctUntilChangedBy { it.isLogin }
            .onEach {
                if (it.isLogin) {
                    refresh()
                } else {
                    reset()
                }
            }.launchIn(scope)
    }

    private suspend fun refresh() {
        runCatching {
            Log.d("UserPreferenceRepository", "Start fetching user preference")
            remoteSource.fetchSelfPreference()
        }.onSuccess { schema ->
            Log.d("UserPreferenceRepository", "End fetching user preference:$schema")
            preference.value = schema
        }.onFailure { error ->
            Log.e("UserPreferenceRepository", "Error fetching user preference: $error")
        }
    }

    private fun reset() {
        preference.value = UserPreferenceSchema.getDefault(language)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppLocaleLanguage

@Module
@InstallIn(SingletonComponent::class)
object LocaleModule {
    @Provides
    @AppLocaleLanguage
    fun provideAppLocaleLanguage(
        @ApplicationContext context: Context,
    ): String {
        val currentLanguage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager = context.getSystemService(LocaleManager::class.java)
            localeManager.applicationLocales[0].toLanguageTag()
        } else {
            LocaleManagerCompat.getApplicationLocales(context)[0]?.toLanguageTag()
        }

        return currentLanguage ?: Locale.getDefault().toLanguageTag()
    }
}
