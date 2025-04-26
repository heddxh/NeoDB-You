package day.vitayuzu.neodb.ui.page.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.CarouselDefaults
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import day.vitayuzu.neodb.ui.model.Entry
import day.vitayuzu.neodb.util.EntryType

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val numberOfEachTrending = 8

    // HACK: https://issuetracker.google.com/issues/362137847#comment5
    // Wait until data is loaded
    if (!uiState.isLoading) {
        val trendingModifier = Modifier.fillMaxWidth()
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
        ) {
            // Book
            item {
                TrendingSection(
                    data = uiState.book.take(numberOfEachTrending),
                    type = EntryType.book,
                    modifier = trendingModifier,
                )
            }
            // Game
            item {
                TrendingSection(
                    data = uiState.game.take(numberOfEachTrending),
                    type = EntryType.game,
                    modifier = trendingModifier,
                )
            }
            // Movie
            item {
                TrendingSection(
                    data = uiState.movie.take(numberOfEachTrending),
                    type = EntryType.movie,
                    modifier = trendingModifier,
                )
            }
            // TV
            item {
                TrendingSection(
                    data = uiState.tv.take(numberOfEachTrending),
                    type = EntryType.tv,
                    modifier = trendingModifier,
                )
            }
            // Music
            item {
                TrendingSection(
                    data = uiState.music.take(numberOfEachTrending),
                    type = EntryType.music,
                    modifier = trendingModifier,
                )
            }
            // Podcast
            item {
                TrendingSection(
                    data = uiState.podcast.take(numberOfEachTrending),
                    type = EntryType.podcast,
                    modifier = trendingModifier,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendingSection(
    data: List<Entry>,
    type: EntryType,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        val carouselState = rememberCarouselState { data.size }
        Text(
            text = "Trending ${stringResource(type.toR())}", // TODO: i18n "Trending"
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 8.dp),
        )
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalUncontainedCarousel(
            state = carouselState,
            itemSpacing = 8.dp, // NOTE: I think the library's implementation is wrong, but leave it
            itemWidth = type.coverDimension.first.dp,
            contentPadding = PaddingValues(horizontal = 8.dp),
            flingBehavior = CarouselDefaults.singleAdvanceFlingBehavior(carouselState),
        ) {
            val item = data[it]
            AsyncImage(
                model = item.coverUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(type.coverDimension.second.dp)
                    .maskClip(MaterialTheme.shapes.small),
            )
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
