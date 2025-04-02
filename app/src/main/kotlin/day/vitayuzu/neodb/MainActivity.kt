package day.vitayuzu.neodb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import day.vitayuzu.neodb.ui.page.library.LibraryPage
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
object Library

@Serializable
object Settings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NeoDBYouTheme {
                // Navigation
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Library // FIXME: Should be [Home]
                ) {
                    composable<Home> {}
                    composable<Library> { LibraryPage() }
                    composable<Settings> {}
                }
            }
        }
    }
}