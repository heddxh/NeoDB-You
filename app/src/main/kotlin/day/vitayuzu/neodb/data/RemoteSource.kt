package day.vitayuzu.neodb.data

import day.vitayuzu.neodb.data.schema.PagedMarkSchema
import day.vitayuzu.neodb.util.ShelfType
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
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

class RemoteSource(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) {
    suspend fun fetchMyShelf(
        type: ShelfType,
        page: Int = 1
    ): PagedMarkSchema =
        withContext(dispatcher) {
            api.fetchMyShelf(
                type,
                page
            )
        }

    // NOTE: May should be a separate object if we have multiple APIs(sources)
    private companion object {
        val client = ktorfit {
            println("Initializing Ktorfit client...")
            baseUrl("https://neodb.social/api/")
            httpClient(HttpClient {
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true })
                }
                install(Logging) {
                    logger = Logger.ANDROID
                    level = LogLevel.NONE
                }
            })
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
        "Authorization: Bearer PKFmBt5hcPdjia2zQQoJwUZkAlKWbvUAlipTRvfLaMMXu6a96p1qAxHyDw",
        "Content-Type: application/json"
    )
    suspend fun fetchMyShelf(
        @Path("type") type: ShelfType,
        @Query("page") page: Int = 1
    ): PagedMarkSchema
}