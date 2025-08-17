package day.vitayuzu.neodb.data

import android.util.Log
import day.vitayuzu.neodb.BuildConfig
import io.github.g00fy2.versioncompare.Version
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class UpdateRepository @Inject constructor(private val remoteSource: RemoteSource) {

    private val _newVersionUrl = MutableStateFlow<String?>(null)
    val newVersionUrl = _newVersionUrl.asStateFlow()

    /**
     * Check update from GitHub latest release. If there is a new version,
     * emit related information and update [newVersionUrl].
     */
    fun checkUpdate() = flow {
        runCatching {
            with(remoteSource.getLatestVersionFromGithub()) {
                if (Version(tagName.drop(1)) > Version(BuildConfig.VERSION_NAME)) {
                    emit(this)
                    _newVersionUrl.update { htmlUrl }
                    Log.d("UpdateRepository", "New version found: $tagName")
                } else {
                    Log.d("UpdateRepository", "Has been the latest version")
                }
            }
        }.onFailure { e ->
            Log.e("AuthRepository", "Failed to check update", e)
        }
    }
}
