package day.vitayuzu.neodb.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import day.vitayuzu.neodb.ui.model.Mark
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import day.vitayuzu.neodb.ui.theme.statusColor
import day.vitayuzu.neodb.util.ShelfType

@Composable
fun UserMarkCard(
    mark: Mark,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    enableShadow: Boolean = false,
    enableBorder: Boolean = true,
) {
    val hasRating = mark.rating != null
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
        CardDefaults.cardElevation(defaultElevation = 8.dp)
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
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    ShelfTypeLabel(shelfType = mark.shelfType)

                    if (hasRating) {
                        RatingSection(mark = mark, showNumber = !compact)
                    }

                    if (!compact) {
                        Text(
                            text = mark.date.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f),
                            modifier = Modifier.padding(vertical = 6.dp),
                        )
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
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        tonalElevation = 8.dp,
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RatingStars(
                full = mark.fullStars,
                half = mark.hasHalfStar,
                size = 16,
                space = 1,
            )
            if (showNumber) {
                Text(
                    text = String.format("%.1f", mark.rating!! / 2f),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
private fun ShelfTypeLabel(shelfType: ShelfType, modifier: Modifier = Modifier) {
    val containerColor = MaterialTheme.colorScheme.statusColor(shelfType)

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = containerColor,
        shadowElevation = 4.dp,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color.White),
            )
            Text(
                text = stringResource(shelfType.toR()),
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
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
