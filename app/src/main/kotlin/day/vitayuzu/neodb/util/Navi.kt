package day.vitayuzu.neodb.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

sealed interface Navi {
    @Serializable
    data object Home : Navi

    @Serializable
    data object Library : Navi

    @Serializable
    data object Settings : Navi

    @Serializable
    data object Login

    companion object {
        val mainScreens = listOf(
            MainScreen("Home", Home, Icons.Default.Home),
            MainScreen("Library", Library, Icons.Default.DateRange), // FIXME: library icon
            MainScreen("Settings", Settings, Icons.Default.Settings),
        )
    }
}

data class MainScreen<T : Navi>(
    val name: String,
    val route: T,
    val icon: ImageVector,
)
