package day.vitayuzu.neodb.data

import android.util.Log
import day.vitayuzu.neodb.data.schema.HasPages
import day.vitayuzu.neodb.data.schema.MarkInSchema
import day.vitayuzu.neodb.data.schema.ResultSchema
import day.vitayuzu.neodb.data.schema.TrendingItemSchema
import day.vitayuzu.neodb.util.EntryType
import day.vitayuzu.neodb.util.ShelfType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class NeoDBRepository @Inject constructor(private val remoteSource: RemoteSource) {

    // Concurrently fetch all shelves
    @OptIn(ExperimentalCoroutinesApi::class)
    val userShelf = flowOf(*ShelfType.entries.toTypedArray())
        .flatMapMerge {
            fetchMyShelfByShelfType(it)
        }

    // Concurrently fetch all trending, return a map of type to trending
    @OptIn(ExperimentalCoroutinesApi::class)
    val serverTrending = flowOf(*EntryType.entries.take(6).toTypedArray())
        .flatMapMerge { type ->
            fetchTrendingByEntryType(type).map {
                mapOf(type to it)
            }
        }

    private fun fetchTrendingByEntryType(type: EntryType): Flow<List<TrendingItemSchema>> = flow {
        emit(remoteSource.fetchTrending(type))
    }.log(type.toString())

    private fun fetchMyShelfByShelfType(shelfType: ShelfType) = pagedRequest { page ->
        remoteSource.fetchMyShelf(shelfType, page)
    }.log(shelfType.toString())

    fun fetchDetail(type: EntryType, uuid: String) = flow {
        emit(remoteSource.fetchDetail(type, uuid))
    }.log("$type $uuid")

    fun fetchItemPosts(uuid: String) = pagedRequest { page ->
        remoteSource.fetchItemPosts(uuid, "comment", page)
    }.log("comment in $uuid")

    private fun <T : HasPages> pagedRequest(request: suspend (Int) -> T) = flow {
        val initialResponse = request(1)
        val totalPages = initialResponse.pages
        emit(initialResponse)
        for (page in 2..totalPages) {
            emit(request(page))
        }
    }

    fun fetchItemUserMark(uuid: String) = flow {
        emit(remoteSource.fetchItemUserMark(uuid))
    }.log("fetch user mark in $uuid")

    fun postMark(uuid: String, data: MarkInSchema) = flow {
        emit(remoteSource.postMark(uuid, data))
    }.validate().log("post mark in $uuid")

    fun searchWithKeyword(keywords: String) = pagedRequest {
        remoteSource.searchWithKeywords(keywords, null, it)
    }.log("search with keyword $keywords")
}

/**
 * Helper function to log the request for a flow.
 */
fun <T> Flow<T>.log(msg: String, tag: String = "Repository") = this
    .onStart {
        Log.d(tag, "Start fetching $msg")
    }.onCompletion {
        Log.d(tag, "End fetching $msg")
    }.catch {
        Log.e(tag, "Error fetching $msg: $it")
    }

fun Flow<ResultSchema>.validate() = this.filter { it.message == "OK" }
