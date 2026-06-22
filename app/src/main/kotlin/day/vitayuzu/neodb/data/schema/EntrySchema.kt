package day.vitayuzu.neodb.data.schema

import day.vitayuzu.neodb.data.schema.detail.ExternalResource
import day.vitayuzu.neodb.data.schema.detail.LocalizedData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EntrySchema(
    @SerialName("api_url") val apiUrl: String = "",
    val brief: String = "",
    val category: String = "",
    @SerialName("cover_image_url") val coverImageUrl: String?,
    val description: String = "",
    @SerialName("external_resources") val externalResources: List<ExternalResource> = listOf(),
    val id: String = "",
    @SerialName("localized_description") val localizedDescription: List<LocalizedData> =
        listOf(),
    @SerialName("localized_title") val localizedTitle: List<LocalizedData> = listOf(),
    @SerialName("parent_uuid") val parentUuid: String? = null,
    val rating: Float? = null,
    @SerialName("rating_count") val ratingCount: Int? = null,
    @SerialName("rating_distribution") val ratingDistribution: List<Int> = listOf(),
    val tags: List<String>? = listOf(),
    val title: String = "",
    val type: String = "",
    val url: String = "",
    val uuid: String = "",
)
