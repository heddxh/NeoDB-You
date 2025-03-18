package day.vitayuzu.neodb.data.model

import day.vitayuzu.neodb.data.schema.EntrySchema

/**
 * Entry represents a book/movie/tv etc, used for a card.
 * Shoud be a subset of [EntrySchema]
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
    val rating: Float?
) : EntryMark {
    constructor(schema: EntrySchema) : this(
        title = schema.displayTitle,
        category = EntryType.valueOf(schema.category.replaceFirstChar { it.uppercase() }),
        url = schema.url,
        des = schema.description,
        coverUrl = schema.coverImageUrl,
        rating = schema.rating
    )
}

interface EntryMark

