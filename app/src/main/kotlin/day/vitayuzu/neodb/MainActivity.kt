package day.vitayuzu.neodb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.stringResource
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
import day.vitayuzu.neodb.ui.page.detail.DetailPage
import day.vitayuzu.neodb.ui.page.home.HomeScreen
import day.vitayuzu.neodb.ui.page.library.LibraryPage
import day.vitayuzu.neodb.ui.page.login.LoginPage
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import day.vitayuzu.neodb.util.Navi
import day.vitayuzu.neodb.util.Navi.Companion.mainScreens
import day.vitayuzu.neodb.util.Navi.Home
import day.vitayuzu.neodb.util.Navi.Library
import day.vitayuzu.neodb.util.Navi.Login
import day.vitayuzu.neodb.util.Navi.Settings
import org.publicvalue.multiplatform.oidc.appsupport.AndroidCodeAuthFlowFactory
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Auth/Oauth2 factory
    @Inject lateinit var codeAuthFlowFactory: AndroidCodeAuthFlowFactory

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
fun MainScaffold(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentScreen = mainScreens.find { screen ->
        currentBackStack?.destination?.hierarchy?.any { nav ->
            nav.hasRoute(screen.route::class)
        } ?: false
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            if (currentScreen != null) {
                TopAppBar(
                    title = { Text(stringResource(currentScreen.name)) },
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = currentScreen in mainScreens,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                NavigationBar(Modifier.animateEnterExit()) {
                    mainScreens.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(stringResource(screen.name)) },
                            selected = currentScreen == screen,
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
            modifier = Modifier.padding(it),
        )
    }
}

/**
 * Main navigation for the app.
 * Control which screen to show in the [MainScaffold].
 */
@Composable
fun MainNavi(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Home,
        popExitTransition = {
            scaleOut(
                targetScale = 0.9f,
                transformOrigin = TransformOrigin(pivotFractionX = 0.5f, pivotFractionY = 0.5f),
            )
        },
        popEnterTransition = { EnterTransition.None },
        modifier = modifier,
    ) {
        composable<Login> { LoginPage({ navController.navigate(Home) }) }
        composable<Home> {
            HomeScreen(
                onClickEntry = { type, uuid ->
                    navController.navigate(Navi.Detail(type, uuid))
                },
            )
        }
        composable<Library> {
            LibraryPage(
                onClickEntry = { type, uuid ->
                    navController.navigate(Navi.Detail(type, uuid))
                },
            )
        }
        composable<Settings> {}
        composable<Navi.Detail> {
            val detailEntry: Navi.Detail = it.toRoute()
            DetailPage(detailEntry.type, detailEntry.uuid)
        }
    }
}
