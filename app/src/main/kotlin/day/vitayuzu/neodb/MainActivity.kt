package day.vitayuzu.neodb

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
                MainScaffold(
                    appNavigator = remember {
                        AppNavigator(
                            checkLogin = { authRepository.accountStatus.value.isLogin },
                            gotoLogin = {
                                this.startActivity(Intent(this, OauthActivity::class.java))
                            },
                        )
                    },
                ) { keywords ->
                    // onSearch
                    neoDBRepository.searchWithKeyword(keywords).map { searchResult ->
                        searchResult.data.map { Entry(it) }
                    }
                }
            }
        }
    }
}

@Composable
private fun MainScaffold(
    appNavigator: AppNavigator,
    modifier: Modifier = Modifier,
    onSearch: (String) -> Flow<List<Entry>> = { flowOf() },
) {
    val modalSheetController = remember { ModalSheetController() }

    CompositionLocalProvider(LocalModalSheetController provides modalSheetController) {
        Scaffold(
            modifier = modifier,
            //        topBar = {
            //            MainTopBar(currentMainScreen, onSearch) { type, uuid ->
            //                navController.navigate(Navi.Detail(type, uuid))
            //            }
            //        },
            floatingActionButton = {
                AnimatedVisibility(appNavigator.current is Detail) {
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
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = appNavigator.current is TopLevelDestination,
                    enter = expandVertically(),
                    exit = shrinkVertically(),
                ) {
                    NavigationBar(Modifier.animateEnterExit()) {
                        AppNavigator.TopLevelDestinations.forEach { destination ->
                            NavigationBarItem(
                                icon = { Icon(destination.icon, null) },
                                label = { Text(stringResource(destination.name)) },
                                selected = appNavigator.current == destination,
                                onClick = { appNavigator goTo destination },
                            )
                        }
                    }
                }
            },
        ) {
            MainNavDisplay(
                appNavigator = appNavigator,
                insetsPaddingValues = it,
            )
        }
    }
}

@Composable
@Suppress("ktlint:compose:vm-injection-check")
fun MainNavDisplay(
    appNavigator: AppNavigator,
    insetsPaddingValues: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val mainScreenModifier =
        Modifier.padding(insetsPaddingValues).consumeWindowInsets(insetsPaddingValues)
    // Hoist viewmodel for top level screens to avoid reconstruction.
    val homeViewModel: HomeViewModel = hiltViewModel()
    val libraryViewModel: LibraryViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    NavDisplay(
        backStack = appNavigator.backStack,
        modifier = modifier,
        entryDecorators = listOf(
            rememberSceneSetupNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<Home> {
                HomeScreen(mainScreenModifier, homeViewModel) { type, uuid ->
                    appNavigator goTo Detail(type, uuid)
                }
            }
            entry<Library> {
                LibraryPage(mainScreenModifier, libraryViewModel) { type, uuid ->
                    appNavigator goTo Detail(type, uuid)
                }
            }
            entry<Settings> {
                SettingsPage(mainScreenModifier, settingsViewModel) {
                    appNavigator goTo License
                }
            }
            entry<Detail> { (type, uuid) ->
                DetailPage(
                    type = type,
                    uuid = uuid,
                )
            }
            entry<License> {
                val libraries by rememberLibraries(R.raw.aboutlibraries)
                LibrariesContainer(libraries, contentPadding = insetsPaddingValues)
            }
        },
    )
}

// @OptIn(ExperimentalMaterial3Api::class)
// @Composable
// private fun MainTopBar(
//    currentMainScreen: MainScreen<out Navi>?,
//    onSearch: (String) -> Flow<List<Entry>> = { flowOf() },
//    onClickEntry: (EntryType, String) -> Unit = { _, _ -> },
// ) {
//    val scope = rememberCoroutineScope()
//    if (currentMainScreen != null) {
//        val searchBarState = rememberSearchBarState(SearchBarValue.Collapsed)
//        Crossfade(searchBarState.currentValue) {
//            if (it == SearchBarValue.Expanded) {
//                SearchModal(
//                    state = searchBarState,
//                    onSearch = onSearch,
//                    onClickEntry = onClickEntry,
//                )
//            } else {
//                TopAppBar(
//                    modifier = Modifier.padding(top = 8.dp), // TopSearchBar has an extra padding
//                    title = { Text(stringResource(currentMainScreen.name)) },
//                    actions = {
//                        when (currentMainScreen.nav) {
//                            // Show search button
//                            Home, Library -> IconButton(
//                                onClick = {
//                                    scope.launch { searchBarState.animateToExpanded() }
//                                },
//                            ) {
//                                Icon(
//                                    imageVector = Icons.Default.Search,
//                                    contentDescription = null,
//                                )
//                            }
//
//                            else -> {}
//                        }
//                    },
//                )
//            }
//        }
//    }
// }
