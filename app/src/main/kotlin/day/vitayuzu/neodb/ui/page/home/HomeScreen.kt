package day.vitayuzu.neodb.ui.page.home

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import day.vitayuzu.neodb.ui.component.SharedBottomBar
import day.vitayuzu.neodb.ui.component.SharedFab
import day.vitayuzu.neodb.ui.model.Entry
import day.vitayuzu.neodb.util.AppNavigator
import day.vitayuzu.neodb.util.EntryType
import day.vitayuzu.neodb.util.LocalNavigator
import day.vitayuzu.neodb.util.sharedBoundsTransition

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier, viewModel: HomeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appNavigator = LocalNavigator.current

    // TODO: show fetched data immediately
    Scaffold(
        modifier = modifier,
        bottomBar = { SharedBottomBar() },
        floatingActionButton = {
            SharedFab(onClick = { appNavigator goto AppNavigator.Search }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Perform search",
                )
            }
        },
    ) {
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.updateTrending() },
            modifier = Modifier.padding(it).consumeWindowInsets(it),
        ) {
            Column(
                // PullToRefresh relies on child scroll event to detect scroll gestures,
                // so make sure child composable has enough height.
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
            ) {
                // Book
                TrendingSection(
                    data = uiState.book,
                    type = EntryType.book,
                )
                // Game
                TrendingSection(
                    data = uiState.game,
                    type = EntryType.game,
                )
                // Movie
                TrendingSection(
                    data = uiState.movie,
                    type = EntryType.movie,
                )
                // TV
                TrendingSection(
                    data = uiState.tv,
                    type = EntryType.tv,
                )
                // Music
                TrendingSection(
                    data = uiState.music,
                    type = EntryType.music,
                )
                // Podcast
                TrendingSection(
                    data = uiState.podcast,
                    type = EntryType.podcast,
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TrendingSection(
    data: List<Entry>,
    type: EntryType,
    modifier: Modifier = Modifier,
) {
    val appNavigator = LocalNavigator.current
    if (data.isEmpty()) return // return in advance if no data
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = stringResource(type.toR()),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 8.dp).alpha(.8f),
        )
        Spacer(modifier = Modifier.height(4.dp))

        val pagerState = rememberPagerState { data.size }
        val flingBehavior = PagerDefaults.flingBehavior(
            state = pagerState,
            pagerSnapDistance = PagerSnapDistance.atMost(3),
        )
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 8.dp),
            pageSize = PageSize.Fixed(type.coverDimension.first.dp),
            pageSpacing = 8.dp,
            flingBehavior = flingBehavior,
            key = { data[it].uuid },
        ) {
            val item = data[it]
            Column(modifier = Modifier.sharedBoundsTransition(item.uuid)) {
                AsyncImage(
                    model = item.coverUrl,
                    contentDescription = item.title,
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .height(type.coverDimension.second.dp)
                        .clip(MaterialTheme.shapes.small)
                        .clickable { appNavigator goto AppNavigator.Detail(type, item.uuid) },
                )
                // FIXME: may show 2 lines at most without mess the layout
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.alpha(.6f),
                )
            }
        }
    }
}

/**
 * Dimensions of cover for different types of entries.
 * @return Pair of (width, height)
 */
val EntryType.coverDimension: Pair<Int, Int>
    get() = when (this) {
        // Octavo size(229 x 152), commonly used for hardbacks.
        // See: https://en.wikipedia.org/wiki/Book_size#United_States
        EntryType.book -> (100 to 151)
        // Copy from one of switch game cover on NeoDB(268 x 434), sorry...
        EntryType.game -> (100 to 162)
        // One Sheet size in US.
        // See: https://en.wikipedia.org/wiki/Film_poster#United_States
        EntryType.movie -> (100 to 148)
        // Album size
        // See: https://en.wikipedia.org/wiki/Album_cover#Album_covers_in_the_age_of_downloads_and_streaming
        EntryType.music -> (100 to 100)
        // From the TVDB
        EntryType.tv -> (100 to 147)
        else -> (100 to 100)
    }
