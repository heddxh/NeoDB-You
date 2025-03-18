package day.vitayuzu.neodb.data

import day.vitayuzu.neodb.data.model.ShelfType
import day.vitayuzu.neodb.data.schema.PagedMarkSchema
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UserShelfRemoteSource (
    private val api: NeoDbApi,
    private val dispatcher: CoroutineDispatcher
) {
    suspend fun fetchMyShelf(type: ShelfType): PagedMarkSchema = withContext(dispatcher) {
        api.fetchMyShelf(type)
    }
}
