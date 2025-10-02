package day.vitayuzu.neodb

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.request.crossfade
import com.mikepenz.aboutlibraries.ui.compose.android.rememberLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import dagger.hilt.android.AndroidEntryPoint
import day.vitayuzu.neodb.data.AuthRepository
import day.vitayuzu.neodb.data.NeoDBRepository
import day.vitayuzu.neodb.data.UpdateRepository
import day.vitayuzu.neodb.ui.component.SharedBottomBar
import day.vitayuzu.neodb.ui.page.detail.DetailPage
import day.vitayuzu.neodb.ui.page.home.HomeScreen
import day.vitayuzu.neodb.ui.page.library.LibraryPage
import day.vitayuzu.neodb.ui.page.search.SearchPage
import day.vitayuzu.neodb.ui.page.settings.SettingsPage
import day.vitayuzu.neodb.ui.theme.MotionHorizontallyDirection
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import day.vitayuzu.neodb.ui.theme.sharedXAxisTransition
import day.vitayuzu.neodb.util.AppNavigator
import day.vitayuzu.neodb.util.AppNavigator.AppDestination
import day.vitayuzu.neodb.util.AppNavigator.Detail
import day.vitayuzu.neodb.util.AppNavigator.Home
import day.vitayuzu.neodb.util.AppNavigator.Library
import day.vitayuzu.neodb.util.AppNavigator.License
import day.vitayuzu.neodb.util.AppNavigator.Settings
import day.vitayuzu.neodb.util.AppNavigator.TopLevelDestination
import day.vitayuzu.neodb.util.LocalNavigator
import day.vitayuzu.neodb.util.LocalSharedTransitionScope
import day.vitayuzu.neodb.util.SharedTransitionScopeProvider
import day.vitayuzu.neodb.util.rememberNavBackStack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var neoDBRepository: NeoDBRepository

    @Inject lateinit var authRepository: AuthRepository

    @Inject lateinit var updateRepository: UpdateRepository

    @Inject @AppScope lateinit var appScope: CoroutineScope

    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SingletonImageLoader.setSafe {
            ImageLoader
                .Builder(this)
                .crossfade(true)
                .build()
        }

        appScope.launch { authRepository.updateAccountStatus() }
        updateRepository.checkUpdateFlow.launchIn(appScope)

        enableEdgeToEdge()
        setContent {
            NeoDBYouTheme {
                // Save and restore navigation back stack.
                val backStack: NavBackStack<AppDestination> = rememberNavBackStack(Home)
                val previous = rememberNavBackStack<AppDestination>()
                val appNavigator = remember {
                    AppNavigator(
                        backStack = backStack,
                        previous = previous.firstOrNull(),
                        checkLogin = { authRepository.accountStatus.value.isLogin },
                        gotoLogin = { this.startActivity(Intent(this, OauthActivity::class.java)) },
                    )
                }
                CompositionLocalProvider(LocalNavigator provides appNavigator) {
                    SharedTransitionLayout {
                        CompositionLocalProvider(
                            LocalSharedTransitionScope provides SharedTransitionScopeProvider(this),
                        ) {
                            // This root surface is to make all transition have proper background.
                            // Or when transition between screens with dark mode, user will see white background.
                            Surface {
                                MainNavDisplay()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MainNavDisplay(modifier: Modifier = Modifier) {
    val appNavigator = LocalNavigator.current

    val libraries by rememberLibraries(R.raw.aboutlibraries)

    val sharedXAxisTransitionMetadata = remember {
        NavDisplay.transitionSpec {
            sharedXAxisTransition(
                MotionHorizontallyDirection.Forward,
            )
        } + NavDisplay.popTransitionSpec {
            sharedXAxisTransition(
                MotionHorizontallyDirection.Backward,
            )
        } + NavDisplay.predictivePopTransitionSpec {
            sharedXAxisTransition(
                MotionHorizontallyDirection.Backward,
            )
        }
    }

    NavDisplay(
        modifier = modifier,
        backStack = appNavigator.backStack,
        onBack = { appNavigator.back() },
        entryDecorators = listOf(
            rememberSceneSetupNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            // Keep viewmodel of top level destinations.
            rememberViewModelStoreNavEntryDecorator {
                appNavigator.previous !is TopLevelDestination
            },
        ),
        transitionSpec = {
            if (appNavigator.current is TopLevelDestination) {
                sharedXAxisTransition(appNavigator.animationDirection)
            } else {
                EnterTransition.None togetherWith ExitTransition.None
            }
        },
        predictivePopTransitionSpec = {
            EnterTransition.None togetherWith ExitTransition.None
        },
        popTransitionSpec = {
            EnterTransition.None togetherWith ExitTransition.None
        },
        entryProvider = entryProvider {
            entry<Home> {
                HomeScreen {
                    appNavigator.SharedBottomBar()
                }
            }
            entry<Library> {
                LibraryPage {
                    appNavigator.SharedBottomBar()
                }
            }
            entry<Settings> {
                SettingsPage {
                    appNavigator.SharedBottomBar()
                }
            }
            entry<AppNavigator.Search> {
                SearchPage()
            }
            entry<Detail> {
                DetailPage(it.type, it.uuid)
            }
            entry<License>(metadata = sharedXAxisTransitionMetadata) {
                LibrariesContainer(libraries)
            }
        },
    )
}
