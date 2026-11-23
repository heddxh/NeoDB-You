package day.vitayuzu.neodb.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import day.vitayuzu.neodb.R
import day.vitayuzu.neodb.ui.model.Entry
import day.vitayuzu.neodb.ui.model.Mark
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import day.vitayuzu.neodb.ui.theme.kindColorPair
import day.vitayuzu.neodb.util.AppNavigator
import day.vitayuzu.neodb.util.LocalNavigator
import day.vitayuzu.neodb.util.sharedBoundsTransition

@Composable
fun EntryMarkCard(
    entry: Entry,
    mark: Mark?,
    modifier: Modifier = Modifier,
    trailingAction: (@Composable () -> Unit)? = null,
) {
    val appNavigator = LocalNavigator.current
    val kindColors = MaterialTheme.colorScheme.kindColorPair(entry.category)
    val coverShape = MaterialTheme.shapes.medium

    Card(
        modifier = modifier
            .fillMaxWidth()
            .sharedBoundsTransition(SharedEntryCardKey(entry.uuid)),
        shape = MaterialTheme.shapes.large,
        onClick = { appNavigator goto AppNavigator.Detail(entry.category, entry.uuid) },
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            AsyncImage(
                model = entry.coverUrl,
                contentDescription = entry.title,
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                modifier = Modifier
                    .width(92.dp)
                    .height(130.dp)
                    .clip(coverShape)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        shape = coverShape,
                    ),
                placeholder = painterResource(R.drawable.image_placeholder),
                fallback = painterResource(R.drawable.image_placeholder),
                error = painterResource(R.drawable.image_placeholder),
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Surface(
                        shape = CircleShape,
                        color = kindColors.container,
                        contentColor = kindColors.onContainer,
                    ) {
                        Text(
                            text = stringResource(entry.category.toR()),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        )
                    }
                    if (entry.rating != null) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            StarIcon(size = 14)
                            Text(
                                text = formatScore(entry.rating),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    trailingAction?.invoke()
                }
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (entry.des.isNotBlank()) {
                    Text(
                        text = entry.des,
                        maxLines = if (mark != null) 2 else 3,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (mark != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    UserMarkCard(
                        mark = mark,
                        compact = true,
                        enableBorder = false,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

data class SharedEntryCardKey(val uuid: String)

@Preview
@Composable
private fun PreviewEntryMarkCard() {
    NeoDBYouTheme {
        Surface(modifier = Modifier.padding(12.dp)) {
            EntryMarkCard(
                entry = Entry.TEST,
                mark = Mark.TEST,
            )
        }
    }
}
