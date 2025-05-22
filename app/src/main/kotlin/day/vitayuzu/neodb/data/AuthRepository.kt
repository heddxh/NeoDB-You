package day.vitayuzu.neodb.data

import android.util.Log
import day.vitayuzu.neodb.data.schema.UserSchema
import day.vitayuzu.neodb.util.AUTH_CALLBACK
import de.jensklingenberg.ktorfit.Ktorfit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.update
import org.publicvalue.multiplatform.oidc.OpenIdConnectClient
import org.publicvalue.multiplatform.oidc.ktor.clearTokens
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val remoteSource: RemoteSource,
    private val preferenceSource: LocalPreferenceSource,
    private val ktorfit: Ktorfit,
) {

    private val _accountStatus = MutableStateFlow(AccountStatus())
    val accountStatus = _accountStatus.asStateFlow()

    suspend fun updateAccountStatus() {
        Log.d("AuthRepository", "Updating account status...")
        val token = getAccessToken()
        if (token == null) {
            _accountStatus.update { AccountStatus() }
        } else {
            fetchSelfAccountInfo
                .onEmpty {
                    _accountStatus.update { AccountStatus() }
                    revoke() // clean storage
                }.collect { userSchema ->
                    _accountStatus.update { AccountStatus(true, userSchema) }
                }
        }
    }

    suspend fun registerAppIfNeeded(): Result<OpenIdConnectClient> {
        val clientId = preferenceSource.getAuthClientId()
        val clientSecret = preferenceSource.getAuthClientSecret()

        if (clientId != null && clientSecret != null) {
            Log.d("AuthRepository", "Found saved auth client identification")
            return Result.success(constructAuthClient(clientId, clientSecret))
        } else {
            Log.d("AuthRepository", "No saved auth client identification found, registering...")
            val oauthClientResult = remoteSource.registerOauthAPP()

            if (oauthClientResult.isSuccess) {
                val oauthClientData = oauthClientResult.getOrElse {
                    return signalRegistrationFailure()
                }
                try {
                    preferenceSource.storeAuthClientIdentify(oauthClientData)
                    Log.d("AuthRepository", "Saved auth client")
                    return Result.success(
                        constructAuthClient(
                            oauthClientData.clientId,
                            oauthClientData.clientSecret,
                        ),
                    )
                } catch (e: Exception) {
                    return signalRegistrationFailure()
                }
            } else {
                return signalRegistrationFailure()
            }
        }
    }

    suspend fun getAccessToken(): String? {
        try {
            return preferenceSource.getAccessToken()
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to get access token", e)
            return null
        }
    }

    /**
     * Store access token in local storage and force ktor to refresh token.
     */
    suspend fun storeAccessToken(token: String) {
        try {
            preferenceSource.storeAccessToken(token)
            ktorfit.httpClient.clearTokens()
            updateAccountStatus()
        } catch (e: Exception) {
            revoke() // clean storage
            Log.e("AuthRepository", "Failed to store access token", e)
        }
    }

    /**
     * Remove all auth data from local storage and force Ktor to refresh token.
     */
    suspend fun revoke() {
        try {
            preferenceSource.deleteAllAuthData()
            ktorfit.httpClient.clearTokens()
            _accountStatus.update { AccountStatus() }
            Log.d("AuthRepository", "Revoked all auth data")
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to delete all auth data", e)
        }
    }

    private val fetchSelfAccountInfo = flow {
        emit(remoteSource.fetchSelfAccountInfo())
    }.log("Fetch self account", tag = "AuthRepository")

    private companion object {
        val AuthRegisterException: Exception = Exception("Failed to register app")

        fun <T> signalRegistrationFailure(): Result<T> {
            Log.e("AuthRepository", "Failed to register app")
            return Result.failure(AuthRegisterException)
        }

        fun constructAuthClient(
            clientId: String,
            clientSecret: String,
        ) = OpenIdConnectClient {
            endpoints {
                tokenEndpoint = "https://neodb.social/oauth/token"
                authorizationEndpoint = "https://neodb.social/oauth/authorize/"
                revocationEndpoint = "https://neodb.social/oauth/revoke"
                userInfoEndpoint = null
                endSessionEndpoint = null
            }
            this.clientId = clientId
            this.clientSecret = clientSecret
            redirectUri = AUTH_CALLBACK
            scope = "read write"
        }
    }
}

data class AccountStatus(
    val isLogin: Boolean = false,
    val account: UserSchema? = null,
)
