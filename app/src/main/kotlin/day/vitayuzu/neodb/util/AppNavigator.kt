package day.vitayuzu.neodb.util

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import day.vitayuzu.neodb.R
import day.vitayuzu.neodb.ui.theme.MotionHorizontallyDirection
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Navi: さくらふたば
 */
class AppNavigator(
    backStack: NavBackStack<AppDestination> = NavBackStack(Home),
    previous: AppDestination? = null,
    val checkLogin: () -> Boolean = { true },
    val gotoLogin: () -> Unit = {},
) {
    private val _backStack = backStack
    val backStack: List<AppDestination> // expose read-only observable list
        get() = _backStack.toList()

    val current: AppDestination
        get() = _backStack.last()

    var previous: AppDestination? = previous
        private set

    val animationDirection: MotionHorizontallyDirection
        get() = previous?.directionTo(current) ?: MotionHorizontallyDirection.Forward

    infix fun goto(destination: AppDestination) {
        if (destination is RequireLogin && !checkLogin()) {
            gotoLogin()
        } else if (destination is TopLevelDestination) {
            previous = current
            _backStack.clear()
            _backStack.add(destination)
        } else {
            previous = current
            _backStack.add(destination)
        }
    }

    fun back() {
        previous = _backStack.lastOrNull()
        _backStack.removeLastOrNull()
    }

    private sealed interface RequireLogin

    sealed interface AppDestination : NavKey {
        fun directionTo(destination: AppDestination): MotionHorizontallyDirection
    }

    sealed interface TopLevelDestination : AppDestination {
        val icon: ImageVector
        val name: Int
    }

    @Serializable
    data object Home : TopLevelDestination {
        override val icon = Icons.Default.Home

        @StringRes override val name = R.string.home_title

        override fun directionTo(destination: AppDestination) = MotionHorizontallyDirection.Forward
    }

    @Serializable
    data object Library : RequireLogin, TopLevelDestination {
        override val icon = Icons.Default.DateRange

        @StringRes override val name = R.string.library_title

        override fun directionTo(destination: AppDestination) = if (destination is Home) {
            MotionHorizontallyDirection.Backward
        } else {
            MotionHorizontallyDirection.Forward
        }
    }

    @Serializable
    data object Settings : TopLevelDestination {
        override val icon = Icons.Default.Settings

        @StringRes override val name = R.string.settings_title

        override fun directionTo(destination: AppDestination) = if (destination is License) {
            MotionHorizontallyDirection.Forward
        } else {
            MotionHorizontallyDirection.Backward
        }
    }

    @Serializable
    data object Search : AppDestination {
        override fun directionTo(destination: AppDestination) = if (destination is Detail) {
            MotionHorizontallyDirection.Forward
        } else {
            MotionHorizontallyDirection.Backward
        }
    }

    @Serializable
    data class Detail(val type: EntryType, val uuid: String) : AppDestination, RequireLogin {
        override fun directionTo(destination: AppDestination) = MotionHorizontallyDirection.Forward
    }

    @Serializable
    data object License : AppDestination {
        override fun directionTo(destination: AppDestination) = MotionHorizontallyDirection.Forward
    }

    companion object {
        val TopLevelDestinations = listOf(Home, Library, Settings)
    }
}

val LocalNavigator = staticCompositionLocalOf { AppNavigator() }

/**
 * Modified from [androidx.navigation3.runtime.rememberNavBackStack].
 */
@Composable
inline fun <reified T : NavKey> rememberNavBackStack(vararg elements: T): NavBackStack<T> =
    rememberSerializable(serializer = serializer()) { NavBackStack(*elements) }
