package day.vitayuzu.neodb

import android.app.assist.AssistContent
import android.content.Intent
import android.net.Uri
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.request.crossfade
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import dagger.hilt.android.AndroidEntryPoint
import day.vitayuzu.neodb.data.AppSettingsManager
import day.vitayuzu.neodb.data.AppSettingsManager.Companion.ONBOARDING_COMPLETED
import day.vitayuzu.neodb.data.AuthRepository
import day.vitayuzu.neodb.ui.component.SharedBottomBar
import day.vitayuzu.neodb.ui.page.detail.DetailPage
import day.vitayuzu.neodb.ui.page.detail.DetailViewModel
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var authRepository: AuthRepository

    @Inject lateinit var appSettingsManager: AppSettingsManager

    @Inject @AppScope lateinit var appScope: CoroutineScope

    private var webUriProvider: (() -> Uri?)? = null

    override fun onProvideAssistContent(outContent: AssistContent) {
        super.onProvideAssistContent(outContent)

        // Reparse the browser URL so query/fragment delimiters in an encoded path are recognized.
        val uri = webUriProvider?.invoke()?.let { Uri.parse(it.toString()) }
        outContent.webUri = uri
            ?.takeIf { it.scheme == "https" && !it.host.isNullOrBlank() && it.userInfo == null }
            ?.buildUpon()
            ?.clearQuery()
            ?.fragment(null)
            ?.build()
    }

    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen() // change back to `postSplashScreenTheme`
        super.onCreate(savedInstanceState)

        SingletonImageLoader.setSafe {
            ImageLoader
                .Builder(this)
                .crossfade(true)
                .build()
        }

        appScope.launch { authRepository.updateAccountStatus() }

        // Check if onboarding is completed
        appScope.launch {
            val onboardingCompleted = appSettingsManager.getAuthData(ONBOARDING_COMPLETED) ?: false
            if (!onboardingCompleted) {
                startActivity(Intent(this@MainActivity, OauthActivity::class.java))
            }
        }

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
                                MainNavDisplay(onWebUriProviderChanged = { webUriProvider = it })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MainNavDisplay(
    onWebUriProviderChanged: ((() -> Uri?)?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val appNavigator = LocalNavigator.current
    val backStack = appNavigator.backStack
    val detailViewModels = remember { mutableMapOf<Detail, DetailViewModel>() }

    DisposableEffect(appNavigator) {
        onWebUriProviderChanged {
            // Read the stack at request time: outgoing entries can outlive a navigation change.
            val destination = appNavigator.current as? Detail
            detailViewModels[destination]?.getFullUrl()
        }
        onDispose { onWebUriProviderChanged(null) }
    }

    val libraries by produceLibraries(R.raw.aboutlibraries)

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

    val entries = rememberDecoratedNavEntries(
        // Keep tab state and ViewModels even when a different top-level destination is displayed.
        backStack = backStack + AppNavigator.TopLevelDestinations.filterNot { it in backStack },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
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
            entry<Detail> { destination ->
                val viewModel = hiltViewModel<DetailViewModel, DetailViewModel.Factory> {
                    it.create(destination.type, destination.uuid)
                }
                DisposableEffect(destination, viewModel) {
                    detailViewModels[destination] = viewModel
                    onDispose { detailViewModels.remove(destination, viewModel) }
                }
                DetailPage(destination.type, destination.uuid, viewModel = viewModel)
            }
            entry<License>(metadata = sharedXAxisTransitionMetadata) {
                LibrariesContainer(libraries)
            }
        },
    )

    NavDisplay(
        modifier = modifier,
        entries = entries.take(backStack.size),
        onBack = { appNavigator.back() },
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
    )
}
