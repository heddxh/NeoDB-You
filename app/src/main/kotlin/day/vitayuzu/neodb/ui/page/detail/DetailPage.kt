package day.vitayuzu.neodb.ui.page.detail

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import day.vitayuzu.neodb.R
import day.vitayuzu.neodb.ui.component.StarsWithScores
import day.vitayuzu.neodb.ui.model.Detail
import day.vitayuzu.neodb.ui.model.Post
import day.vitayuzu.neodb.util.EntryType
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailPage(
    type: EntryType,
    uuid: String,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel =
        hiltViewModel<DetailViewModel, DetailViewModel.Factory> { it.create(type, uuid) },
) {
    val detailUiState by viewModel.detailUiState.collectAsStateWithLifecycle()
    val postUiState by viewModel.postUiState.collectAsStateWithLifecycle()

    Surface(modifier = modifier) {
        when (val state = detailUiState) { // Store in a temporary variable to enable smart cast
            is DetailUiState.Success -> {
                var showBottomSheet by remember { mutableStateOf(false) }
                // Background
                AsyncImage(
                    model = state.detail.coverUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alpha = 0.2f,
                    modifier = Modifier.fillMaxSize().blur(12.dp),
                )
                // Content
                DetailContent(
                    data = state.detail,
                    postList = postUiState.postList,
                    onClick = { showBottomSheet = true },
                )
                // Modal to show all detailed info
                if (showBottomSheet) {
                    ModalBottomSheet(onDismissRequest = { showBottomSheet = false }) {
                        ShowAllInfoModalContent(des = state.detail.des ?: "")
                    }
                }
            }

            else -> {}
        }
        if (detailUiState is DetailUiState.Loading ||
            postUiState.isLoading
        ) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun DetailContent(
    data: Detail,
    modifier: Modifier = Modifier,
    postList: List<Post> = emptyList(),
    onClick: () -> Unit = {},
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
                des = data.des ?: "",
                modifier = Modifier.fillMaxWidth().clickable(
                    // Using this overload to skip composition
                    interactionSource = null,
                    indication = LocalIndication.current,
                    onClick = onClick,
                ),
            )
        }

        // Review cards list
        items(
            items = postList,
            key = { it.hashCode() },
        ) {
            // TODO: show human readable date, see [Instant.toReadableString]
            PostCard(
                avatarUrl = it.avatar,
                username = it.username,
                content = it.content,
                rating = it.rating,
                date = it.date
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .date
                    .toString(),
            )
            HorizontalDivider(thickness = 0.2.dp)
        }
    }
}

@Composable
private fun DetailHeadingItem(
    title: String,
    coverUrl: String?,
    category: String,
    rating: Float?,
    info: String,
    des: String,
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
                .width(140.dp) // FIXME: some landscape or square cover looks ugly
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
            // Rating
            if (rating != null) StarsWithScores(rating)
            // Info
            Text(
                text = info,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            // Description
            Text(
                text = des,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ShowAllInfoModalContent(
    modifier: Modifier = Modifier,
    des: String = "",
) {
    // TODO: complete the modal, and refine the UI
    LazyColumn(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.detail_field_description),
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(Modifier.width(8.dp))
                Text(des)
            }
        }
    }
}

@Composable
private fun PostCard(
    avatarUrl: String?,
    username: String,
    content: String,
    rating: Int?,
    date: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Placeholder: https://mastodon.social/avatars/original/missing.png
        AsyncImage(
            model = avatarUrl,
            contentDescription = "Avatar of $username",
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.avatar_placeholder),
            fallback = painterResource(R.drawable.avatar_placeholder),
            error = painterResource(R.drawable.avatar_placeholder),
            modifier = Modifier.size(50.dp).clip(MaterialTheme.shapes.small),
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Username and rating stars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                // TODO: render markdown content in posts
                // TODO: collapsed/expand post
                // TODO: show user status, like playing or played, etc
                // FIXME: display name too long
                Text(
                    text = username,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (rating != null) StarsWithScores(rating = rating.toFloat(), showScores = false)
            }
            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = date,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.alpha(.5f).align(Alignment.End),
            )
        }
    }
}
