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
import androidx.compose.ui.unit.dp
import day.vitayuzu.neodb.R
import day.vitayuzu.neodb.ui.theme.ratingColor

/**
 * @param rating rating scores in 5 system
 */
@Composable
fun StarsWithScores(
    rating: Float,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RatingStars(
            full = rating.toInt(),
            half = rating > rating.toInt(),
        )
        Text(
            text = "%.1f".format(rating),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.ratingColor,
        )
    }
}

@Composable
fun RatingStars(
    full: Int,
    half: Boolean,
    modifier: Modifier = Modifier,
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
            StarIcon(isHalf = true)
        }
    }
}

@Composable
fun StarIcon(
    modifier: Modifier = Modifier,
    isHalf: Boolean = false,
) {
    if (isHalf) {
        Icon(
            painterResource(R.drawable.star_half),
            tint = MaterialTheme.colorScheme.ratingColor,
            contentDescription = null,
            modifier = modifier.size(16.dp).offset(y = (-0.5).dp), // Visually center
        )
    } else {
        Icon(
            Icons.Filled.Star,
            tint = MaterialTheme.colorScheme.ratingColor,
            contentDescription = null,
            modifier = modifier.size(16.dp).offset(y = (-0.5).dp), // Visually center
        )
    }
}
