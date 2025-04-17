package day.vitayuzu.neodb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import day.vitayuzu.neodb.ui.page.library.LibraryPage
import day.vitayuzu.neodb.ui.page.login.LoginPage
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import day.vitayuzu.neodb.util.Navi.Home
import day.vitayuzu.neodb.util.Navi.Library
import day.vitayuzu.neodb.util.Navi.Login
import day.vitayuzu.neodb.util.Navi.Settings
import org.publicvalue.multiplatform.oidc.appsupport.AndroidCodeAuthFlowFactory
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Auth/Oauth2
    @Inject lateinit var codeAuthFlowFactory: AndroidCodeAuthFlowFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        codeAuthFlowFactory.registerActivity(this)
        enableEdgeToEdge()
        setContent {
            NeoDBYouTheme {
                // Navigation
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Login, // FIXME: Should be [Home]
                ) {
                    composable<Login> { LoginPage() }
                    composable<Home> {}
                    composable<Library> { LibraryPage() }
                    composable<Settings> {}
                }
            }
        }
    }
}
