package day.vitayuzu.neodb.util

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.publicvalue.multiplatform.oidc.appsupport.AndroidCodeAuthFlowFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@HiltAndroidApp
class NeoDBYouApp : Application()

@Module
@InstallIn(SingletonComponent::class)
object AuthFlowModule {
    @Singleton
    @Provides
    fun authFlowFactoryProvider() = AndroidCodeAuthFlowFactory(useWebView = false)
}

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
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
