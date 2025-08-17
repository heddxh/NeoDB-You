package day.vitayuzu.neodb.data

import android.util.Log
import day.vitayuzu.neodb.data.LocalPreferenceSource.Companion.ACCESS_TOKEN
import day.vitayuzu.neodb.data.LocalPreferenceSource.Companion.CLIENT_ID
import day.vitayuzu.neodb.data.LocalPreferenceSource.Companion.CLIENT_SECRET
import day.vitayuzu.neodb.data.LocalPreferenceSource.Companion.INSTANCE_URL
import day.vitayuzu.neodb.data.schema.InstanceSchema
import day.vitayuzu.neodb.data.schema.UserSchema
import day.vitayuzu.neodb.util.BASE_URL
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.plugin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for authentication and account information.
 *
 * Authentication flow:
 * 0. Launch [day.vitayuzu.neodb.OauthActivity].
 * 1. [validateInstanceUrl]: fetches instance info from given url.
 * 2. [registerClientIfNeeded]: register app if needed, return client id and secret,
 * store all related stuff.
 * 3. Launch browser, permitted from user and get auth code.
 * 4. [exchangeAccessToken]: exchange auth code for access token.
 * 5. [updateAccountStatus]: logged, get account information.
 */
@Singleton
class AuthRepository @Inject constructor(
    private val remoteSource: RemoteSource,
    private val preferenceSource: LocalPreferenceSource,
    private val ktorfit: Ktorfit,
) {

    private val _accountStatus = MutableStateFlow(AccountStatus())
    val accountStatus = _accountStatus.asStateFlow()

    init {
        ktorfit.httpClient.plugin(HttpSend).intercept { request ->
            // Bypass other requests or github will return 403
            if (request.url.host.contains("api.github.com")) {
                return@intercept execute(request)
            }
            // Intercept request host if user has logged.
            if (accountStatus.value.isLogin) {
                request.url.host = accountStatus.value.instanceUrl
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

    /**
     * Register app if not already registered.
     * If successful, store client id, secret, instance url in [LocalPreferenceSource].
     * @return `Pair<ClientId, ClientSecret>` if successfully registered.
     */
    suspend fun registerClientIfNeeded(instanceUrl: String): Result<Pair<String, String>> {
        val clientId = preferenceSource.get(CLIENT_ID)
        val clientSecret = preferenceSource.get(CLIENT_SECRET)

        if (clientId != null &&
            clientSecret != null &&
            instanceUrl == accountStatus.value.instanceUrl
        ) {
            Log.d("AuthRepository", "Found saved auth client identification")
            return Result.success(Pair(clientId, clientSecret))
        } else {
            Log.d("AuthRepository", "No saved auth client identification found, registering...")
            runCatching {
                remoteSource.registerOauthAPP(instanceUrl)
            }.onSuccess { oauthData ->
                with(preferenceSource) {
                    store(INSTANCE_URL, instanceUrl)
                    store(CLIENT_ID, oauthData.clientId)
                    store(CLIENT_SECRET, oauthData.clientSecret)
                }
                _accountStatus.update { it.copy(instanceUrl = instanceUrl) }
                Log.d("AuthRepository", "Registered client saved")
                return Result.success(Pair(oauthData.clientId, oauthData.clientSecret))
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
    suspend fun exchangeAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
    ) {
        val instanceUrl = accountStatus.value.instanceUrl
        runCatching {
            remoteSource
                .exchangeAccessToken(instanceUrl, clientId, clientSecret, code)
                .accessToken
        }.onSuccess { token ->
            preferenceSource.store(ACCESS_TOKEN, token)
            ktorfit.httpClient.clearToken()
            updateAccountStatus()
        }.onFailure {
            Log.e("AuthRepository", "Failed to exchange access token", it)
        }
    }

    suspend fun updateAccountStatus() {
        Log.d("AuthRepository", "Updating account status...")
        val token = preferenceSource.get(ACCESS_TOKEN)
        if (token == null) {
            _accountStatus.update { AccountStatus() }
        } else {
            runCatching {
                remoteSource.fetchSelfAccountInfo(accountStatus.value.instanceUrl)
            }.onSuccess { userSchema ->
                _accountStatus.update {
                    it.copy(
                        isLogin = true,
                        account = userSchema,
                    )
                }
            }.onFailure {
                Log.e("AuthRepository", "Failed to fetch self account info", it)
            }
        }
    }

    /**
     * Remove all auth data from local storage and revoke token.
     */
    suspend fun revoke() {
        try {
            preferenceSource.getAllAuthData()?.let {
                val (instanceUrl, clientId, clientSecret, token) = it
                remoteSource.revokeAccessToken(instanceUrl, clientId, clientSecret, token)
            }
            preferenceSource.deleteAllAuthData()
            ktorfit.httpClient.clearToken()
            _accountStatus.update { AccountStatus() }
            Log.d("AuthRepository", "Revoked all auth data")
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to delete all auth data", e)
        }
    }

    /**
     * WORKAROUND: Manually clear ktor cached tokens, see: https://youtrack.jetbrains.com/issue/KTOR-4759
     *  see: https://github.com/kalinjul/kotlin-multiplatform-oidc/blob/95769c578224ccf2da99087c47f2ecbf39bcb1e4/oidc-ktor/src/commonMain/kotlin/org/publicvalue/multiplatform/oidc/ktor/HttpClient%2BclearTokens.kt
     */
    private fun HttpClient.clearToken() {
        authProvider<BearerAuthProvider>()?.clearToken()
    }
}

data class AccountStatus(
    val isLogin: Boolean = false,
    val instanceUrl: String = BASE_URL,
    val account: UserSchema? = null,
)
