package day.vitayuzu.neodb.data

import day.vitayuzu.neodb.data.schema.AuthClientIdentify
import day.vitayuzu.neodb.data.schema.PagedMarkSchema
import day.vitayuzu.neodb.util.APP_NAME
import day.vitayuzu.neodb.util.AUTH_CALLBACK
import day.vitayuzu.neodb.util.IoDispatcher
import day.vitayuzu.neodb.util.ShelfType
import day.vitayuzu.neodb.util.WEBSITE
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteSource @Inject constructor(
    private val api: NeoDbApi,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) {
    suspend fun fetchMyShelf(
        type: ShelfType,
        page: Int = 1,
    ): PagedMarkSchema = withContext(dispatcher) {
        api.fetchMyShelf(type, page)
    }

    suspend fun registerOauthAPP(): Result<AuthClientIdentify> = withContext(dispatcher) {
        try {
            val result = api.registerOauthAPP()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
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
    @Headers("Content-Type: application/json")
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
