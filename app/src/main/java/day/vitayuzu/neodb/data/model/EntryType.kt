package day.vitayuzu.neodb.data.model

import day.vitayuzu.neodb.R

enum class EntryType {
    Book,
    Movie,
    Tv,
    Music,
    Game,
    Podcast,
    Performance,
    Fanfic,
    Exhibition,
    Collection;

    fun toR(): Int = when (this) {
        Book -> R.string.entry_displayname_book
        Movie -> R.string.entry_displayname_movie
        Tv -> R.string.entry_displayname_tv
        Music -> R.string.entry_displayname_music
        Game -> R.string.entry_displayname_game
        Podcast -> R.string.entry_displayname_podcast
        Performance -> R.string.entry_displayname_performance

        // FIXME: add more types for android string resource
        // Fanfic -> R.string.entry_displayname_fanfic
        // Exhibition -> R.string.entry_displayname_exhibition
        // Collection -> R.string.entry_displayname_collection
        else -> 0
    }
}