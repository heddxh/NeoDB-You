package day.vitayuzu.neodb.data.schema.detail

import day.vitayuzu.neodb.util.EntryType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// TODO: Add performance products
@Serializable
@SerialName("Performance")
data class PerformanceSchema(
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
    @SerialName("orig_title") val origTitle: String?,
    val genre: List<String>,
    val language: List<String>,
    @SerialName("opening_date") val openingDate: String?,
    @SerialName("closing_date") val closingDate: String?,
    val director: List<String>,
    val playwright: List<String>,
    @SerialName("orig_creator") val origCreator: List<String>,
    val composer: List<String>,
    val choreographer: List<String>,
    val performer: List<String>,
    val actor: List<CrewMemberSchema>,
    val crew: List<CrewMemberSchema>,
    @SerialName("official_site") val officialSite: String?,
) : DetailSchema

@Serializable
data class CrewMemberSchema(
    val name: String,
    val role: String?,
)
