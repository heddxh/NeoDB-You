package day.vitayuzu.neodb.data.model

import day.vitayuzu.neodb.data.schema.MarkScheme
import kotlinx.datetime.LocalDate

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
    val comment: String?
) {
    constructor(scheme: MarkScheme) : this(
        entry = Entry(scheme.entrySchema),
        shelfType = ShelfType.valueOf(scheme.shelfType.replaceFirstChar { it.uppercase() }),
        date = LocalDate.parse(scheme.createdTime),
        rating = scheme.ratingGrade,
        comment = scheme.commentText
    )

    val fullStars = rating?.div(2) ?: 0
    val hasHalfStar = rating?.rem(2) == 1
}
