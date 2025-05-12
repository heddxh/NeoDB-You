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
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        repeat(full) {
            StarIcon()
        }
        if (half) {
            StarIcon(kind = Star.HALF)
        }
        repeat(empty) {
            StarIcon(kind = Star.EMPTY)
        }
    }
}

@Composable
fun StarIcon(
    modifier: Modifier = Modifier,
    kind: Star = Star.FULL,
) {
    when (kind) {
        Star.FULL -> Icon(
            Icons.Filled.Star,
            tint = MaterialTheme.colorScheme.ratingColor,
            contentDescription = null,
            modifier = modifier.size(16.dp).offset(y = (-0.5).dp), // Visually center
        )

        Star.HALF -> Icon(
            painterResource(R.drawable.star_half),
            tint = MaterialTheme.colorScheme.ratingColor,
            contentDescription = null,
            modifier = modifier.size(16.dp).offset(y = (-0.5).dp), // Visually center
        )

        Star.EMPTY -> Icon(
            painterResource(R.drawable.star_empty),
            tint = MaterialTheme.colorScheme.ratingColor,
            contentDescription = null,
            modifier = modifier.size(16.dp).offset(y = (-0.5).dp), // Visually center
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
