package day.vitayuzu.neodb.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import day.vitayuzu.neodb.util.sharedElementTransition

@Composable
fun SharedFab(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    // FIXME: Disable animation between same Fab.
    val fabState =
        remember { MutableTransitionState(false).apply { targetState = true } }
    AnimatedVisibility(
        visibleState = fabState,
        modifier = modifier.sharedElementTransition("FAB"),
        enter = scaleIn(),
        exit = scaleOut(),
    ) {
        FloatingActionButton(onClick = onClick) {
            content()
        }
    }
}
