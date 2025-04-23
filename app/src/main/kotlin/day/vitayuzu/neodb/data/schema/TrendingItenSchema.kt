package day.vitayuzu.neodb.data.schema

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// TODO: merge into EntrySchema
@Serializable
data class TrendingItemSchema(
    @SerialName("api_url") val apiUrl: String = "",
    val brief: String = "",
    val category: String = "",
    @SerialName("cover_image_url") val coverImageUrl: String = "",
    val description: String = "",
    @SerialName("display_title") val displayTitle: String = "",
    @SerialName("external_resources") val externalResources: List<ExternalResource> = listOf(),
    val id: String = "",
    @SerialName("localized_description") val localizedDescription: List<LocalizedDescription> =
        listOf(),
    @SerialName("localized_title") val localizedTitle: List<LocalizedTitle> = listOf(),
    @SerialName("parent_uuid") val parentUuid: String? = null,
    val rating: Float? = null,
    @SerialName("rating_count") val ratingCount: Int? = null,
    @SerialName("rating_distribution") val ratingDistribution: List<Int> = listOf(),
    val tags: List<String> = listOf(),
    val title: String = "",
    val type: String = "",
    val url: String = "",
    val uuid: String = "",
)

@Serializable
data class LocalizedTitle(
    val lang: String = "",
    val text: String = "",
)

@Serializable
data class LocalizedDescription(
    val lang: String = "",
    val text: String = "",
)

@Serializable
data class ExternalResource(val url: String = "")
