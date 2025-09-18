package day.vitayuzu.neodb.util

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
fun Modifier.sharedBoundsTransition(
    key: Any,
    enter: EnterTransition = fadeIn(),
    exit: ExitTransition = fadeOut(),
) = with(LocalSharedTransitionScope.current?.sharedTransitionScope) {
    if (this == null) {
        Modifier
    } else {
        this@sharedBoundsTransition.sharedBounds(
            rememberSharedContentState(key),
            LocalNavAnimatedContentScope.current,
            enter = enter,
            exit = exit,
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
