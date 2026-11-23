package day.vitayuzu.neodb.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import day.vitayuzu.neodb.R
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import day.vitayuzu.neodb.ui.theme.ratingColor
import java.util.Locale

fun formatScore(rating: Float): String = String.format(Locale.US, "%.1f", rating)

fun formatScore(rating: Int): String = String.format(Locale.US, "%.1f", rating.toFloat())

/**
 * @param rating rating scores in 10 point system
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
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RatingStars(
            full = ratingInFive.toInt(),
            half = ratingInFive - ratingInFive.toInt() >= 0.5f,
            size = size,
            space = starSpace,
        )
        if (showScores) {
            Text(
                text = formatScore(rating),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.ratingColor,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CookieRatingBadge(
    rating: Float,
    modifier: Modifier = Modifier,
    size: Dp = 46.dp,
) {
    Surface(
        modifier = modifier.size(size),
        shape = MaterialShapes.Cookie9Sided.toShape(),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = formatScore(rating),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
fun RatingStars(
    full: Int,
    half: Boolean,
    modifier: Modifier = Modifier,
    empty: Int = (5 - full - if (half) 1 else 0).coerceAtLeast(0),
    size: Int = 16,
    space: Int = 2,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        repeat(full.coerceIn(0, 5)) {
            StarIcon(size = size, modifier = Modifier.offset(y = (-0.5).dp))
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
    NeoDBYouTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StarsWithScores(rating = 7.7f)
            CookieRatingBadge(rating = 8.4f)
        }
    }
}
