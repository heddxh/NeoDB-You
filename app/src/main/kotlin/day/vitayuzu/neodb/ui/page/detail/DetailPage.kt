package day.vitayuzu.neodb.ui.page.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import day.vitayuzu.neodb.R
import day.vitayuzu.neodb.ui.component.ExpandableText
import day.vitayuzu.neodb.ui.component.StarsWithScores
import day.vitayuzu.neodb.ui.model.Detail
import day.vitayuzu.neodb.ui.model.Post
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import day.vitayuzu.neodb.util.EntryType
import day.vitayuzu.neodb.util.toDateString
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun DetailPage(
    type: EntryType,
    uuid: String,
    modifier: Modifier = Modifier,
    showComposeModal: Boolean = false,
    onDismissComposeModal: () -> Unit = {},
    viewModel: DetailViewModel =
        hiltViewModel<DetailViewModel, DetailViewModel.Factory> { it.create(type, uuid) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(modifier = modifier.fillMaxSize()) {
        Box {
            // Date Picker
            var isShowDatePicker by remember { mutableStateOf(false) }
            val currentTimeLong = Clock.System.now().toEpochMilliseconds()
            var postDate by remember { mutableLongStateOf(currentTimeLong) }

            AnimatedVisibility(isShowDatePicker) {
                DatePickerModal(
                    onConfirm = { postDate = it ?: currentTimeLong },
                    onDismiss = { isShowDatePicker = false },
                )
            }

            // Main UI with background image.
            AnimatedVisibility(uiState.detail != null, enter = fadeIn()) {
                val detail = uiState.detail!!
                var showDesModal by remember { mutableStateOf(false) }
                // Background
                AsyncImage(
                    model = detail.coverUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alpha = 0.2f,
                    modifier = Modifier.fillMaxSize().blur(12.dp),
                )

                // Content
                var isShowAllReviews by remember { mutableStateOf(false) }
                DetailContent(
                    data = detail,
                    postList = uiState.postList.take(if (isShowAllReviews) Int.MAX_VALUE else 5),
                    onClick = { showDesModal = true },
                    hasMore = uiState.hasMorePost && !uiState.isLoadingPost,
                    showMore = {
                        isShowAllReviews = true
                        viewModel.refreshPosts(Int.MAX_VALUE)
                    },
                )

                // Modal
                // FIXME: move FAB logic here instead of in MainActivity.
                if (showComposeModal) { // Compose modal
                    PostComposeModal(
                        postDate = Instant.fromEpochMilliseconds(postDate),
                        onDismiss = onDismissComposeModal,
                        onSend = {
                            viewModel.postMark(it)
                            onDismissComposeModal()
                        },
                        onShowDatePicker = { isShowDatePicker = true },
                    )
                } else if (showDesModal) { // Modal showing all detailed info.
                    // TODO: show other field.
                    ModalBottomSheet(onDismissRequest = { showDesModal = false }) {
                        ShowAllInfoModalContent(des = detail.des ?: "")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun DetailContent(
    data: Detail,
    modifier: Modifier = Modifier,
    postList: List<Post> = emptyList(),
    onClick: () -> Unit = {},
    hasMore: Boolean = false,
    showMore: () -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .consumeWindowInsets(WindowInsets.statusBars),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = WindowInsets.statusBars.asPaddingValues(),
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
                date = it.date.toDateString(),
            )
            HorizontalDivider(thickness = 0.2.dp)
        }

        // See all reviews
        item {
            AnimatedVisibility(hasMore, enter = fadeIn()) {
                ElevatedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(WindowInsets.navigationBars.asPaddingValues()),
                    onClick = showMore,
                ) { Text(stringResource(R.string.detail_show_all_reviews)) }
            }
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
private fun ShowAllInfoModalContent(modifier: Modifier = Modifier, des: String = "") {
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
                // TODO: show user status, like playing or played, etc
                Text(
                    text = username,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (rating != null) {
                    StarsWithScores(
                        rating = rating.toFloat(),
                        showScores = false,
                    )
                }
            }
            Card(shape = MaterialTheme.shapes.extraSmall) {
                ExpandableText(
                    text = content,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    overlayColor = CardDefaults.cardColors().containerColor,
                )
            }
            Text(
                text = date,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.alpha(.5f).align(Alignment.End),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
private fun DatePickerModal(
    date: Instant = Clock.System.now(),
    onConfirm: (Long?) -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    val datePickerState =
        rememberDatePickerState(initialSelectedDateMillis = date.toEpochMilliseconds())

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onConfirm(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    ) {
        DatePicker(state = datePickerState, showModeToggle = false)
    }
}

@Suppress("ktlint:standard:max-line-length")
@OptIn(ExperimentalTime::class)
@Preview
@Composable
private fun DetailContentPreview() {
    val data = Detail(
        type = EntryType.movie,
        title = "The Shawshank Redemption",
        coverUrl = null,
        rating = 4.8f,
        info = "Drama / Crime",
        des = "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
    )
    val postList = listOf(
        Post(
            avatar = "https://example.com/avatar1.jpg",
            username = "User1",
            content = "Great movie!",
            rating = 5,
            date = Instant.parse("2023-10-27T12:00:00Z"),
        ),
        Post(
            avatar = "https://example.com/avatar2.jpg",
            username = "User2",
            content = "A classic.",
            rating = 4,
            date = Instant.parse("2024-10-27T12:00:00Z"),
        ),
    )
    NeoDBYouTheme {
        Surface {
            DetailContent(data = data, postList = postList, hasMore = true)
        }
    }
}

@Suppress("ktlint:standard:max-line-length")
@PreviewLightDark
@Composable
private fun PostCardPreview() {
    NeoDBYouTheme {
        Surface {
            PostCard(
                avatarUrl = null,
                username = "John Doe",
                content = "This is a sample post content. It can be a long text that needs to be truncated.This is a sample post content. It can be a long text that needs to be truncated.This is a sample post content. It can be a long text that needs to be truncated.",
                rating = 4,
                date = "2023-10-27",
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
