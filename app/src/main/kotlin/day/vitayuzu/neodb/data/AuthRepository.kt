package day.vitayuzu.neodb.data

import android.util.Log
import day.vitayuzu.neodb.util.AUTH_CALLBACK
import org.publicvalue.multiplatform.oidc.OpenIdConnectClient
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val remoteSource: RemoteSource,
    private val preferenceSource: LocalPreferenceSource,
) {
    suspend fun registerAppIfNeeded(): Result<OpenIdConnectClient> {
        val clientId = preferenceSource.getAuthClientId()
        val clientSecret = preferenceSource.getAuthClientSecret()

        if (clientId != null && clientSecret != null) {
            Log.d("AuthRepository", "Found saved auth client")
            return Result.success(constructAuthClient(clientId, clientSecret))
        } else {
            Log.d("AuthRepository", "No saved auth client found, registering...")
            val oauthClientResult = remoteSource.registerOauthAPP()

            if (oauthClientResult.isSuccess) {
                val oauthClientData =
                    oauthClientResult.getOrElse {
                        return signalRegistrationFailure()
                    }
                try {
                    preferenceSource.saveAuthClientIdentify(oauthClientData)
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

    suspend fun storeAccessToken(token: String) {
        try {
            preferenceSource.storeAccessToken(token)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to store access token", e)
        }
    }

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
        }
    }
}
