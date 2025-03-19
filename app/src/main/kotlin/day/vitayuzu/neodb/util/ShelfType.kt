package day.vitayuzu.neodb.util

import day.vitayuzu.neodb.R

@Suppress("EnumEntryName")
enum class ShelfType {
    wishlist,
    progress,
    complete,
    dropped;

    fun toR(): Int {
        return when (this) {
            wishlist -> R.string.shelf_displayname_wishlist
            progress -> R.string.shelf_displayname_progress
            complete -> R.string.shelf_displayname_completed
            dropped -> R.string.shelf_displayname_dropped
        }
    }
}