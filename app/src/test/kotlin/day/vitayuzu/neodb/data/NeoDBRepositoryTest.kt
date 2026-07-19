package day.vitayuzu.neodb.data

import day.vitayuzu.neodb.data.schema.AuthClientIdentify
import day.vitayuzu.neodb.data.schema.EntrySchema
import day.vitayuzu.neodb.data.schema.GithubLatestReleaseSchema
import day.vitayuzu.neodb.data.schema.InstanceSchema
import day.vitayuzu.neodb.data.schema.MarkInSchema
import day.vitayuzu.neodb.data.schema.MarkSchema
import day.vitayuzu.neodb.data.schema.PagedMarkSchema
import day.vitayuzu.neodb.data.schema.PaginatedPostList
import day.vitayuzu.neodb.data.schema.PublicInstanceSchema
import day.vitayuzu.neodb.data.schema.ResultSchema
import day.vitayuzu.neodb.data.schema.SearchResult
import day.vitayuzu.neodb.data.schema.TokenSchema
import day.vitayuzu.neodb.data.schema.UserPreferenceSchema
import day.vitayuzu.neodb.data.schema.UserSchema
import day.vitayuzu.neodb.data.schema.detail.DetailSchema
import day.vitayuzu.neodb.util.EntryType
import day.vitayuzu.neodb.util.ShelfType
import io.ktor.client.request.HttpRequestBuilder
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class NeoDBRepositoryTest {

    private fun TestScope.repositoryWith(api: NeoDbApi) =
        NeoDBRepository(RemoteSource(api, StandardTestDispatcher(testScheduler)))

    @Test
    fun `pagedRequest fetches every page in order`() = runTest {
        val requestedPages = mutableListOf<Int>()
        val api = object : FakeNeoDbApi() {
            override suspend fun searchWithKeywords(
                keywords: String,
                category: EntryType?,
                page: Int,
            ): SearchResult {
                requestedPages += page
                return SearchResult(data = emptyList(), pages = 3, count = 0)
            }
        }

        val emissions = repositoryWith(api).searchWithKeyword("nausicaa").toList()

        assertEquals(listOf(1, 2, 3), requestedPages)
        assertEquals(3, emissions.size)
    }

    @Test
    fun `pagedRequest stops after a single page`() = runTest {
        val requestedPages = mutableListOf<Int>()
        val api = object : FakeNeoDbApi() {
            override suspend fun searchWithKeywords(
                keywords: String,
                category: EntryType?,
                page: Int,
            ): SearchResult {
                requestedPages += page
                return SearchResult(data = emptyList(), pages = 1, count = 0)
            }
        }

        repositoryWith(api).searchWithKeyword("totoro").toList()

        assertEquals(listOf(1), requestedPages)
    }

    @Test
    fun `userShelf fetches all shelf types and all their pages`() = runTest {
        val requested = mutableListOf<Pair<ShelfType, Int>>()
        val api = object : FakeNeoDbApi() {
            override suspend fun fetchMyShelf(type: ShelfType, page: Int): PagedMarkSchema {
                requested += type to page
                val pages = if (type == ShelfType.wishlist) 2 else 1
                return PagedMarkSchema(data = null, count = 0, pages = pages)
            }
        }

        val emissions = repositoryWith(api).userShelf.toList()

        // 4 shelf types, wishlist has an extra page.
        assertEquals(5, emissions.size)
        val expected = ShelfType.entries.map { it to 1 }.toSet() + (ShelfType.wishlist to 2)
        assertEquals(expected, requested.toSet())
    }

    @Test
    fun `postMark emits the OK result`() = runTest {
        val api = object : FakeNeoDbApi() {
            override suspend fun postMark(uuid: String, data: MarkInSchema) = ResultSchema("OK")
        }

        val emissions = repositoryWith(api).postMark("uuid", MARK_IN).toList()

        assertEquals(listOf(ResultSchema("OK")), emissions)
    }

    @Test
    fun `postMark completes without emission when server reports failure`() = runTest {
        val api = object : FakeNeoDbApi() {
            override suspend fun postMark(uuid: String, data: MarkInSchema) =
                ResultSchema("Something went wrong")
        }

        // validate() raises and log() records the failure; nothing is emitted downstream.
        val emissions = repositoryWith(api).postMark("uuid", MARK_IN).toList()

        assertEquals(emptyList(), emissions)
    }

    private companion object {
        val MARK_IN = MarkInSchema(
            shelfType = ShelfType.complete,
            visibility = 0,
            commentText = null,
            ratingGrade = null,
            tags = emptyList(),
            createdTime = null,
        )
    }
}

/**
 * Base fake for [NeoDbApi]: every endpoint fails loudly, tests override what they need.
 */
private open class FakeNeoDbApi : NeoDbApi {

    override suspend fun fetchInstanceInfo(
        instanceUrl: String,
        ext: HttpRequestBuilder.() -> Unit,
    ): InstanceSchema = unused()

    override suspend fun registerOauthAPP(
        clientName: String,
        redirectUris: String,
        website: String,
        ext: HttpRequestBuilder.() -> Unit,
    ): AuthClientIdentify = unused()

    override suspend fun exchangeAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
        redirectUri: String,
        grantType: String,
        ext: HttpRequestBuilder.() -> Unit,
    ): TokenSchema = unused()

    override suspend fun revokeAccessToken(
        clientId: String,
        clientSecret: String,
        token: String,
        ext: HttpRequestBuilder.() -> Unit,
    ): Unit = unused()

    override suspend fun fetchMyShelf(type: ShelfType, page: Int): PagedMarkSchema = unused()

    override suspend fun fetchTrending(type: EntryType): List<EntrySchema> = unused()

    override suspend fun fetchDetail(type: String, uuid: String): DetailSchema = unused()

    override suspend fun fetchItemPosts(
        uuid: String,
        type: String,
        page: Int,
    ): PaginatedPostList = unused()

    override suspend fun fetchSelfAccountInfo(ext: HttpRequestBuilder.() -> Unit): UserSchema =
        unused()

    override suspend fun fetchSelfPreference(): UserPreferenceSchema = unused()

    override suspend fun fetchItemUserMark(uuid: String): MarkSchema = unused()

    override suspend fun postMark(uuid: String, data: MarkInSchema): ResultSchema = unused()

    override suspend fun deleteMark(uuid: String): ResultSchema = unused()

    override suspend fun searchWithKeywords(
        keywords: String,
        category: EntryType?,
        page: Int,
    ): SearchResult = unused()

    override suspend fun getLatestVersionFromGithub(): GithubLatestReleaseSchema = unused()

    override suspend fun fetchPublicInstances(): List<PublicInstanceSchema> = unused()

    private fun unused(): Nothing =
        throw UnsupportedOperationException("Endpoint not stubbed for this test")
}
