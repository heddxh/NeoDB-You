package day.vitayuzu.neodb.ui.model

import day.vitayuzu.neodb.data.schema.MarkSchema
import day.vitayuzu.neodb.util.ShelfType
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Mark represents a user mark for an entry.
 * @param shelfType One of [ShelfType]
 * @param date Mark date
 * @param rating Rating scores in 10 point system.
 * @param comment User comment
 */
data class Mark(
    val entry: Entry,
    val shelfType: ShelfType,
    val date: LocalDate,
    val rating: Int?,
    val comment: String?,
) {
    @OptIn(ExperimentalTime::class) constructor(scheme: MarkSchema) : this(
        entry = Entry(scheme.entrySchema),
        shelfType = ShelfType.valueOf(scheme.shelfType),
        date =
            Instant
                .parse(scheme.createdTime)
                .toLocalDateTime(timeZone = TimeZone.currentSystemDefault())
                .date,
        rating = scheme.ratingGrade,
        comment = scheme.commentText,
    )

    val fullStars = rating?.div(2) ?: 0
    val hasHalfStar = rating?.rem(2) == 1

    @Suppress("ktlint:standard:max-line-length")
    companion object {
        val TEST = Mark(
            entry = Entry.TEST,
            shelfType = ShelfType.complete,
            date = LocalDate(2023, 1, 1),
            rating = 7,
            comment =
                "This is a test comment.\nThis is a test comment\nThis is a test comment\nThis is a test comment",
        )
    }
}
