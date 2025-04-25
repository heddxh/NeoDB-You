package day.vitayuzu.neodb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import day.vitayuzu.neodb.ui.page.home.HomeScreen
import day.vitayuzu.neodb.ui.page.library.LibraryPage
import day.vitayuzu.neodb.ui.page.login.LoginPage
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
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
fun MainScaffold(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                val currentBackStack by navController.currentBackStackEntryAsState()
                val currentDestination = currentBackStack?.destination
                mainScreens.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(screen.icon, contentDescription = screen.name)
                        },
                        label = { Text(screen.name) },
                        selected = currentDestination?.hierarchy?.any {
                            it.hasRoute(screen.route::class)
                        } == true,
                        onClick = {
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
        startDestination = Login,
        modifier = modifier,
    ) {
        composable<Login> { LoginPage({ navController.navigate(Home) }) }
        composable<Home> { HomeScreen() }
        composable<Library> { LibraryPage() }
        composable<Settings> {}
    }
}
