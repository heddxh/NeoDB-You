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
import day.vitayuzu.neodb.ui.model.Entry
import day.vitayuzu.neodb.ui.model.Mark
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import day.vitayuzu.neodb.util.ShelfType
import kotlinx.datetime.LocalDate

@Composable
fun UserMarkCard(mark: Mark, modifier: Modifier = Modifier) {
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
                Text(
                    text = mark.comment,
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Preview
@Composable
private fun UserMarkPreview() {
    val mark = Mark(
        entry = Entry.TEST,
        shelfType = ShelfType.complete,
        date = LocalDate(2023, 1, 1),
        rating = 7,
        comment = "This is a test comment.",
    )
    NeoDBYouTheme {
        Surface {
            UserMarkCard(mark = mark)
        }
    }
}
