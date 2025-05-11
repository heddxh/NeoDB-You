package day.vitayuzu.neodb.ui.model

import day.vitayuzu.neodb.data.schema.detail.AlbumSchema
import day.vitayuzu.neodb.data.schema.detail.EditionSchema
import day.vitayuzu.neodb.data.schema.detail.GameSchema
import day.vitayuzu.neodb.data.schema.detail.MovieSchema
import day.vitayuzu.neodb.data.schema.detail.PerformanceSchema
import day.vitayuzu.neodb.data.schema.detail.PodcastSchema
import day.vitayuzu.neodb.data.schema.detail.TVSeasonSchema
import day.vitayuzu.neodb.data.schema.detail.TVShowSchema
import day.vitayuzu.neodb.util.EntryType

data class Detail(
    val type: EntryType,
    val title: String,
    val coverUrl: String?,
    val rating: Float?,
    val info: String?,
    val des: String?,
)

private fun MutableSet<String>.addNotNull(element: Any?) {
    element?.let { this += it.toString() }
}

fun EditionSchema.toDetail() = Detail(
    type = this.category,
    title = this.title,
    coverUrl = this.coverImageUrl,
    rating = this.rating,
    des = this.description,
    info = buildSet {
        addAll(this@toDetail.author)
        addAll(this@toDetail.translator)
        addNotNull(this@toDetail.pubHouse)
        addNotNull(this@toDetail.pubYear)
        addNotNull(this@toDetail.imprint)
        addNotNull(this@toDetail.series)
    }.filter { it.isNotBlank() }.joinToString(separator = " / "),
)

fun GameSchema.toDetail() = Detail(
    type = this.category,
    title = this.title,
    coverUrl = this.coverImageUrl,
    rating = this.rating?.div(2),
    des = this.description,
    info = buildSet {
        addAll(this@toDetail.developer)
        addAll(this@toDetail.genre)
        // Take up to 5 platforms
        addAll(this@toDetail.platform.take(5))
        addNotNull(this@toDetail.releaseDate)
        addNotNull(this@toDetail.releaseDate)
    }.filter { it.isNotBlank() }.joinToString(separator = " / "),
)

fun MovieSchema.toDetail() = Detail(
    type = this.category,
    title = this.title,
    coverUrl = this.coverImageUrl,
    rating = this.rating?.div(2),
    des = this.description,
    info = buildSet {
        // Take up to 5 people
        addAll((this@toDetail.playwright + this@toDetail.actor + this@toDetail.director).take(5))
        addAll(this@toDetail.genre)
        addAll(this@toDetail.area)
        addNotNull(this@toDetail.year)
        addNotNull(this@toDetail.duration)
    }.filter { it.isNotBlank() }.joinToString(separator = " / "),
)

fun TVShowSchema.toDetail() = Detail(
    type = this.category,
    title = this.title,
    coverUrl = this.coverImageUrl,
    rating = this.rating?.div(2),
    des = this.description,
    info = buildSet {
        // Take up to 5 people
        addAll((this@toDetail.playwright + this@toDetail.actor + this@toDetail.director).take(5))
        addAll(this@toDetail.genre)
        addAll(this@toDetail.area)
        addNotNull(this@toDetail.year)
    }.filter { it.isNotBlank() }.joinToString(separator = " / "),
)

fun TVSeasonSchema.toDetail() = Detail(
    type = this.category,
    title = this.title,
    coverUrl = this.coverImageUrl,
    rating = this.rating?.div(2),
    des = this.description,
    info = buildSet {
        // Take up to 5 people
        addAll((this@toDetail.playwright + this@toDetail.actor + this@toDetail.director).take(5))
        addAll(this@toDetail.genre)
        addAll(this@toDetail.area)
        addNotNull(this@toDetail.year)
    }.filter { it.isNotBlank() }.joinToString(separator = " / "),
)

fun AlbumSchema.toDetail() = Detail(
    type = this.category,
    title = this.title,
    coverUrl = this.coverImageUrl,
    rating = this.rating?.div(2),
    des = this.description,
    info = buildSet {
        // Take up to 5 people
        addAll(this@toDetail.artist.take(5))
        addAll(this@toDetail.genre)
        addAll(this@toDetail.company)
        addNotNull(this@toDetail.releaseDate)
    }.filter { it.isNotBlank() }.joinToString(separator = " / "),
)

fun PodcastSchema.toDetail() = Detail(
    type = this.category,
    title = this.title,
    coverUrl = this.coverImageUrl,
    rating = this.rating?.div(2),
    des = this.description,
    info = buildSet {
        addAll(this@toDetail.genre)
        addAll(this@toDetail.host)
    }.filter { it.isNotBlank() }.joinToString(separator = " / "),
)

fun PerformanceSchema.toDetail() = Detail(
    type = this.category,
    title = this.title,
    coverUrl = this.coverImageUrl,
    rating = this.rating?.div(2),
    des = this.description,
    info = buildSet {
        addAll(this@toDetail.genre)
        addNotNull(this@toDetail.director.first())
        addNotNull(this@toDetail.playwright.first())
        // Take up to 5 people
        this@toDetail.actor.take(5).forEach {
            add(it.name)
        }
    }.filter { it.isNotBlank() }.joinToString(separator = " / "),
)
