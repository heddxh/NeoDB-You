package day.vitayuzu.neodb.util

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.vector.ImageVector
import day.vitayuzu.neodb.R

/**
 * Navi: さくらふたば
 */
class AppNavigator(val checkLogin: () -> Boolean, val gotoLogin: () -> Unit = {}) {

    val backStack = mutableStateListOf<AppDestination>(Home)

    val current: AppDestination
        get() = backStack.last()

    var previous: AppDestination? = null
        private set

    val animationDestination: Boolean
        get() = previous?.directionFor(current) ?: true

    infix fun goto(destination: AppDestination) {
        if (destination is RequireLogin && !checkLogin()) {
            gotoLogin()
        } else if (destination is TopLevelDestination) {
            previous = current
            backStack.clear()
            backStack.add(destination)
        } else {
            previous = current
            backStack.add(destination)
        }
    }

    fun back() {
        backStack.removeLastOrNull()
        previous = backStack.lastOrNull()
    }

    private sealed interface RequireLogin

    sealed interface AppDestination {
        fun directionFor(destination: AppDestination): Boolean
    }

    sealed interface TopLevelDestination : AppDestination {
        val icon: ImageVector
        val name: Int
    }

    data object Home : TopLevelDestination {
        override val icon = Icons.Default.Home

        @StringRes override val name = R.string.home_title

        override fun directionFor(destination: AppDestination) = true
    }

    data object Library : RequireLogin, TopLevelDestination {
        override val icon = Icons.Default.DateRange

        @StringRes override val name = R.string.library_title

        override fun directionFor(destination: AppDestination) = destination !is Home
    }

    data object Settings : TopLevelDestination {
        override val icon = Icons.Default.Settings

        @StringRes override val name = R.string.settings_title

        override fun directionFor(destination: AppDestination) = destination is License
    }

    data class Detail(val type: EntryType, val uuid: String) : AppDestination, RequireLogin {
        override fun directionFor(destination: AppDestination) = true
    }

    data object License : AppDestination {
        override fun directionFor(destination: AppDestination) = true
    }

    companion object {
        val TopLevelDestinations = listOf(Home, Library, Settings)
    }
}

val LocalNavigator = staticCompositionLocalOf<AppNavigator> {
    error("No AppNavigator provided")
}
