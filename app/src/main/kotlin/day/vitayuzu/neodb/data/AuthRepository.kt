package day.vitayuzu.neodb.data

import android.util.Log
import org.publicvalue.multiplatform.oidc.OpenIdConnectClient

class AuthRepository(
    private val remoteSource: RemoteSource = RemoteSource()
) {
    suspend fun registerAppIfNeeded(): Result<OpenIdConnectClient> {
        // TODO: check if app is registered
        val oauthClientResult = remoteSource.registerOauthAPP()
        if (oauthClientResult.isSuccess) {
            val oauthClientData = oauthClientResult.getOrElse {
                return signalRegistrationFailure()
            }
            Log.d("AuthRepository", "ClientId: ${oauthClientData.clientId}")
            val client = OpenIdConnectClient {
                endpoints {
                    tokenEndpoint = "https://neodb.social/oauth/token"
                    authorizationEndpoint = "https://neodb.social/oauth/authorize/"
                    userInfoEndpoint = null
                    endSessionEndpoint = null
                }
                clientId = oauthClientData.clientId
                clientSecret = oauthClientData.clientSecret
                redirectUri = "day.vitayuzu.neodb://auth"
            }
            return Result.success(client)
        } else {
            return signalRegistrationFailure()
        }
    }

    companion object {

        private val AuthRegisterException: Exception = Exception("Failed to register app")

        private fun <T> signalRegistrationFailure(): Result<T> {
            Log.e("AuthRepository", "Failed to register app")
            return Result.failure(AuthRegisterException)
        }
    }

}