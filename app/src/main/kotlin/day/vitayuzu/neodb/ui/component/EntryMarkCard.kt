package day.vitayuzu.neodb.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import day.vitayuzu.neodb.R
import day.vitayuzu.neodb.ui.model.Entry
import day.vitayuzu.neodb.ui.model.Mark
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import day.vitayuzu.neodb.util.AppNavigator
import day.vitayuzu.neodb.util.LocalNavigator

@Composable
fun EntryMarkCard(
    entry: Entry,
    mark: Mark?,
    modifier: Modifier = Modifier,
) {
    val appNavigator = LocalNavigator.current
    Card(
        modifier = modifier,
        onClick = { appNavigator goto AppNavigator.Detail(entry.category, entry.uuid) },
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onBackground,
            containerColor = MaterialTheme.colorScheme.background,
        ),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(160.dp).padding(8.dp),
        ) {
            AsyncImage(
                model = entry.coverUrl,
                contentDescription = "Cover image of ${entry.title}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.width(114.dp),
                alignment = Alignment.CenterStart,
                placeholder = painterResource(R.drawable.image_placeholder),
                fallback = painterResource(R.drawable.image_placeholder),
                error = painterResource(R.drawable.image_placeholder),
            )

            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxSize(),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = entry.title,
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(entry.category.toR()),
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.alpha(0.5f),
                        )
                    }
                    if (entry.rating != null) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            StarIcon()
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                entry.rating.toString(),
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                // Description
                Text(
                    text = entry.des,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(modifier = Modifier.height(4.dp))
                // User mark card
                if (mark != null) UserMarkCard(mark, expandable = false)
            }
        }
    }
}

@Preview
@Composable
private fun PreviewEntryMarkCard() {
    NeoDBYouTheme {
        Surface {
            EntryMarkCard(
                entry = Entry.TEST,
                mark = Mark.TEST,
            )
        }
    }
}
