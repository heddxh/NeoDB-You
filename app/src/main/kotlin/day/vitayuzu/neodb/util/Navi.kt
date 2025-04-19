package day.vitayuzu.neodb.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

/**
 * Represents the different navigation destinations within the application.
 */
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
        // Top level screens
        val mainScreens = listOf(
            MainScreen("Home", Home, Icons.Default.Home),
            MainScreen("Library", Library, Icons.Default.DateRange), // FIXME: library icon
            MainScreen("Settings", Settings, Icons.Default.Settings),
        )
    }
}

/**
 * Data class representing a main screen in the bottom navigation bar.
 * @param name Display name of the screen.
 * @param route Route of the screen, for type safe navigation.
 * @param icon The icon to display for this screen in the bottom navigation bar.
 */
data class MainScreen<T : Navi>(
    val name: String,
    val route: T,
    val icon: ImageVector,
)
