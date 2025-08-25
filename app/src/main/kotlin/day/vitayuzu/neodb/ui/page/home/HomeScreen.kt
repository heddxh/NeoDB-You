package day.vitayuzu.neodb.ui.page.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
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
import day.vitayuzu.neodb.ui.model.Entry
import day.vitayuzu.neodb.util.EntryType

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onClickEntry: (EntryType, String) -> Unit = { _, _ -> },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // TODO: show fetched data immediately
    PullToRefreshBox(
        isRefreshing = uiState.isLoading,
        onRefresh = { viewModel.updateTrending() },
        modifier = modifier,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
        ) {
            val trendingModifier = Modifier.fillMaxWidth()
            // Book
            item {
                TrendingSection(
                    data = uiState.book,
                    type = EntryType.book,
                    modifier = trendingModifier,
                    onClickEntry = onClickEntry,
                )
            }
            // Game
            item {
                TrendingSection(
                    data = uiState.game,
                    type = EntryType.game,
                    modifier = trendingModifier,
                    onClickEntry = onClickEntry,
                )
            }
            // Movie
            item {
                TrendingSection(
                    data = uiState.movie,
                    type = EntryType.movie,
                    modifier = trendingModifier,
                    onClickEntry = onClickEntry,
                )
            }
            // TV
            item {
                TrendingSection(
                    data = uiState.tv,
                    type = EntryType.tv,
                    modifier = trendingModifier,
                    onClickEntry = onClickEntry,
                )
            }
            // Music
            item {
                TrendingSection(
                    data = uiState.music,
                    type = EntryType.music,
                    modifier = trendingModifier,
                    onClickEntry = onClickEntry,
                )
            }
            // Podcast
            item {
                TrendingSection(
                    data = uiState.podcast,
                    type = EntryType.podcast,
                    modifier = trendingModifier,
                    onClickEntry = onClickEntry,
                )
            }
        }
    }
}

@Composable
fun TrendingSection(
    data: List<Entry>,
    type: EntryType,
    modifier: Modifier = Modifier,
    onClickEntry: (type: EntryType, uuid: String) -> Unit = { _, _ -> },
) {
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
            Column {
                AsyncImage(
                    model = item.coverUrl,
                    contentDescription = item.title,
                    contentScale = ContentScale.FillHeight,
                    //                placeholder = painterResource(R.drawable.image_placeholder),
                    //                fallback = painterResource(R.drawable.image_placeholder),
                    //                error = painterResource(R.drawable.image_placeholder),
                    modifier = Modifier
                        .height(type.coverDimension.second.dp)
                        .clip(MaterialTheme.shapes.small)
                        .clickable { onClickEntry(type, item.uuid) },
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
