package day.vitayuzu.neodb.data.schema

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PagedMarkSchema(
    val data: List<MarkSchema>?,
    val count: Int,
    override val pages: Int,
) : HasPages

@Serializable
data class MarkSchema(
    @SerialName("created_time") val createdTime: String,
    @SerialName("comment_text") val commentText: String?,
    @SerialName("item") val entrySchema: EntrySchema,
    val visibility: Int,
    @SerialName("post_id") val postId: Long?,
    @SerialName("rating_grade") val ratingGrade: Int?,
    @SerialName("shelf_type") val shelfType: String,
    val tags: List<String>?,
) {
    init {
        require(visibility in 0..2) { "visibility must be 0, 1, or 2" }
    }
}
