package day.vitayuzu.neodb.util

import androidx.annotation.Keep
import day.vitayuzu.neodb.R
import kotlinx.serialization.Serializable

@Suppress("ktlint:standard:enum-entry-name-case", "EnumEntryName")
@Serializable
@Keep // Maybe removed by R8
enum class EntryType {
    game,
    book,
    movie,
    tv,
    music,
    podcast,
    performance,

    // Fanfic, Exhibition, Collection,
    default, ;

    fun toR(): Int = when (this) {
        book -> R.string.entry_displayname_book
        movie -> R.string.entry_displayname_movie
        tv -> R.string.entry_displayname_tv
        music -> R.string.entry_displayname_music
        game -> R.string.entry_displayname_game
        podcast -> R.string.entry_displayname_podcast
        performance -> R.string.entry_displayname_performance
        // FIXME: add more types for android string resource
        // Fanfic -> R.string.entry_displayname_fanfic
        // Exhibition -> R.string.entry_displayname_exhibition
        // Collection -> R.string.entry_displayname_collection
        else -> R.string.entry_displayname_other
    }
}
