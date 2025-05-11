package day.vitayuzu.neodb.data.schema

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
data class ExtNeoDB(
    val tag: List<ExtNeoDBTag>?,
    val relatedWith: List<BaseRelatedWithItem>,
)

enum class ExtNeoDBType {
    Status,
    Comment,
    Rating,
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
sealed interface BaseRelatedWithItem {
    val id: String
    val href: String
    val type: ExtNeoDBType
    val updated: String
    val published: String
    val attributedTo: String
    val withRegardTo: String
}

@Serializable
@SerialName("Status")
data class StatusRelatedItem(
    override val type: ExtNeoDBType,
    override val id: String,
    override val href: String,
    override val updated: String,
    override val published: String,
    override val attributedTo: String,
    override val withRegardTo: String,
    val status: String,
) : BaseRelatedWithItem

@Serializable
@SerialName("Comment")
data class CommentRelatedItem(
    override val type: ExtNeoDBType,
    override val id: String,
    override val href: String,
    override val updated: String,
    override val published: String,
    override val attributedTo: String,
    override val withRegardTo: String,
    val content: String,
) : BaseRelatedWithItem

@Serializable
@SerialName("Rating")
data class RatingRelatedItem(
    override val type: ExtNeoDBType,
    override val id: String,
    override val href: String,
    override val updated: String,
    override val published: String,
    override val attributedTo: String,
    override val withRegardTo: String,
    val best: Int,
    val value: Int,
    val worst: Int,
) : BaseRelatedWithItem

@Serializable
data class ExtNeoDBTag(
    val href: String,
    val name: String,
    @SerialName("type") val itemType: String,
    val image: String? = null,
)
