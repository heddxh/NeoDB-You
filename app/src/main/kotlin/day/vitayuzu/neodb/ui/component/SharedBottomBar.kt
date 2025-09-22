package day.vitayuzu.neodb.ui.component

import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import day.vitayuzu.neodb.util.AppNavigator
import day.vitayuzu.neodb.util.AppNavigator.Detail
import day.vitayuzu.neodb.util.AppNavigator.TopLevelDestination
import day.vitayuzu.neodb.util.LocalSharedTransitionScope
import day.vitayuzu.neodb.util.sharedElementTransition

/**
 * Shared, persistent bottom bar shows in top level destinations with seamless animation and self-retained state.
 * That means, bottom bar in different scaffolds will share the unique instance without animation reset.
 *
 * Outer box has [androidx.compose.animation.SharedTransitionScope.sharedElement] modifier
 * to make the whole composable stay in place when destination changes.
 * [AppNavigator.bottomBar] is a [androidx.compose.runtime.movableContentOf] wrapped version of [AppNavigator.bottomBarContent].
 * As soon as transition happens, [AppNavigator.bottomBar] will swapped with [AppNavigator.bottomBarContent] as placeholder,
 * to avoid replicate instance of [AppNavigator.bottomBar] coexisting.
 * That's the key to make [androidx.compose.runtime.movableContentOf] works.
 *
 * Thanks for the genius idea from [tunjid's post](https://www.tunjid.com/articles/ui-layer-architecture-for-persistent-ui-elements-68248e8ecc8e85f53ce1aa46).
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavigator.SharedBottomBar(modifier: Modifier = Modifier) {
    val renderInOverlayModifier =
        with(LocalSharedTransitionScope.current!!.sharedTransitionScope) {
            Modifier.renderInSharedTransitionScopeOverlay {
                // WORKAROUND: Don't know why but without this, bottom bar will miss when goto license page.
                with(this@SharedBottomBar) {
                    isTransitionActive && when {
                        current is Detail -> true
                        current is TopLevelDestination && previous is Detail -> true
                        else -> false
                    }
                }
            }
        }
    val animateModifier =
        with(LocalNavAnimatedContentScope.current) {
            Modifier.animateEnterExit(
                enter = expandVertically(),
                exit = shrinkVertically(),
            )
        }
    Box(
        modifier
            .sharedElementTransition(SharedBottomBarKey)
            .then(renderInOverlayModifier)
            .then(animateModifier),
    ) {
        if (LocalNavAnimatedContentScope.current.transition.targetState != EnterExitState.Visible &&
            this@SharedBottomBar.current is TopLevelDestination
        ) {
            // Placeholder. Using 0-height box may lead problems.
            this@SharedBottomBar.bottomBarContent()
        } else {
            this@SharedBottomBar.bottomBar() // Real bottom bar
        }
    }
}

private data object SharedBottomBarKey
