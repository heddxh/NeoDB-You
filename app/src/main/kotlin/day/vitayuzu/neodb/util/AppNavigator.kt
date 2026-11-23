package day.vitayuzu.neodb.util

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import day.vitayuzu.neodb.R
import day.vitayuzu.neodb.ui.page.search.SearchPageKey
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

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    val bottomBarContent = @Composable {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            HorizontalFloatingToolbar(
                expanded = true,
                colors = FloatingToolbarDefaults.standardFloatingToolbarColors(
                    toolbarContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    toolbarContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { goto(Search) },
                        shape = MaterialTheme.shapes.large,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.sharedBoundsTransition(
                            key = SearchPageKey,
                            enter = scaleIn(),
                            exit = fadeOut() + scaleOut(),
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.cd_search),
                        )
                    }
                },
            ) {
                TopLevelDestinations.forEach { destination ->
                    val selected = backStack.any { it == destination }
                    val label = stringResource(destination.name)
                    Surface(
                        onClick = { goto(destination) },
                        shape = CircleShape,
                        color = if (selected) {
                            MaterialTheme.colorScheme.secondaryContainer
                        } else {
                            Color.Transparent
                        },
                        contentColor = if (selected) {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .animateContentSize(),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(
                                horizontal = if (selected) 16.dp else 14.dp,
                                vertical = 10.dp,
                            ),
                        ) {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = label,
                                modifier = Modifier.size(22.dp),
                            )
                            AnimatedVisibility(
                                visible = selected,
                                enter = fadeIn(),
                                exit = fadeOut(),
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    val bottomBar = movableContentOf { bottomBarContent() }

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

    @Serializable
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
