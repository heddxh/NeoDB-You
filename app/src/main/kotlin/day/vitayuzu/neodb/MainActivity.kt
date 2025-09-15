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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
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
import day.vitayuzu.neodb.ui.page.detail.DetailPage
import day.vitayuzu.neodb.ui.page.home.HomeScreen
import day.vitayuzu.neodb.ui.page.library.LibraryPage
import day.vitayuzu.neodb.ui.page.search.SearchPage
import day.vitayuzu.neodb.ui.page.settings.SettingsPage
import day.vitayuzu.neodb.ui.theme.MotionHorizontallyDirection
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import day.vitayuzu.neodb.ui.theme.sharedXAxisTransition
import day.vitayuzu.neodb.util.AppNavigator
import day.vitayuzu.neodb.util.AppNavigator.Detail
import day.vitayuzu.neodb.util.AppNavigator.Home
import day.vitayuzu.neodb.util.AppNavigator.Library
import day.vitayuzu.neodb.util.AppNavigator.License
import day.vitayuzu.neodb.util.AppNavigator.Settings
import day.vitayuzu.neodb.util.AppNavigator.TopLevelDestination
import day.vitayuzu.neodb.util.LocalNavigator
import day.vitayuzu.neodb.util.LocalSharedTransitionScope
import day.vitayuzu.neodb.util.SharedTransitionScopeProvider
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var neoDBRepository: NeoDBRepository

    @Inject lateinit var authRepository: AuthRepository

    @Inject lateinit var updateRepository: UpdateRepository

    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SingletonImageLoader.setSafe {
            ImageLoader
                .Builder(this)
                .crossfade(true)
                .build()
        }

        lifecycleScope.launch { authRepository.updateAccountStatus() }
        updateRepository.checkUpdateFlow.launchIn(lifecycleScope)

        enableEdgeToEdge()
        setContent {
            NeoDBYouTheme {
                val appNavigator = remember {
                    AppNavigator(
                        checkLogin = { authRepository.accountStatus.value.isLogin },
                        gotoLogin = {
                            this.startActivity(Intent(this, OauthActivity::class.java))
                        },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainNavDisplay(modifier: Modifier = Modifier) {
    val appNavigator = LocalNavigator.current

    val libraries by rememberLibraries(R.raw.aboutlibraries)

    NavDisplay(
        backStack = appNavigator.backStack,
        onBack = { appNavigator.back() },
        modifier = modifier,
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
                HomeScreen()
            }
            entry<Library> {
                LibraryPage()
            }
            entry<Settings> {
                SettingsPage()
            }
            entry<AppNavigator.Search>(
                metadata = NavDisplay.transitionSpec {
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
                },
            ) {
                SearchPage()
            }
            entry<Detail> {
                DetailPage(it.type, it.uuid)
            }
            entry<License> {
                LibrariesContainer(libraries)
            }
        },
    )
}
