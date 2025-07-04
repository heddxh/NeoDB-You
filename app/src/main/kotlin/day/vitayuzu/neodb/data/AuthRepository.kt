package day.vitayuzu.neodb.data

import android.util.Log
import day.vitayuzu.neodb.data.LocalPreferenceSource.Companion.ACCESS_TOKEN
import day.vitayuzu.neodb.data.LocalPreferenceSource.Companion.CLIENT_ID
import day.vitayuzu.neodb.data.LocalPreferenceSource.Companion.CLIENT_SECRET
import day.vitayuzu.neodb.data.LocalPreferenceSource.Companion.INSTANCE_URL
import day.vitayuzu.neodb.data.schema.InstanceSchema
import day.vitayuzu.neodb.data.schema.UserSchema
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for authentication and account information.
 */
@Singleton
class AuthRepository @Inject constructor(
    private val remoteSource: RemoteSource,
    private val preferenceSource: LocalPreferenceSource,
    private val ktorfit: Ktorfit,
) {

    private val _accountStatus = MutableStateFlow(AccountStatus())
    val accountStatus = _accountStatus.asStateFlow()

    val instanceUrl get() = preferenceSource.instanceUrl

    init {
        // Intercept request host if instanceUrl is set.
        ktorfit.httpClient.plugin(HttpSend).intercept { request ->
            if (instanceUrl != null) {
                request.url.host = instanceUrl.toString() // can't smart cast here
            }
            execute(request)
        }
    }

    /**
     * @param instanceUrl Normalized instance url, without scheme and trailing slash.
     */
    suspend fun validateInstanceUrl(instanceUrl: String): Result<InstanceSchema> = try {
        val instanceInfo = remoteSource.fetchInstanceInfo(instanceUrl)
        Result.success(instanceInfo)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateAccountStatus() {
        Log.d("AuthRepository", "Updating account status...")
        val token = preferenceSource.get(ACCESS_TOKEN)
        if (token == null) {
            _accountStatus.update { AccountStatus() }
        } else {
            runCatching {
                remoteSource.fetchSelfAccountInfo()
            }.onSuccess { userSchema ->
                _accountStatus.update {
                    AccountStatus(true, userSchema)
                }
            }.onFailure {
                Log.e("AuthRepository", "Failed to fetch self account info", it)
            }
        }
    }

    /**
     * Register app if not already registered.
     * If successful, store client id, secret, instance url in local storage.
     * @return Client id and secret if successfully registered.
     */
    suspend fun registerClientIfNeeded(instanceUrl: String): Result<Pair<String, String>> {
        val clientId = preferenceSource.get(CLIENT_ID)
        val clientSecret = preferenceSource.get(CLIENT_SECRET)

        if (clientId != null &&
            clientSecret != null &&
            instanceUrl == preferenceSource.instanceUrl
        ) {
            Log.d("AuthRepository", "Found saved auth client identification")
            return Result.success(Pair(clientId, clientSecret))
        } else {
            Log.d("AuthRepository", "No saved auth client identification found, registering...")
            runCatching {
                remoteSource.registerOauthAPP(instanceUrl)
            }.onSuccess {
                preferenceSource.store(INSTANCE_URL, instanceUrl)
                preferenceSource.store(CLIENT_ID, it.clientId)
                preferenceSource.store(CLIENT_SECRET, it.clientSecret)
                Log.d("AuthRepository", "Registered client saved")
                return Result.success(Pair(it.clientId, it.clientSecret))
            }.onFailure {
                Log.e("AuthRepository", "Failed to register client", it)
            }
        }

        Log.e("AuthRepository", "Failed to register app")
        return Result.failure(Throwable())
    }

    /**
     * Get and store access token from web api(NeoDB).
     */
    fun exchangeAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
    ) = flow {
        val instanceUrl = preferenceSource.instanceUrl
        if (instanceUrl == null) {
            Log.e("AuthRepository", "No instance url found for exchange access token")
            throw Throwable()
        }

        val token =
            remoteSource.exchangeAccessToken(instanceUrl, clientId, clientSecret, code).accessToken
        preferenceSource.store(ACCESS_TOKEN, token)

        println(preferenceSource.instanceUrl)
        updateAccountStatus()
        emit(token)
    }.log("Exchange access token", tag = "AuthRepository")

    /**
     * Remove all auth data from local storage.
     */
    suspend fun revoke() {
        try {
            preferenceSource.deleteAllAuthData()
            _accountStatus.update { AccountStatus() }
            Log.d("AuthRepository", "Revoked all auth data")
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to delete all auth data", e)
        }
    }
}

data class AccountStatus(
    val isLogin: Boolean = false,
    val account: UserSchema? = null,
)
