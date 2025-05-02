package day.vitayuzu.neodb.data.schema.detail

import day.vitayuzu.neodb.util.EntryType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

/**
 * Base class with common fields for all kinds of detail schema like [EditionSchema]
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("category")
sealed interface DetailSchema {
    val id: String
    val type: String
    val uuid: String
    val url: String
    val category: EntryType
    val title: String
    val description: String?
    val rating: Float?
    val tags: List<String>?

    @SerialName("api_url") val apiUrl: String

    @SerialName("parent_uuid") val parentUuid: String?

    @SerialName("display_title") val displayTitle: String

    @SerialName("external_resources") val externalResources: List<ExternalResource>?

    @SerialName("localized_title") val localizedTitle: List<LocalizedData>?

    @SerialName("localized_description") val localizedDescription: List<LocalizedData>?

    @SerialName("cover_image_url") val coverImageUrl: String?

    @SerialName("rating_count") val ratingCount: Int?

    @SerialName("rating_distribution") val ratingDistribution: List<Int>?
}

@Serializable
data class LocalizedData(
    val lang: String = "",
    val text: String = "",
)

@Serializable
data class ExternalResource(val url: String)
