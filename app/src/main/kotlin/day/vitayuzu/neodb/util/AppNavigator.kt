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
import day.vitayuzu.neodb.ui.theme.MotionHorizontallyDirection

/**
 * Navi: さくらふたば
 */
class AppNavigator(val checkLogin: () -> Boolean, val gotoLogin: () -> Unit = {}) {

    val backStack = mutableStateListOf<AppDestination>(Home)

    val current: AppDestination
        get() = backStack.last()

    var previous: AppDestination? = null
        private set

    val animationDirection: MotionHorizontallyDirection
        get() = previous?.directionTo(current) ?: MotionHorizontallyDirection.Forward

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
        previous = backStack.lastOrNull()
        backStack.removeLastOrNull()
    }

    private sealed interface RequireLogin

    sealed interface AppDestination {
        fun directionTo(destination: AppDestination): MotionHorizontallyDirection
    }

    sealed interface TopLevelDestination : AppDestination {
        val icon: ImageVector
        val name: Int
    }

    data object Home : TopLevelDestination {
        override val icon = Icons.Default.Home

        @StringRes override val name = R.string.home_title

        override fun directionTo(destination: AppDestination) = MotionHorizontallyDirection.Forward
    }

    data object Library : RequireLogin, TopLevelDestination {
        override val icon = Icons.Default.DateRange

        @StringRes override val name = R.string.library_title

        override fun directionTo(destination: AppDestination) = if (destination is Home) {
            MotionHorizontallyDirection.Backward
        } else {
            MotionHorizontallyDirection.Forward
        }
    }

    data object Settings : TopLevelDestination {
        override val icon = Icons.Default.Settings

        @StringRes override val name = R.string.settings_title

        override fun directionTo(destination: AppDestination) = if (destination is License) {
            MotionHorizontallyDirection.Forward
        } else {
            MotionHorizontallyDirection.Backward
        }
    }

    data object Search : AppDestination {
        override fun directionTo(destination: AppDestination) = if (destination is Detail) {
            MotionHorizontallyDirection.Forward
        } else {
            MotionHorizontallyDirection.Backward
        }
    }

    data class Detail(val type: EntryType, val uuid: String) : AppDestination, RequireLogin {
        override fun directionTo(destination: AppDestination) = MotionHorizontallyDirection.Forward
    }

    data object License : AppDestination {
        override fun directionTo(destination: AppDestination) = MotionHorizontallyDirection.Forward
    }

    companion object {
        val TopLevelDestinations = listOf(Home, Library, Settings)
    }
}

val LocalNavigator = staticCompositionLocalOf {
    AppNavigator({ true }, {})
}
