package day.vitayuzu.neodb.ui.model

import day.vitayuzu.neodb.data.schema.detail.AlbumSchema
import day.vitayuzu.neodb.data.schema.detail.DetailSchema
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

fun DetailSchema.toDetail(): Detail {
    val infoParts = when (this) {
        is EditionSchema -> buildList {
            addAll(author)
            addAll(translator)
            pubHouse?.let { add(it) }
            pubYear?.let { add(it.toString()) }
            imprint?.let { add(it) }
            series?.let { add(it) }
        }

        is GameSchema -> buildList {
            addAll(developer)
            addAll(genre)
            addAll(platform.take(5)) // Take up to 5 platforms
            releaseDate?.let { add(it) }
        }

        is MovieSchema -> buildList {
            // Take up to 5 people
            addAll((playwright + actor + director).take(5))
            addAll(genre)
            addAll(area)
            year?.let { add(it.toString()) }
            duration?.let { add(it) }
        }

        is TVShowSchema -> buildList {
            // Take up to 5 people
            addAll((playwright + actor + director).take(5))
            addAll(genre)
            addAll(area)
            year?.let { add(it.toString()) }
        }

        is TVSeasonSchema -> buildList {
            // Take up to 5 people
            addAll((playwright + actor + director).take(5))
            addAll(genre)
            addAll(area)
            year?.let { add(it.toString()) }
        }

        is AlbumSchema -> buildList {
            // Take up to 5 people
            addAll(artist.take(5))
            addAll(genre)
            addAll(company)
            releaseDate?.let { add(it) }
        }

        is PodcastSchema -> buildList {
            addAll(genre)
            addAll(host)
        }

        is PerformanceSchema -> buildList {
            addAll(genre)
            director.firstOrNull()?.let { add(it) }
            playwright.firstOrNull()?.let { add(it) }
            // Take up to 5 people
            actor.take(5).forEach { add(it.name) }
        }
    }

    return Detail(
        type = this.category,
        title = this.title,
        coverUrl = this.coverImageUrl,
        rating = this.rating,
        des = this.description,
        info = infoParts.filter { it.isNotBlank() }.joinToString(separator = " / "),
    )
}
