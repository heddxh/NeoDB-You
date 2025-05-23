package day.vitayuzu.neodb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import day.vitayuzu.neodb.data.AuthRepository
import day.vitayuzu.neodb.ui.page.detail.DetailPage
import day.vitayuzu.neodb.ui.page.home.HomeScreen
import day.vitayuzu.neodb.ui.page.library.LibraryPage
import day.vitayuzu.neodb.ui.page.settings.SettingsPage
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import day.vitayuzu.neodb.util.Navi
import day.vitayuzu.neodb.util.Navi.Companion.mainScreens
import day.vitayuzu.neodb.util.Navi.Home
import day.vitayuzu.neodb.util.Navi.Library
import day.vitayuzu.neodb.util.Navi.Settings
import org.publicvalue.multiplatform.oidc.appsupport.AndroidCodeAuthFlowFactory
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Auth/Oauth2 factory
    @Inject lateinit var codeAuthFlowFactory: AndroidCodeAuthFlowFactory

    @Inject lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        codeAuthFlowFactory.registerActivity(this)
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
@OptIn(ExperimentalMaterial3Api::class)
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

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            if (currentMainScreen != null) {
                TopAppBar(
                    title = { Text(stringResource(currentMainScreen.name)) },
                    actions = {
                        when (currentMainScreen.route) {
                            Home, Library -> Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.padding(horizontal = 8.dp),
                            )

                            else -> {}
                        }
                    },
                )
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = currentBackStack?.destination?.hasRoute(Navi.Detail::class) == true,
            ) {
                FloatingActionButton(
                    modifier = Modifier.animateEnterExit(),
                    onClick = viewModel::showComposeModal,
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
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
            showComposeModal = uiState.isShowComposeModal,
            onDismissComposeModal = viewModel::dismissComposeModal,
        )
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
    showComposeModal: Boolean = false,
    onDismissComposeModal: () -> Unit = {},
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
                showComposeModal = showComposeModal,
                onDismissComposeModal = onDismissComposeModal,
            )
        }
    }
}
