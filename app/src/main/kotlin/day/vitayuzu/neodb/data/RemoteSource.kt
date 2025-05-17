package day.vitayuzu.neodb.data

import day.vitayuzu.neodb.data.schema.AuthClientIdentify
import day.vitayuzu.neodb.data.schema.PagedMarkSchema
import day.vitayuzu.neodb.data.schema.PaginatedPostList
import day.vitayuzu.neodb.data.schema.TrendingItemSchema
import day.vitayuzu.neodb.data.schema.UserSchema
import day.vitayuzu.neodb.data.schema.detail.DetailSchema
import day.vitayuzu.neodb.util.APP_NAME
import day.vitayuzu.neodb.util.AUTH_CALLBACK
import day.vitayuzu.neodb.util.EntryType
import day.vitayuzu.neodb.util.IoDispatcher
import day.vitayuzu.neodb.util.ShelfType
import day.vitayuzu.neodb.util.WEBSITE
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.GET
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

    suspend fun fetchTrending(type: EntryType): List<TrendingItemSchema> = withContext(dispatcher) {
        api.fetchTrending(type)
    }

    suspend fun fetchDetail(
        type: EntryType,
        uuid: String,
    ) = withContext(dispatcher) {
        val typeStringEndpoint = if (type == EntryType.music) {
            "album"
        } else {
            type.toString()
        }
        api.fetchDetail(typeStringEndpoint, uuid)
    }

    suspend fun fetchItemPosts(
        uuid: String,
        type: String,
        page: Int,
    ) = withContext(dispatcher) {
        api.fetchItemPosts(uuid, type, page)
    }

    suspend fun registerOauthAPP(): Result<AuthClientIdentify> = withContext(dispatcher) {
        try {
            val result = api.registerOauthAPP()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchSelfAccountInfo(): UserSchema = withContext(dispatcher) {
        api.fetchSelfAccountInfo()
    }
}

/**
 * Network Api for NeoDB, powered by [Ktorfit](https://github.com/Foso/Ktorfit).
 *
 * It is the main api interface for the app.
 * Note: should not be private since Ktorfit need to generate the implementation(extend it).
 */
interface NeoDbApi {
    // Auth
    @POST("v1/apps")
    @FormUrlEncoded
    suspend fun registerOauthAPP(
        @Field("client_name") clientName: String = APP_NAME,
        @Field("redirect_uris") redirectUris: String = AUTH_CALLBACK,
        @Field("website") website: String = WEBSITE,
    ): AuthClientIdentify

    @GET("me/shelf/{type}")
    suspend fun fetchMyShelf(
        @Path("type") type: ShelfType,
        @Query("page") page: Int = 1,
    ): PagedMarkSchema

    @GET("trending/{type}")
    suspend fun fetchTrending(
        @Path("type") type: EntryType,
    ): List<TrendingItemSchema>

    @GET("{type}/{uuid}")
    suspend fun fetchDetail(
        @Path("type") type: String,
        @Path("uuid") uuid: String,
    ): DetailSchema

    @GET("item/{uuid}/posts/")
    suspend fun fetchItemPosts(
        @Path("uuid") uuid: String,
        @Query("type") type: String,
        @Query("page") page: Int,
    ): PaginatedPostList

    @GET("me")
    suspend fun fetchSelfAccountInfo(): UserSchema
}
