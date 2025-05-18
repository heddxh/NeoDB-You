package day.vitayuzu.neodb.ui.model

import day.vitayuzu.neodb.data.schema.CommentRelatedItem
import day.vitayuzu.neodb.data.schema.PostSchema
import day.vitayuzu.neodb.data.schema.RatingRelatedItem
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil

data class Post(
    val avatar: String?,
    val username: String,
    val rating: Int?, // One user rating, range 0-10
    val date: Instant,
    val content: String,
) {
    constructor(schema: PostSchema) : this(
        avatar = schema.account.avatar,
        username = schema.account.displayName,
        rating = schema.extNeoDB.relatedWith.find { it is RatingRelatedItem }?.let {
            (it as RatingRelatedItem).value
        },
        date = Instant.parse(schema.editedAt ?: schema.createdAt), // TODO: find a way to display edit date
        content = schema.extNeoDB.relatedWith.find { it is CommentRelatedItem }?.let {
            (it as CommentRelatedItem).content
        } ?: "",
    )
}

/**
 * Convert [Instant] to a human-readable string,
 * like: 1d ago, 2m ago, 3y ago, etc.
 * WIP
 */
fun Instant.toReadableString(): String {
    val duration = this.periodUntil(Clock.System.now(), TimeZone.UTC)
    return if (duration.days <= 7) {
        "${duration.days}d ago"
    } else if (duration.months <= 1) {
        "${duration.months}m ago"
    } else if (duration.years <= 1) {
        "${duration.years}y ago"
    } else {
        this.toString()
    }
}
