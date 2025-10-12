package day.vitayuzu.neodb.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import day.vitayuzu.neodb.R
import day.vitayuzu.neodb.ui.theme.ratingColor

/**
 * @param rating rating scores in 10 system
 */
@Composable
fun StarsWithScores(
    rating: Float,
    modifier: Modifier = Modifier,
    showScores: Boolean = true,
    size: Int = 16,
    starSpace: Int = 2,
) {
    val ratingInFive = rating.div(2)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RatingStars(
            full = ratingInFive.toInt(),
            half = ratingInFive > ratingInFive.toInt(),
            size = size,
            space = starSpace,
        )
        if (showScores) {
            Text(
                text = "%.1f".format(rating),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.ratingColor,
            )
        }
    }
}

@Composable
fun RatingStars(
    full: Int,
    half: Boolean,
    modifier: Modifier = Modifier,
    empty: Int = 5 - full - if (half) 1 else 0,
    size: Int = 16,
    space: Int = 2,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        // FIXME: remove or correctly calculate the offset according to the size
        repeat(full) {
            StarIcon(size = size, modifier = Modifier.offset(y = (-0.5).dp)) // Visually center
        }
        if (half) {
            StarIcon(size = size, kind = Star.HALF, modifier = Modifier.offset(y = (-0.5).dp))
        }
        repeat(empty) {
            StarIcon(size = size, kind = Star.EMPTY, modifier = Modifier.offset(y = (-0.5).dp))
        }
    }
}

@Composable
fun StarIcon(
    modifier: Modifier = Modifier,
    kind: Star = Star.FULL,
    size: Int = 16,
) {
    when (kind) {
        Star.FULL -> Icon(
            Icons.Filled.Star,
            tint = MaterialTheme.colorScheme.ratingColor,
            contentDescription = null,
            modifier = modifier.size(size.dp),
        )

        Star.HALF -> Icon(
            painterResource(R.drawable.star_half),
            tint = MaterialTheme.colorScheme.ratingColor,
            contentDescription = null,
            modifier = modifier.size(size.dp),
        )

        Star.EMPTY -> Icon(
            painterResource(R.drawable.star_empty),
            tint = MaterialTheme.colorScheme.ratingColor,
            contentDescription = null,
            modifier = modifier.size(size.dp),
        )
    }
}

enum class Star {
    FULL,
    HALF,
    EMPTY,
}

@Preview
@Composable
private fun StarsWithScoresPreview() {
    StarsWithScores(rating = 6.8f)
}
