package day.vitayuzu.neodb

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
import day.vitayuzu.neodb.ui.component.SearchModal
import day.vitayuzu.neodb.ui.model.Entry
import day.vitayuzu.neodb.ui.page.detail.DetailPage
import day.vitayuzu.neodb.ui.page.home.HomeScreen
import day.vitayuzu.neodb.ui.page.home.HomeViewModel
import day.vitayuzu.neodb.ui.page.library.LibraryPage
import day.vitayuzu.neodb.ui.page.library.LibraryViewModel
import day.vitayuzu.neodb.ui.page.settings.SettingsPage
import day.vitayuzu.neodb.ui.page.settings.SettingsViewModel
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import day.vitayuzu.neodb.util.AppNavigator
import day.vitayuzu.neodb.util.AppNavigator.Detail
import day.vitayuzu.neodb.util.AppNavigator.Home
import day.vitayuzu.neodb.util.AppNavigator.Library
import day.vitayuzu.neodb.util.AppNavigator.License
import day.vitayuzu.neodb.util.AppNavigator.Settings
import day.vitayuzu.neodb.util.AppNavigator.TopLevelDestination
import day.vitayuzu.neodb.util.LocalModalSheetController
import day.vitayuzu.neodb.util.LocalNavigator
import day.vitayuzu.neodb.util.ModalSheetController
import day.vitayuzu.neodb.util.ModalState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var neoDBRepository: NeoDBRepository

    @Inject lateinit var authRepository: AuthRepository

    @Inject lateinit var updateRepository: UpdateRepository

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
                CompositionLocalProvider(
                    LocalNavigator provides appNavigator,
                    LocalModalSheetController provides remember { ModalSheetController() },
                ) {
                    MainScaffold { keywords ->
                        // onSearch
                        neoDBRepository.searchWithKeyword(keywords).map { searchResult ->
                            searchResult.data.map { Entry(it) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MainScaffold(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Flow<List<Entry>> = { flowOf() },
) {
    val appNavigator = LocalNavigator.current
    Scaffold(
        modifier = modifier,
        topBar = { MainTopBar(onSearch) },
        floatingActionButton = { MainFAB(appNavigator) },
        bottomBar = { MainBottomBar() },
    ) {
        MainNavDisplay(insetsPaddingValues = it)
    }
}

@Composable
@Suppress("ktlint:compose:vm-injection-check")
private fun MainNavDisplay(insetsPaddingValues: PaddingValues, modifier: Modifier = Modifier) {
    val appNavigator = LocalNavigator.current
    val mainScreenModifier =
        Modifier.padding(insetsPaddingValues).consumeWindowInsets(insetsPaddingValues)
    // Hoist viewmodel for top level screens to avoid reconstruction.
    val homeViewModel: HomeViewModel = hiltViewModel()
    val libraryViewModel: LibraryViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val libraries by rememberLibraries(R.raw.aboutlibraries)
    NavDisplay(
        backStack = appNavigator.backStack,
        onBack = { appNavigator.back() },
        modifier = modifier,
        entryDecorators = listOf(
            rememberSceneSetupNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        transitionSpec = {
            if (appNavigator.animationDestination) {
                slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
            } else {
                slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
            }
        },
        predictivePopTransitionSpec = {
            fadeIn(tween(300)) togetherWith fadeOut(tween(300))
        },
        popTransitionSpec = {
            fadeIn(tween(300)) togetherWith fadeOut(tween(300))
        },
        entryProvider = entryProvider {
            entry<Home> {
                HomeScreen(mainScreenModifier, homeViewModel)
            }
            entry<Library> {
                LibraryPage(mainScreenModifier, libraryViewModel)
            }
            entry<Settings> {
                SettingsPage(mainScreenModifier, settingsViewModel)
            }
            entry<Detail> {
                DetailPage(it.type, it.uuid)
            }
            entry<License> {
                LibrariesContainer(libraries, contentPadding = insetsPaddingValues)
            }
        },
    )
}

@Composable
private fun MainFAB(appNavigator: AppNavigator, modifier: Modifier = Modifier) {
    AnimatedVisibility(appNavigator.current is Detail, modifier = modifier) {
        val modalSheetController = LocalModalSheetController.current
        FloatingActionButton(
            modifier = Modifier.animateEnterExit(),
            onClick = { modalSheetController.status = ModalState.NEW },
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add new mark for this entry",
            )
        }
    }
}

@Composable
fun MainBottomBar(modifier: Modifier = Modifier) {
    val appNavigator = LocalNavigator.current
    AnimatedVisibility(
        visible = appNavigator.current is TopLevelDestination,
        modifier = modifier,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        NavigationBar(Modifier.animateEnterExit()) {
            AppNavigator.TopLevelDestinations.forEach { destination ->
                NavigationBarItem(
                    icon = { Icon(destination.icon, null) },
                    label = { Text(stringResource(destination.name)) },
                    selected = appNavigator.current == destination,
                    onClick = { appNavigator goto destination },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopBar(onSearch: (String) -> Flow<List<Entry>> = { flowOf() }) {
    val scope = rememberCoroutineScope()
    val appNavigator = LocalNavigator.current
    if (appNavigator.current is TopLevelDestination) {
        val currentMainScreen = appNavigator.current as TopLevelDestination
        val searchBarState = rememberSearchBarState(SearchBarValue.Collapsed)
        Crossfade(searchBarState.currentValue) { it ->
            if (it == SearchBarValue.Expanded) {
                SearchModal(state = searchBarState, onSearch = onSearch)
            } else {
                TopAppBar(
                    modifier = Modifier.padding(top = 8.dp), // TopSearchBar has an extra padding
                    title = {
                        AnimatedContent(currentMainScreen) {
                            Text(stringResource(it.name))
                        }
                    },
                    actions = {
                        AnimatedContent(currentMainScreen) { mainScreen ->
                            when (mainScreen) {
                                // Show search button
                                Home, Library -> IconButton(
                                    onClick = {
                                        scope.launch { searchBarState.animateToExpanded() }
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                    )
                                }

                                else -> {}
                            }
                        }
                    },
                )
            }
        }
    }
}
