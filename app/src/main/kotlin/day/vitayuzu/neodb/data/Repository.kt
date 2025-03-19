package day.vitayuzu.neodb.data

import android.util.Log
import day.vitayuzu.neodb.data.schema.PagedMarkSchema
import day.vitayuzu.neodb.util.ShelfType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class Repository(private val remoteSource: RemoteSource = RemoteSource()) {

    fun fetchMyAllShelf(): Flow<PagedMarkSchema> {
        return flow {
            for (type in ShelfType.entries) {
                emitAll(fetchMyShelfByShelfType(type))
            }
        }
    }

    private fun fetchMyShelfByShelfType(type: ShelfType): Flow<PagedMarkSchema> {
        return flow {
            println("Start fetching $type")
            var page = 1
            val res = remoteSource.fetchMyShelf(type)
            val maxPage = res.pages
            while (page <= maxPage) {
                emit(
                    remoteSource.fetchMyShelf(
                        type,
                        page
                    )
                )
                page++
            }
            println("End fetching $type")
        }.catch {
            Log.d(
                "Repository",
                "fetchMyShelf: $it"
            )
        }
    }

}
