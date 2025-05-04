package day.vitayuzu.neodb.ui.page.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import day.vitayuzu.neodb.data.schema.detail.DetailSchema
import day.vitayuzu.neodb.ui.component.RemoteImage
import day.vitayuzu.neodb.ui.component.StarsWithScores
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

    when (val state = uiState) {
        is DetailUiState.Success -> Box(modifier = modifier) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    DetailHeadingItem(
                        title = state.detail.title,
                        coverUrl = state.detail.coverImageUrl,
                        category = "游戏",
                        rating = state.detail.rating,
                        info = "PS4 / Nintendo Switch / PS5 / 文字冒险 / 策略 / Vanillaware Ltd. / Atlus Co., Ltd. / 2019年11月28日",
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                    )
                }

                // Description
                val des = state.detail.description
                if (des != null) {
                    item {
                        Text(
                            text = des,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 6,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                // Review cards list
                items(10) {
                    ReviewCard(
                        avatarUrl = "https://neodb.social/media/profile_images/2025/1/24/z9zuVzu-rBdbZmZUShr6KB2MQcw.jpg",
                        username = "维他柚子酒",
                        content = "我要吃炒面面包 Supporting line text lorem ipsum dolor sit amet, consectetur.",
                        rating = 4.5f,
                    )
                }
            }
            // Background
            AsyncImage(
                model = state.detail.coverImageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.2f,
                modifier = Modifier.fillMaxSize().blur(12.dp),
            )
        }

        else -> return
    }
}

@Composable
private fun DetailContent(detail: DetailSchema) {
}

/**
 * @param rating Rating scores in 5 point system.
 */
@Composable
fun DetailHeadingItem(
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
        RemoteImage(
            imageUrl = coverUrl,
            contentDescription = "Cover image of $title",
            modifier = Modifier
                .height(120.dp)
                .width(100.dp)
                .shadow(4.dp)
                .clip(MaterialTheme.shapes.small),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
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
fun ReviewCard(
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
