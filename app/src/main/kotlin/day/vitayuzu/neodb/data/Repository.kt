package day.vitayuzu.neodb.data

import android.util.Log
import day.vitayuzu.neodb.data.schema.PagedMarkSchema
import day.vitayuzu.neodb.data.schema.TrendingItemSchema
import day.vitayuzu.neodb.util.EntryType
import day.vitayuzu.neodb.util.ShelfType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class Repository @Inject constructor(private val remoteSource: RemoteSource) {

    // Concurrently fetch all shelves
    @OptIn(ExperimentalCoroutinesApi::class)
    fun fetchMyAllShelf(): Flow<PagedMarkSchema> =
        flowOf(*ShelfType.entries.toTypedArray()).flatMapMerge {
            fetchMyShelfByShelfType(it)
        }

    // Concurrently fetch all trending, return a map of type to trending
    @OptIn(ExperimentalCoroutinesApi::class)
    fun fetchTrending(): Flow<Map<EntryType, List<TrendingItemSchema>>> {
        val typeInTrending = listOf(
            EntryType.book,
            EntryType.movie,
            EntryType.tv,
            EntryType.music,
            EntryType.game,
            EntryType.podcast,
        )
        return flowOf(*typeInTrending.toTypedArray()).flatMapMerge { type ->
            fetchTrendingByEntryType(type).map {
                mapOf(type to it)
            }
        }
    }

    private fun fetchTrendingByEntryType(type: EntryType): Flow<List<TrendingItemSchema>> = flow {
        emit(remoteSource.fetchTrending(type))
    }.onStart {
        Log.d("Repository", "Trending: Start fetching $type")
    }.onCompletion {
        Log.d("Repository", "Trending: End fetching $type")
    }.catch {
        Log.e("Repository", "Trending: Error fetching $type: $it")
    }

    private fun fetchMyShelfByShelfType(shelfType: ShelfType): Flow<PagedMarkSchema> = flow {
        val initialResponse = remoteSource.fetchMyShelf(shelfType)
        val totalPages = initialResponse.pages
        emit(initialResponse) // Emit the first page
        for (page in 2..totalPages) {
            emit(remoteSource.fetchMyShelf(shelfType, page))
        }
    }.onStart {
        Log.d("Repository", "Start fetching $shelfType")
    }.onCompletion {
        Log.d("Repository", "End fetching $shelfType")
    }.catch {
        Log.e("Repository", "Error fetching $shelfType: $it")
    }

    fun fetchDetail(
        type: EntryType,
        uuid: String,
    ) = flow {
        emit(remoteSource.fetchDetail(type, uuid))
    }.onStart {
        Log.d("Repository", "Start fetching $type $uuid")
    }.onCompletion {
        Log.d("Repository", "End fetching $type $uuid")
    }.catch {
        Log.e("Repository", "Error fetching $type $uuid: $it")
    }
}
