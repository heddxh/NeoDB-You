package day.vitayuzu.neodb.ui.component

import androidx.compose.animation.EnterExitState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import day.vitayuzu.neodb.util.sharedElementTransition

/**
 * Shared, persistent bottom bar shows in top level destinations with seamless animation and self-retained state.
 * That means, bottom bar in different scaffolds will share the unique instance without animation reset.
 *
 * Outer box has [androidx.compose.animation.SharedTransitionScope.sharedElement] modifier
 * to make the whole composable stay in place when destination changes.
 * As soon as transition happens, [movableContent] will swapped with a empty [Box] as placeholder,
 * to avoid replicate instance of [movableContent] coexisting.
 * That's the key to make [androidx.compose.runtime.movableContentOf] works.
 *
 * Thanks for the genius idea from [tunjid's post](https://www.tunjid.com/articles/ui-layer-architecture-for-persistent-ui-elements-68248e8ecc8e85f53ce1aa46).
 *
 * @param movableContent Composable wrapped by [androidx.compose.runtime.movableContentOf]
 */
@Composable
fun SharedBottomBar(modifier: Modifier = Modifier, movableContent: @Composable () -> Unit) {
    Box(modifier.sharedElementTransition(SharedBottomBarKey)) {
        if (LocalNavAnimatedContentScope.current.transition.targetState != EnterExitState.Visible) {
            Box(Modifier.fillMaxWidth()) // placeholder
        } else {
            movableContent()
        }
    }
}

private data object SharedBottomBarKey
