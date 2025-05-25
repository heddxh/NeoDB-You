package day.vitayuzu.neodb.data.schema

import day.vitayuzu.neodb.data.schema.detail.DetailSchema
import kotlinx.serialization.Serializable

@Serializable
data class SearchResult(
    val data: List<DetailSchema>,
    override val pages: Int,
    val count: Int,
) : HasPages
