package day.vitayuzu.neodb.ui.page.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import day.vitayuzu.neodb.ui.component.RemoteImage
import day.vitayuzu.neodb.ui.component.StarsWithScores
import day.vitayuzu.neodb.ui.model.Detail
import day.vitayuzu.neodb.util.EntryType

@Composable
fun DetailPage(
    type: EntryType,
    uuid: String,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel =
        hiltViewModel<DetailViewModel, DetailViewModel.Factory> { it.create(type, uuid) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) { // Store in a temporary variable to enable smart cast
        is DetailUiState.Success -> Box(modifier = modifier) {
            // Background
            AsyncImage(
                model = state.detail.coverUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.2f,
                modifier = Modifier.fillMaxSize().blur(12.dp),
            )
            // Content
            DetailContent(data = state.detail)
        }

        is DetailUiState.Loading -> LinearProgressIndicator(modifier = modifier.fillMaxWidth())
        else -> return
    }
}

@Composable
private fun DetailContent(
    data: Detail,
    modifier: Modifier = Modifier,
    reviewCards: LazyListScope.() -> Unit = {},
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            DetailHeadingItem(
                title = data.title,
                coverUrl = data.coverUrl,
                category = stringResource(data.type.toR()),
                rating = data.rating,
                info = data.info ?: "",
                modifier = Modifier.fillMaxWidth().padding(8.dp),
            )
        }

        // Description
        if (data.des != null) {
            item {
                Text(
                    text = data.des,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        // Review cards list
        reviewCards()
//        items(10) {
//            ReviewCard(
//                avatarUrl = "https://neodb.social/media/profile_images/2025/1/24/z9zuVzu-rBdbZmZUShr6KB2MQcw.jpg",
//                username = "维他柚子酒",
//                content = "我要吃炒面面包 Supporting line text lorem ipsum dolor sit amet, consectetur.",
//                rating = 4.5f,
//            )
//        }
    }
}

/**
 * @param rating Rating scores in 5 point system.
 */
@Composable
private fun DetailHeadingItem(
    title: String,
    coverUrl: String?,
    category: String,
    rating: Float?,
    info: String,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier,
    ) {
        AsyncImage(
            model = coverUrl,
            contentDescription = "Cover image of $title",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .width(140.dp) // FIXME: some landscape cover looks ugly
                .shadow(4.dp)
                .clip(MaterialTheme.shapes.small),
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            // Category
            Text(
                text = category,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.alpha(.5f),
            )
            if (rating != null) StarsWithScores(rating)
            Text(
                text = info,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ReviewCard(
    avatarUrl: String,
    username: String,
    content: String,
    rating: Float,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        RemoteImage(
            imageUrl = avatarUrl,
            contentDescription = "Avatar of $username",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(50.dp),
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Username and rating stars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = username,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                StarsWithScores(rating)
            }
            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
