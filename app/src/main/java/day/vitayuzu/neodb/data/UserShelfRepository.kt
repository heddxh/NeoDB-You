package day.vitayuzu.neodb.data

import day.vitayuzu.neodb.data.model.ShelfType
import day.vitayuzu.neodb.data.schema.PagedMarkSchema

class UserShelfRepository(
    private val remoteSource: UserShelfRemoteSource
) {
    suspend fun fetchMyShelf(type: ShelfType): PagedMarkSchema = remoteSource.fetchMyShelf(type)
}