package day.vitayuzu.neodb.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import day.vitayuzu.neodb.ui.page.search.SearchPageKey
import day.vitayuzu.neodb.util.AppNavigator
import day.vitayuzu.neodb.util.LocalNavigator
import day.vitayuzu.neodb.util.sharedBoundsTransition
import day.vitayuzu.neodb.util.sharedElementTransition

@Composable
fun SharedFab(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val fabState = remember { MutableTransitionState(false).apply { targetState = true } }
    AnimatedVisibility(
        visibleState = fabState,
        modifier = modifier.sharedElementTransition(SharedFabKey),
        enter = scaleIn(),
        exit = scaleOut(),
    ) {
        FloatingActionButton(onClick = onClick) {
            content()
        }
    }
}

@Composable
fun SharedSearchFab(modifier: Modifier = Modifier) {
    val appNavigator = LocalNavigator.current
    SharedFab(
        modifier = modifier.sharedBoundsTransition(
            key = SearchPageKey,
            enter = scaleIn(),
            exit = fadeOut() + scaleOut(),
        ),
        onClick = { appNavigator goto AppNavigator.Search },
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Perform search",
        )
    }
}

data object SharedFabKey
