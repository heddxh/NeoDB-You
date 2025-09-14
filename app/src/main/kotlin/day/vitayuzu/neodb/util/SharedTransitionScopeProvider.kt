package day.vitayuzu.neodb.util

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation3.ui.LocalNavAnimatedContentScope

@OptIn(ExperimentalSharedTransitionApi::class)
class SharedTransitionScopeProvider(
    val sharedTransitionScope: SharedTransitionScope,
)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.sharedBoundsTransition(key: Any) =
    with(LocalSharedTransitionScope.current?.sharedTransitionScope) {
        if (this == null) {
            Modifier
        } else {
            this@sharedBoundsTransition.sharedBounds(
                rememberSharedContentState(key),
                LocalNavAnimatedContentScope.current,
            )
        }
    }

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.sharedElementTransition(
    key: Any,
    animatedVisibilityScope: AnimatedVisibilityScope = LocalNavAnimatedContentScope.current,
) = with(LocalSharedTransitionScope.current?.sharedTransitionScope) {
    if (this == null) {
        Modifier
    } else {
        this@sharedElementTransition.sharedElement(
            rememberSharedContentState(key),
            animatedVisibilityScope,
        )
    }
}

val LocalSharedTransitionScope = staticCompositionLocalOf<SharedTransitionScopeProvider?> { null }
