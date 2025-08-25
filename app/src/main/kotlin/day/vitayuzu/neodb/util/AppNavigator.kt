package day.vitayuzu.neodb.util

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.vector.ImageVector
import day.vitayuzu.neodb.R

/**
 * Navi: さくらふたば
 */
class AppNavigator(val checkLogin: () -> Boolean, val gotoLogin: () -> Unit = {}) {

    val backStack = mutableStateListOf<AppDestination>(Home)

    val current: AppDestination?
        get() = backStack.lastOrNull()

    infix fun goTo(destination: AppDestination) {
        if (destination is RequireLogin && !checkLogin()) {
            gotoLogin()
        } else if (destination is TopLevelDestination) {
            backStack.clear()
            backStack.add(destination)
        } else {
            backStack.add(destination)
        }
    }

    private sealed interface RequireLogin

    sealed interface AppDestination

    sealed interface TopLevelDestination : AppDestination {
        val icon: ImageVector
        val name: Int
    }

    data object Home : TopLevelDestination {
        override val icon = Icons.Default.Home

        @StringRes override val name = R.string.home_title
    }

    data object Library : RequireLogin, TopLevelDestination {
        override val icon = Icons.Default.DateRange

        @StringRes override val name = R.string.library_title
    }

    data object Settings : TopLevelDestination {
        override val icon = Icons.Default.Settings

        @StringRes override val name = R.string.settings_title
    }

    data class Detail(val type: EntryType, val uuid: String) : AppDestination, RequireLogin

    data object License : AppDestination

    companion object {
        val TopLevelDestinations = listOf(Home, Library, Settings)
    }
}
