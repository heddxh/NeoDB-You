package day.vitayuzu.neodb.data

import day.vitayuzu.neodb.data.schema.AuthClientIdentify
import day.vitayuzu.neodb.data.schema.GithubLatestReleaseSchema
import day.vitayuzu.neodb.data.schema.InstanceSchema
import day.vitayuzu.neodb.data.schema.MarkInSchema
import day.vitayuzu.neodb.data.schema.MarkSchema
import day.vitayuzu.neodb.data.schema.PagedMarkSchema
import day.vitayuzu.neodb.data.schema.PaginatedPostList
import day.vitayuzu.neodb.data.schema.ResultSchema
import day.vitayuzu.neodb.data.schema.SearchResult
import day.vitayuzu.neodb.data.schema.TokenSchema
import day.vitayuzu.neodb.data.schema.TrendingItemSchema
import day.vitayuzu.neodb.data.schema.UserSchema
import day.vitayuzu.neodb.data.schema.detail.DetailSchema
import day.vitayuzu.neodb.util.APP_NAME
import day.vitayuzu.neodb.util.AUTH_CALLBACK
import day.vitayuzu.neodb.util.EntryType
import day.vitayuzu.neodb.util.IoDispatcher
import day.vitayuzu.neodb.util.ShelfType
import day.vitayuzu.neodb.util.WEBSITE
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.ReqBuilder
import de.jensklingenberg.ktorfit.http.Url
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.set
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteSource @Inject constructor(
    private val api: NeoDbApi,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) {
    // =====================================< Auth Start >================================

    /**
     * Fetch instance info from given url, ignore current instance address.
     * @param instanceUrl Normalized instance url, without scheme and trailing slash.
     */
    suspend fun fetchInstanceInfo(instanceUrl: String): InstanceSchema = withContext(dispatcher) {
        api.fetchInstanceInfo(instanceUrl) {
            url.set("https", instanceUrl, path = "api/v1/instance")
        }
    }

    suspend fun registerOauthAPP(instanceUrl: String): AuthClientIdentify =
        withContext(dispatcher) {
            api.registerOauthAPP {
                url.set("https", instanceUrl, path = "api/v1/apps")
            }
        }

    suspend fun exchangeAccessToken(
        instanceUrl: String,
        clientId: String,
        clientSecret: String,
        code: String,
    ) = withContext(dispatcher) {
        api.exchangeAccessToken(clientId, clientSecret, code) {
            url.set("https", instanceUrl, path = "oauth/token")
        }
    }

    suspend fun revokeAccessToken(
        instanceUrl: String,
        clientId: String,
        clientSecret: String,
        token: String,
    ) = withContext(dispatcher) {
        api.revokeAccessToken(clientId, clientSecret, token) {
            url.set("https", instanceUrl, path = "oauth/revoke")
        }
    }

    // ===========================================< Auth END >============================

    suspend fun fetchMyShelf(type: ShelfType, page: Int = 1): PagedMarkSchema =
        withContext(dispatcher) {
            api.fetchMyShelf(type, page)
        }

    suspend fun fetchTrending(type: EntryType): List<TrendingItemSchema> = withContext(dispatcher) {
        api.fetchTrending(type)
    }

    suspend fun fetchDetail(type: EntryType, uuid: String) = withContext(dispatcher) {
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

    suspend fun fetchSelfAccountInfo(): UserSchema = withContext(dispatcher) {
        api.fetchSelfAccountInfo()
    }

    suspend fun fetchItemUserMark(uuid: String): MarkSchema = withContext(dispatcher) {
        api.fetchItemUserMark(uuid)
    }

    suspend fun postMark(uuid: String, data: MarkInSchema): ResultSchema = withContext(dispatcher) {
        api.postMark(uuid, data)
    }

    suspend fun searchWithKeywords(
        keywords: String,
        category: EntryType?,
        page: Int,
    ): SearchResult = withContext(dispatcher) {
        api.searchWithKeywords(keywords, category, page)
    }

    suspend fun getLatestVersionFromGithub() = withContext(dispatcher) {
        api.getLatestVersionFromGithub()
    }
}

/**
 * Network Api for NeoDB, powered by [Ktorfit](https://github.com/Foso/Ktorfit).
 *
 * It is the main api interface for the app.
 * Note: should not be private since Ktorfit need to generate the implementation(extend it).
 * TODO: add trailing slash for all endpoints.
 */
interface NeoDbApi {
    // =====================================< Auth Start >================================
    @GET
    suspend fun fetchInstanceInfo(
        @Url instanceUrl: String,
        @ReqBuilder ext: HttpRequestBuilder.() -> Unit,
    ): InstanceSchema

    @POST("v1/apps")
    @FormUrlEncoded
    suspend fun registerOauthAPP(
        @Field("client_name") clientName: String = APP_NAME,
        @Field("redirect_uris") redirectUris: String = AUTH_CALLBACK,
        @Field("website") website: String = WEBSITE,
        @ReqBuilder ext: HttpRequestBuilder.() -> Unit,
    ): AuthClientIdentify

    @POST("oauth/token")
    @FormUrlEncoded
    suspend fun exchangeAccessToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String = AUTH_CALLBACK,
        @Field("grant_type") grantType: String = "authorization_code",
        @ReqBuilder ext: HttpRequestBuilder.() -> Unit = {},
    ): TokenSchema

    @POST("oauth/revoke")
    @FormUrlEncoded
    suspend fun revokeAccessToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("token") token: String,
        @ReqBuilder ext: HttpRequestBuilder.() -> Unit = {},
    )

    // ===========================================< Auth END >============================

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

    @GET("me/shelf/item/{uuid}")
    suspend fun fetchItemUserMark(
        @Path("uuid") uuid: String,
    ): MarkSchema

    @POST("me/shelf/item/{uuid}")
    suspend fun postMark(
        @Path("uuid") uuid: String,
        @Body data: MarkInSchema,
    ): ResultSchema

    @GET("catalog/search")
    suspend fun searchWithKeywords(
        @Query("query") keywords: String,
        @Query("category") category: EntryType?,
        @Query("page") page: Int,
    ): SearchResult

    @GET("https://api.github.com/repos/heddxh/NeoDB-You/releases/latest")
    @Headers("Accept: application/vnd.github+json", "X-GitHub-Api-Version: 2022-11-28")
    suspend fun getLatestVersionFromGithub(): GithubLatestReleaseSchema
}
