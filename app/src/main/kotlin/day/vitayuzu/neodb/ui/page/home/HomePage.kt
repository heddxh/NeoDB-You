package day.vitayuzu.neodb.ui.page.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import day.vitayuzu.neodb.ui.component.EntryTypeText
import day.vitayuzu.neodb.ui.component.SharedSearchFab
import day.vitayuzu.neodb.ui.component.StatusBarProtection
import day.vitayuzu.neodb.ui.model.Entry
import day.vitayuzu.neodb.util.AppNavigator
import day.vitayuzu.neodb.util.EntryType
import day.vitayuzu.neodb.util.LocalNavigator
import day.vitayuzu.neodb.util.sharedBoundsTransition

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    sharedBottomBar: @Composable () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier,
        bottomBar = sharedBottomBar,
        floatingActionButton = { SharedSearchFab() },
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.updateTrending() },
            modifier = Modifier.padding(padding).consumeWindowInsets(padding).fillMaxSize(),
        ) {
            // PullToRefresh relies on child scroll event to detect scroll gestures,
            // so make sure child composable has enough height.
            Box(Modifier.fillMaxSize().verticalScroll(scrollState)) {
                AnimatedVisibility(
                    visible = !uiState.isLoading,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top)) {
                        uiState.data.forEach { (type, data) ->
                            TrendingSection(data, type)
                        }
                    }
                }
            }
        }
    }

    StatusBarProtection(scrollState)
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TrendingSection(
    entries: List<Entry>,
    type: EntryType,
    modifier: Modifier = Modifier,
) {
    val appNavigator = LocalNavigator.current
    Column(modifier = modifier) {
        ProvideTextStyle(MaterialTheme.typography.titleLarge) {
            EntryTypeText(type, Modifier.padding(horizontal = 8.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))

        // WORKAROUND: Make all items in pager with different lines of text have the same height
        var pagerItemHeight by remember { mutableIntStateOf(0) }

        val pagerState = rememberPagerState { entries.size }
        val flingBehavior = PagerDefaults.flingBehavior(
            state = pagerState,
            pagerSnapDistance = PagerSnapDistance.atMost(3),
        )
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 8.dp),
            pageSize = PageSize.Fixed(type.coverDimension.first.dp),
            pageSpacing = 8.dp,
            verticalAlignment = Alignment.Top,
            flingBehavior = flingBehavior,
            modifier = Modifier.height(
                if (pagerItemHeight > 0) {
                    with(LocalDensity.current) { pagerItemHeight.toDp() }
                } else {
                    Dp.Unspecified
                },
            ),
        ) {
            val item = entries[it]
            Column(
                modifier = Modifier
                    .sharedBoundsTransition(
                        SharedTrendingItemKey(item.uuid),
                    ).onSizeChanged {
                        if (it.height > pagerItemHeight) pagerItemHeight = it.height
                    },
            ) {
                AsyncImage(
                    model = item.coverUrl,
                    contentDescription = item.title,
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .height(type.coverDimension.second.dp)
                        .clip(MaterialTheme.shapes.small)
                        .clickable { appNavigator goto AppNavigator.Detail(type, item.uuid) },
                )
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.alpha(.6f),
                )
            }
        }
    }
}

data class SharedTrendingItemKey(val uuid: String)

@Preview
@Composable
private fun PreviewTrendingSection() {
    val data = buildList {
        repeat(10) { add(Entry.TEST) }
    }
    val type = EntryType.movie
    TrendingSection(
        entries = data,
        type = type,
    )
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
