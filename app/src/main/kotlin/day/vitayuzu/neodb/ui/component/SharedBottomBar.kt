package day.vitayuzu.neodb.ui.component

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import day.vitayuzu.neodb.util.AppNavigator
import day.vitayuzu.neodb.util.LocalNavigator
import day.vitayuzu.neodb.util.sharedElementTransition

@Composable
fun SharedBottomBar(modifier: Modifier = Modifier) {
    val appNavigator = LocalNavigator.current
    NavigationBar(modifier = modifier.sharedElementTransition("BottomBar")) {
        AppNavigator.Companion.TopLevelDestinations.forEach { destination ->
            NavigationBarItem(
                icon = { Icon(destination.icon, null) },
                label = { Text(stringResource(destination.name)) },
                selected = appNavigator.current == destination,
                onClick = { appNavigator goto destination },
            )
        }
    }
}
