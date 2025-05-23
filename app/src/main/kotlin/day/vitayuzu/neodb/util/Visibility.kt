package day.vitayuzu.neodb.util

import day.vitayuzu.neodb.R

enum class Visibility {
    Public, // 0
    Unlisted, // 1
    Followers, // 2
    ;

    fun toR() = when (this) {
        Public -> R.string.visibility_displayname_public
        Unlisted -> R.string.visibility_displayname_unlisted
        Followers -> R.string.visibility_displayname_followers
    }
}
