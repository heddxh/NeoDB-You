package day.vitayuzu.neodb.ui.page.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

    // HACK: https://issuetracker.google.com/issues/362137847#comment5
    // Wait until data is loaded
    if (!uiState.isLoading) {
        TrendingSection(
            data = uiState.book.take(8),
            type = EntryType.book,
            modifier = modifier.fillMaxSize().padding(8.dp),
        )
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
        )
        // Octavo size(229 x 152), commonly used for hardbacks.
        // See: https://en.wikipedia.org/wiki/Book_size#United_States
        HorizontalUncontainedCarousel(
            state = carouselState,
            itemSpacing = 8.dp, // NOTE: I think the library implementation is wrong, but leave it.
            itemWidth = 152.dp,
            flingBehavior = CarouselDefaults.singleAdvanceFlingBehavior(carouselState),
        ) {
            val item = data[it]
            AsyncImage(
                model = item.coverUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.height(229.dp).maskClip(MaterialTheme.shapes.medium),
            )
        }
    }
}
