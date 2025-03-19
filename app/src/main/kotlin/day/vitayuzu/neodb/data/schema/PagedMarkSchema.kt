package day.vitayuzu.neodb.data.schema

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PagedMarkSchema(
    val pages: Int,
    val data: List<MarkScheme>?,
    val count: Int
)

@Serializable
data class MarkScheme(
    @SerialName("created_time") val createdTime: String,
    @SerialName("comment_text") val commentText: String?,
    @SerialName("item") val entrySchema: EntrySchema,
    val visibility: Int,
    @SerialName("post_id") val postId: Long?,
    @SerialName("rating_grade") val ratingGrade: Int?,
    @SerialName("shelf_type") val shelfType: String,
    val tags: List<String>?
) {
    init {
        require(visibility in 0..2) { "visibility must be 0, 1, or 2" }
    }
}

@Serializable
data class EntrySchema(
    @SerialName("api_url") val apiUrl: String,
    @SerialName("cover_image_url") val coverImageUrl: String?,
    @SerialName("display_title") val displayTitle: String,
    val rating: Float?,
    val description: String,
    val type: String,
    val title: String,
    val uuid: String,
    val url: String,
    @SerialName("rating_count") val ratingCount: Int?,
    val tags: List<String>?,
    @SerialName("external_resources") val externalResources: List<ExternalResourcesItem>?,
    @SerialName("parent_uuid") val parentUuid: String?,
    @SerialName("rating_distribution") val ratingDistribution: List<Int>?,
    val id: String,
    val category: String
)

@Serializable
data class ExternalResourcesItem(val url: String)






