package day.vitayuzu.neodb.data

import android.util.Log
import day.vitayuzu.neodb.data.schema.AuthClientIdentify
import day.vitayuzu.neodb.data.schema.PagedMarkSchema
import day.vitayuzu.neodb.util.APP_NAME
import day.vitayuzu.neodb.util.AUTH_CALLBACK
import day.vitayuzu.neodb.util.BASEURL
import day.vitayuzu.neodb.util.ShelfType
import day.vitayuzu.neodb.util.WEBSITE
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class RemoteSource(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend fun fetchMyShelf(
        type: ShelfType,
        page: Int = 1,
    ): PagedMarkSchema =
        withContext(dispatcher) {
            api.fetchMyShelf(type, page)
        }

    suspend fun registerOauthAPP(): Result<AuthClientIdentify> =
        withContext(dispatcher) {
            try {
                val result = api.registerOauthAPP()
                Result.success(result)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // NOTE: May should be a separate object if we have multiple APIs(sources)
    companion object {
        private val client =
            ktorfit {
                Log.d("RemoteSource", "Initializing Ktorfit client...")
                baseUrl(BASEURL)
                httpClient(
                    HttpClient {
                        install(ContentNegotiation) {
                            json(Json { ignoreUnknownKeys = true })
                        }
                        install(Logging) {
                            logger = Logger.ANDROID
                            level = LogLevel.NONE
                        }
                    },
                )
            }
        val api = client.createNeoDbApi()
    }
}

/**
 * Network Api for NeoDB, powered by [Ktorfit](https://github.com/Foso/Ktorfit).
 *
 * It is the main api interface for the app.
 * Note: should not be private since Ktorfit need to generate the implementation(extend it).
 */
interface NeoDbApi {
    @GET("me/shelf/{type}")
    @Headers(
        "Content-Type: application/json",
    )
    suspend fun fetchMyShelf(
        @Path("type") type: ShelfType,
        @Query("page") page: Int = 1,
    ): PagedMarkSchema

    @POST("v1/apps")
    @FormUrlEncoded
    suspend fun registerOauthAPP(
        @Field("client_name") clientName: String = APP_NAME,
        @Field("redirect_uris") redirectUris: String = AUTH_CALLBACK,
        @Field("website") website: String = WEBSITE,
    ): AuthClientIdentify
}
