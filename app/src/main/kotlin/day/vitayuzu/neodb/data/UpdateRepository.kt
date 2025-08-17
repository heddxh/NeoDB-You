package day.vitayuzu.neodb.data

import android.util.Log
import day.vitayuzu.neodb.BuildConfig
import io.github.g00fy2.versioncompare.Version
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateRepository @Inject constructor(private val remoteSource: RemoteSource) {
    val checkUpdateFlow = flow {
        runCatching {
            with(remoteSource.getLatestVersionFromGithub()) {
                if (Version(tagName.drop(1)) > Version(BuildConfig.VERSION_NAME)) {
                    emit(this)
                    Log.d("UpdateRepository", "New version found: $tagName")
                } else {
                    emit(null)
                    Log.d("UpdateRepository", "Has been the latest version")
                }
            }
        }.onFailure { e ->
            emit(null)
            Log.e("UpdateRepository", "Failed to check update", e)
        }
    }
}
