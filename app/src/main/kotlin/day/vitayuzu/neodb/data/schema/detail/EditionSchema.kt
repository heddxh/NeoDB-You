package day.vitayuzu.neodb.data.schema.detail

import day.vitayuzu.neodb.util.EntryType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Edition")
data class EditionSchema(
    // Common fields
    override val id: String,
    override val type: String,
    override val uuid: String,
    override val url: String,
    @SerialName("api_url") override val apiUrl: String,
    override val category: EntryType,
    @SerialName("parent_uuid") override val parentUuid: String?,
    @SerialName("display_title") override val displayTitle: String,
    @SerialName("external_resources") override val externalResources: List<ExternalResource>?,
    override val title: String,
    override val description: String?,
    @SerialName("localized_title") override val localizedTitle: List<LocalizedData>?,
    @SerialName("localized_description") override val localizedDescription: List<LocalizedData>?,
    @SerialName("cover_image_url") override val coverImageUrl: String?,
    override val rating: Float?,
    @SerialName("rating_count") override val ratingCount: Int?,
    @SerialName("rating_distribution") override val ratingDistribution: List<Int>?,
    override val tags: List<String>?,
    // Specific fields
    val subtitle: String?,
    @SerialName("orig_title") val origTitle: String?,
    val author: List<String> = emptyList(),
    val translator: List<String> = emptyList(),
    val language: List<String> = emptyList(),
    @SerialName("pub_house") val pubHouse: String?,
    @SerialName("pub_year") val pubYear: Int?,
    @SerialName("pub_month") val pubMonth: Int?,
    val binding: String?,
    val price: String?,
    val pages: String?,
    val series: String?,
    val imprint: String?,
    val isbn: String?,
) : DetailSchema
