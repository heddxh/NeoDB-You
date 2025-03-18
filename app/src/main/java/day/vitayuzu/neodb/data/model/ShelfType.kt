package day.vitayuzu.neodb.data.model

import day.vitayuzu.neodb.R

enum class ShelfType {
    Wishlist,
    Progress,
    Completed,
    Dropped;

    fun toR(): Int {
        return when (this) {
            ShelfType.Wishlist -> R.string.shelf_displayname_wishlist
            ShelfType.Progress -> R.string.shelf_displayname_progress
            ShelfType.Completed -> R.string.shelf_displayname_completed
            ShelfType.Dropped -> R.string.shelf_displayname_completed
        }
    }
}