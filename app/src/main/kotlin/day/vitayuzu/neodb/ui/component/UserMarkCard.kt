package day.vitayuzu.neodb.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import day.vitayuzu.neodb.ui.model.Mark
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import day.vitayuzu.neodb.ui.theme.statusColorPair
import day.vitayuzu.neodb.util.ShelfType

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserMarkCard(
    mark: Mark,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    enableShadow: Boolean = false,
    enableBorder: Boolean = true,
) {
    val hasRating = mark.rating != null && mark.rating > 0
    val hasComment = !mark.comment.isNullOrBlank()

    val border = if (enableBorder) {
        BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f),
        )
    } else {
        BorderStroke(0.dp, Color.Transparent)
    }

    val shadow = if (enableShadow) {
        CardDefaults.cardElevation(defaultElevation = 4.dp)
    } else {
        CardDefaults.cardElevation()
    }

    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
        border = border,
        elevation = shadow,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    ) {
        Column(
            modifier = Modifier.padding(if (compact) 8.dp else 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ShelfTypeLabel(shelfType = mark.shelfType, compact = compact)

                if (hasRating) {
                    RatingSection(mark = mark, showNumber = !compact)
                }

                Spacer(modifier = Modifier.weight(1f))

                if (!compact) {
                    Text(
                        text = mark.date.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (!compact && mark.tags.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    mark.tags.forEach { tag ->
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        ) {
                            Text(
                                text = "#$tag",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            )
                        }
                    }
                }
            }

            if (hasComment && !compact) {
                val comment = mark.comment.trim()

                CommentSection(Modifier.fillMaxWidth()) {
                    ExpandableText(
                        text = comment,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 4,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun RatingSection(
    mark: Mark,
    modifier: Modifier = Modifier,
    showNumber: Boolean = true,
) {
    val rating = mark.rating ?: return
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
    ) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RatingStars(
                full = mark.fullStars,
                half = mark.hasHalfStar,
                size = 14,
                space = 1,
            )
            if (showNumber) {
                Text(
                    text = formatScore(rating),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun CommentSection(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        content = content,
    )
}

@Composable
fun ShelfTypeLabel(
    shelfType: ShelfType,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val pair = MaterialTheme.colorScheme.statusColorPair(shelfType)

    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = pair.container,
        contentColor = pair.onContainer,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(
                horizontal = if (compact) 8.dp else 10.dp,
                vertical = if (compact) 4.dp else 6.dp,
            ),
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(pair.dot),
            )
            Text(
                text = stringResource(shelfType.toR()),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun UserMarkPreview() {
    NeoDBYouTheme {
        Surface(modifier = Modifier.padding(12.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                UserMarkCard(mark = Mark.TEST)
                Spacer(Modifier.height(2.dp))
                UserMarkCard(mark = Mark.TEST.copy(comment = null))
                Spacer(Modifier.height(2.dp))
                UserMarkCard(mark = Mark.TEST.copy(rating = null))
                Spacer(Modifier.height(2.dp))
                UserMarkCard(mark = Mark.TEST.copy(rating = null, comment = null))
                Spacer(Modifier.height(2.dp))
                UserMarkCard(mark = Mark.TEST, compact = true)
            }
        }
    }
}
