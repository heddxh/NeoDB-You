package day.vitayuzu.neodb.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import day.vitayuzu.neodb.data.schema.AuthClientIdentify
import day.vitayuzu.neodb.util.AUTH_CALLBACK
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.publicvalue.multiplatform.oidc.OpenIdConnectClient

class AuthRepository(
    private val remoteSource: RemoteSource = RemoteSource(),
    private val dataStore: DataStore<Preferences>
) {
    suspend fun registerAppIfNeeded(): Result<OpenIdConnectClient> {

        val clientId = getAuthClientId()
        val clientSecret = getAuthClientSecret()

        if (clientId != null && clientSecret != null) {
            Log.d("AuthRepository", "Found saved auth client")
            return Result.success(constructAuthClient(clientId, clientSecret))
        } else {

            Log.d("AuthRepository", "No saved auth client found, registering...")
            val oauthClientResult = remoteSource.registerOauthAPP()

            if (oauthClientResult.isSuccess) {
                val oauthClientData = oauthClientResult.getOrElse {
                    return signalRegistrationFailure()
                }
                try {
                    saveAuthClientIdentify(oauthClientData)
                    Log.d("AuthRepository", "Saved auth client")
                    return Result.success(
                        constructAuthClient(
                            oauthClientData.clientId,
                            oauthClientData.clientSecret
                        )
                    )
                } catch (e: Exception) {
                    return signalRegistrationFailure()
                }
            } else {
                return signalRegistrationFailure()
            }
        }
    }

    private suspend fun saveAuthClientIdentify(identify: AuthClientIdentify) {
        dataStore.edit {
            it[CLIENT_ID] = identify.clientId
            it[CLIENT_SECRET] = identify.clientSecret
        }
    }

    private suspend fun getAuthClientId(): String? = dataStore.data
        .map { it[CLIENT_ID] }
        .catch {
            Log.e("AuthRepository", "Error while reading preferences", it)
            emit(null)
        }
        .firstOrNull()

    private suspend fun getAuthClientSecret(): String? = dataStore.data
        .map { it[CLIENT_SECRET] }
        .catch {
            Log.e("AuthRepository", "Error while reading preferences", it)
            emit(null)
        }
        .firstOrNull()

    private companion object {
        val AuthRegisterException: Exception = Exception("Failed to register app")
        fun <T> signalRegistrationFailure(): Result<T> {
            Log.e("AuthRepository", "Failed to register app")
            return Result.failure(AuthRegisterException)
        }

        val CLIENT_ID = stringPreferencesKey("client_id")
        val CLIENT_SECRET = stringPreferencesKey("client_secret")

        fun constructAuthClient(
            clientId: String,
            clientSecret: String
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