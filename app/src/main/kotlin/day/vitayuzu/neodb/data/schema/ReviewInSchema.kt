package day.vitayuzu.neodb.data.schema

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewInSchema(
    val visibility: Int, // 0, 1, 2
    @SerialName("created_time") val createdTime: String?,
    val title: String,
    val body: String, // Markdown
    @SerialName("post_to_fediverse") val postToFediverse: Boolean = false,
)
