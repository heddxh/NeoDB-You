package day.vitayuzu.neodb.data

import android.util.Log
import day.vitayuzu.neodb.BuildConfig
import io.github.g00fy2.versioncompare.Version
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class OtherRepository @Inject constructor(private val remoteSource: RemoteSource) {
    val checkUpdateFlow = flow {
        runCatching {
            with(remoteSource.getLatestVersionFromGithub()) {
                if (Version(tagName.drop(1)) > Version(BuildConfig.VERSION_NAME)) {
                    emit(this)
                    Log.d("CheckUpdateFlow", "New version found: $tagName")
                } else {
                    emit(null)
                    Log.d("CheckUpdateFlow", "Has been the latest version")
                }
            }
        }.onFailure { e ->
            emit(null)
            Log.e("CheckUpdateFlow", "Failed to check update", e)
        }
    }

    val fetchPublicInstancesFlow = flow {
        runCatching {
            remoteSource.fetchPublicInstances()
        }.onSuccess { instances ->
            // Sort by total users in descending order
            emit(instances.sortedByDescending { it.totalUsers })
        }.onFailure { error ->
            Log.e("FetchPublicInstancesFlow", "Failed to fetch public instances", error)
        }
    }
}
