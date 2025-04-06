package day.vitayuzu.neodb.data

import android.util.Log
import day.vitayuzu.neodb.data.schema.PagedMarkSchema
import day.vitayuzu.neodb.util.ShelfType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

class Repository(
    private val remoteSource: RemoteSource = RemoteSource(),
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun fetchMyAllShelf(): Flow<PagedMarkSchema> =
        flowOf(*ShelfType.entries.toTypedArray()).flatMapMerge {
            fetchMyShelfByShelfType(it)
        }

    private fun fetchMyShelfByShelfType(shelfType: ShelfType): Flow<PagedMarkSchema> =
        flow {
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
}
