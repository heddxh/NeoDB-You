package day.vitayuzu.neodb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import day.vitayuzu.neodb.ui.page.library.LibraryPage
import day.vitayuzu.neodb.ui.page.login.LoginPage
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import kotlinx.serialization.Serializable
import org.publicvalue.multiplatform.oidc.appsupport.AndroidCodeAuthFlowFactory

@Serializable
object Home

@Serializable
object Library

@Serializable
object Settings

@Serializable
object Login

class MainActivity : ComponentActivity() {

    // Auth/Oauth2
    private val codeAuthFlowFactory = AndroidCodeAuthFlowFactory(useWebView = false)

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
                    startDestination = Login // FIXME: Should be [Home]
                ) {
                    composable<Login> { LoginPage(codeAuthFlowFactory) }
                    composable<Home> {}
                    composable<Library> { LibraryPage() }
                    composable<Settings> {}
                }
            }
        }
    }
}