package day.vitayuzu.neodb.data

import android.util.Log
import day.vitayuzu.neodb.util.AUTH_CALLBACK
import kotlinx.coroutines.flow.flow
import org.publicvalue.multiplatform.oidc.OpenIdConnectClient
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val remoteSource: RemoteSource,
    private val preferenceSource: LocalPreferenceSource,
) {

    val isLogin = AtomicBoolean(false) // Cache in memory, initialized in [MainActivity]

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
            isLogin.set(false)
            return null
        }
    }

    suspend fun storeAccessToken(token: String) {
        try {
            preferenceSource.storeAccessToken(token)
            isLogin.set(true)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to store access token", e)
        }
    }

    fun fetchMe() = flow {
        emit(remoteSource.fetchMe())
    }.log("Fetch self account")

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
