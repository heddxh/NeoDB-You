package day.vitayuzu.neodb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import day.vitayuzu.neodb.data.UserShelfRemoteSource
import day.vitayuzu.neodb.data.UserShelfRepository
import day.vitayuzu.neodb.data.createNeoDbApi
import day.vitayuzu.neodb.data.model.ShelfType
import day.vitayuzu.neodb.ui.me.MainViewModel
import day.vitayuzu.neodb.ui.me.MePage
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

val ktorfit = ktorfit {
    baseUrl("https://neodb.social/api/")
    httpClient(HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    })
}
val api = ktorfit.createNeoDbApi()

val remoteSource = UserShelfRemoteSource(api, Dispatchers.IO)
val repo = UserShelfRepository(remoteSource)
val viewModel = MainViewModel(repo)

@Serializable
object Home

@Serializable
object Me

@Serializable
object Settings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel.fetchUserShelf(ShelfType.Wishlist)
        setContent {
            NeoDBYouTheme {
                // Navigation
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Me //FIXME: Should be [Home]
                ) {
                    composable<Home> {}
                    composable<Me> { MePage() }
                    composable<Settings> {}
                }
            }
        }
    }
}