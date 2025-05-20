package day.vitayuzu.neodb.data.schema

import day.vitayuzu.neodb.util.ShelfType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MarkInSchema(
    @SerialName("shelf_type") val shelfType: ShelfType,
    val visibility: Int, // 0, 1, 2
    @SerialName("comment_text") val commentText: String?,
    @SerialName("rating_grade") val ratingGrade: Int?,
    val tags: List<String>,
    @SerialName("created_time") val createdTime: String?,
    @SerialName("post_to_fediverse") val postToFediverse: Boolean = false,
)
