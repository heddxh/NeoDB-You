package day.vitayuzu.neodb.ui.model

import day.vitayuzu.neodb.data.schema.EntrySchema
import day.vitayuzu.neodb.util.EntryType

/**
 * Entry represents a book/movie/tv etc, used for a card.
 * Should be a subset of [EntrySchema]
 * @param title The name
 * @param category One of [EntryType]
 * @param url NeoDB url
 * @param des Description
 * @param coverUrl Cover image url
 * @param rating Displayed ratings
 */
data class Entry(
    val title: String,
    val category: EntryType,
    val url: String,
    val des: String,
    val coverUrl: String?,
    val rating: Float?,
) {
    constructor(schema: EntrySchema) : this(
        title = schema.displayTitle,
        category =
            try {
                EntryType.valueOf(schema.category.replaceFirstChar { it.uppercase() })
            } catch (_: IllegalArgumentException) {
                EntryType.Default
            },
        url = schema.url,
        des = schema.description,
        coverUrl = schema.coverImageUrl,
        rating = schema.rating,
    )
}
