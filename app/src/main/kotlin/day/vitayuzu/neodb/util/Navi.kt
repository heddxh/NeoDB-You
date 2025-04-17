package day.vitayuzu.neodb.util

import kotlinx.serialization.Serializable

sealed interface Navi {
    @Serializable
    data object Home : Navi

    @Serializable
    data object Library : Navi

    @Serializable
    data object Settings : Navi

    @Serializable
    data object Login : Navi
}
