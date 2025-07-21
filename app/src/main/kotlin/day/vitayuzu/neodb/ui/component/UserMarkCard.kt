package day.vitayuzu.neodb.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import day.vitayuzu.neodb.ui.model.Mark
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme

@Composable
fun UserMarkCard(
    mark: Mark,
    modifier: Modifier = Modifier,
    expandable: Boolean = true,
) {
    OutlinedCard(
        shape = MaterialTheme.shapes.small,
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(horizontal = 8.dp).padding(bottom = 8.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().paddingFromBaseline(top = 20.5.dp),
            ) {
                // Rating stars
                if (mark.rating != null) {
                    RatingStars(mark.fullStars, mark.hasHalfStar)
                }
                // Date and shelf type/status
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        mark.date.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.alignByBaseline(),
                    )
                    Text(
                        stringResource(mark.shelfType.toR()),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.alignByBaseline(),
                    )
                }
            }
            // Comment
            if (mark.comment != null) {
                if (expandable) {
                    ExpandableText(
                        text = mark.comment,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 3,
                    )
                } else {
                    Text(
                        mark.comment,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun UserMarkPreview() {
    NeoDBYouTheme {
        Surface {
            UserMarkCard(mark = Mark.TEST)
        }
    }
}
