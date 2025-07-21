package day.vitayuzu.neodb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.request.crossfade
import dagger.hilt.android.AndroidEntryPoint
import day.vitayuzu.neodb.ui.component.SearchModal
import day.vitayuzu.neodb.ui.model.Entry
import day.vitayuzu.neodb.ui.page.detail.DetailPage
import day.vitayuzu.neodb.ui.page.home.HomeScreen
import day.vitayuzu.neodb.ui.page.library.LibraryPage
import day.vitayuzu.neodb.ui.page.settings.SettingsPage
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import day.vitayuzu.neodb.util.EntryType
import day.vitayuzu.neodb.util.MainScreen
import day.vitayuzu.neodb.util.Navi
import day.vitayuzu.neodb.util.Navi.Companion.mainScreens
import day.vitayuzu.neodb.util.Navi.Home
import day.vitayuzu.neodb.util.Navi.Library
import day.vitayuzu.neodb.util.Navi.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SingletonImageLoader.setSafe {
            ImageLoader
                .Builder(this)
                .crossfade(true)
                .build()
        }

        enableEdgeToEdge()
        setContent {
            NeoDBYouTheme {
                MainScaffold()
            }
        }
    }
}

/**
 * Main scaffold for the app.
 * It contains the bottom navigation bar, top appbar and the main content(screens).
 */
@Composable
private fun MainScaffold(
    modifier: Modifier = Modifier,
    viewModel: MainActivityViewModel = viewModel(),
) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentMainScreen = mainScreens.find { screen ->
        currentBackStack?.destination?.hierarchy?.any { nav ->
            nav.hasRoute(screen.route::class)
        } ?: false
    }

    var showNewMarkModal by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            MainTopBar(currentMainScreen, viewModel::search) { type, uuid ->
                navController.navigate(Navi.Detail(type, uuid))
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = currentBackStack?.destination?.hasRoute(Navi.Detail::class) == true,
            ) {
                FloatingActionButton(
                    modifier = Modifier.animateEnterExit(),
                    onClick = { showNewMarkModal = true },
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add new mark for this entry",
                    )
                }
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = currentMainScreen in mainScreens,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                NavigationBar(Modifier.animateEnterExit()) {
                    mainScreens.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(stringResource(screen.name)) },
                            selected = currentMainScreen == screen,
                            onClick = {
                                // FIXME: detail back to home
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        )
                    }
                }
            }
        },
    ) {
        MainNavi(
            navController = navController,
            insetsPaddingValues = it,
            showNewMarkModal = showNewMarkModal,
            onModalDismiss = { showNewMarkModal = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopBar(
    currentMainScreen: MainScreen<out Navi>?,
    onSearch: (String) -> Flow<List<Entry>> = { flowOf() },
    onClickEntry: (EntryType, String) -> Unit = { _, _ -> },
) {
    val scope = rememberCoroutineScope()
    if (currentMainScreen != null) {
        val searchBarState = rememberSearchBarState(SearchBarValue.Collapsed)
        Crossfade(searchBarState.currentValue) {
            if (it == SearchBarValue.Expanded) {
                SearchModal(
                    state = searchBarState,
                    onSearch = onSearch,
                    onClickEntry = onClickEntry,
                )
            } else {
                TopAppBar(
                    modifier = Modifier.padding(top = 8.dp), // TopSearchBar has an extra padding
                    title = { Text(stringResource(currentMainScreen.name)) },
                    actions = {
                        when (currentMainScreen.route) {
                            // Show search button
                            Home, Library -> IconButton(
                                onClick = {
                                    scope.launch { searchBarState.animateToExpanded() }
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                )
                            }

                            else -> {}
                        }
                    },
                )
            }
        }
    }
}

/**
 * Main navigation for the app.
 * Control which screen to show in the [MainScaffold].
 */
@Composable
private fun MainNavi(
    navController: NavHostController,
    insetsPaddingValues: PaddingValues,
    modifier: Modifier = Modifier,
    showNewMarkModal: Boolean = false,
    onModalDismiss: () -> Unit = {},
) {
    NavHost(
        navController = navController,
        startDestination = Home,
//        popExitTransition = {
//            scaleOut(
//                targetScale = 0.9f,
//                transformOrigin = TransformOrigin.Center,
//            )
//        },
//        popEnterTransition = { EnterTransition.None },
        modifier = modifier,
    ) {
        // Apply and consume window insets padding from scaffold.
        val mainScreenModifier =
            Modifier.padding(insetsPaddingValues).consumeWindowInsets(insetsPaddingValues)
        composable<Home> {
            HomeScreen(
                modifier = mainScreenModifier,
                onClickEntry = { type, uuid ->
                    navController.navigate(Navi.Detail(type, uuid))
                },
            )
        }
        composable<Library> {
            LibraryPage(
                modifier = mainScreenModifier,
                onClickEntry = { type, uuid ->
                    navController.navigate(Navi.Detail(type, uuid))
                },
            )
        }
        composable<Settings> {
            SettingsPage(modifier = mainScreenModifier)
        }
        composable<Navi.Detail> {
            val detailEntry: Navi.Detail = it.toRoute()
            DetailPage(
                type = detailEntry.type,
                uuid = detailEntry.uuid,
                showNewMarkModal = showNewMarkModal,
                onModalDismiss = onModalDismiss,
            )
        }
    }
}
