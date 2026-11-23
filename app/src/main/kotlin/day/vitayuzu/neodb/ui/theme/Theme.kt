package day.vitayuzu.neodb.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import day.vitayuzu.neodb.util.EntryType
import day.vitayuzu.neodb.util.ShelfType

@Immutable
data class ColorPair(
    val container: Color,
    val onContainer: Color,
    val dot: Color,
)

private val DarkColorScheme =
    darkColorScheme(
        primary = BrandPrimaryDark,
        onPrimary = BrandOnPrimaryDark,
        primaryContainer = BrandPrimaryContainerDark,
        onPrimaryContainer = BrandOnPrimaryContainerDark,
        secondary = BrandSecondaryDark,
        onSecondary = BrandOnSecondaryDark,
        secondaryContainer = BrandSecondaryContainerDark,
        onSecondaryContainer = BrandOnSecondaryContainerDark,
        tertiary = BrandTertiaryDark,
        onTertiary = BrandOnTertiaryDark,
        tertiaryContainer = BrandTertiaryContainerDark,
        onTertiaryContainer = BrandOnTertiaryContainerDark,
        background = BrandBackgroundDark,
        onBackground = BrandOnBackgroundDark,
        surface = BrandSurfaceDark,
        onSurface = BrandOnSurfaceDark,
        surfaceVariant = BrandSurfaceVariantDark,
        onSurfaceVariant = BrandOnSurfaceVariantDark,
        outline = BrandOutlineDark,
        outlineVariant = BrandOutlineVariantDark,
        surfaceContainerLowest = BrandSurfaceContainerLowestDark,
        surfaceContainerLow = BrandSurfaceContainerLowDark,
        surfaceContainer = BrandSurfaceContainerDark,
        surfaceContainerHigh = BrandSurfaceContainerHighDark,
        surfaceContainerHighest = BrandSurfaceContainerHighestDark,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = BrandPrimaryLight,
        onPrimary = BrandOnPrimaryLight,
        primaryContainer = BrandPrimaryContainerLight,
        onPrimaryContainer = BrandOnPrimaryContainerLight,
        secondary = BrandSecondaryLight,
        onSecondary = BrandOnSecondaryLight,
        secondaryContainer = BrandSecondaryContainerLight,
        onSecondaryContainer = BrandOnSecondaryContainerLight,
        tertiary = BrandTertiaryLight,
        onTertiary = BrandOnTertiaryLight,
        tertiaryContainer = BrandTertiaryContainerLight,
        onTertiaryContainer = BrandOnTertiaryContainerLight,
        background = BrandBackgroundLight,
        onBackground = BrandOnBackgroundLight,
        surface = BrandSurfaceLight,
        onSurface = BrandOnSurfaceLight,
        surfaceVariant = BrandSurfaceVariantLight,
        onSurfaceVariant = BrandOnSurfaceVariantLight,
        outline = BrandOutlineLight,
        outlineVariant = BrandOutlineVariantLight,
        surfaceContainerLowest = BrandSurfaceContainerLowestLight,
        surfaceContainerLow = BrandSurfaceContainerLowLight,
        surfaceContainer = BrandSurfaceContainerLight,
        surfaceContainerHigh = BrandSurfaceContainerHighLight,
        surfaceContainerHighest = BrandSurfaceContainerHighestLight,
    )

private val ColorScheme.isDark: Boolean
    get() = background.luminance() < 0.5f

val ColorScheme.ratingColor: Color
    @Composable
    get() = RatingColor

@Composable
fun ColorScheme.kindColors(kind: EntryType): Color = when (kind) {
    EntryType.book -> BookColor
    EntryType.game -> GameColor
    EntryType.movie -> MovieColor
    EntryType.music -> MusicColor
    EntryType.performance -> PerformanceColor
    EntryType.tv -> TVColor
    EntryType.podcast -> PodcastColor
    EntryType.default -> OtherColor
}

@Composable
fun ColorScheme.kindColorPair(kind: EntryType): ColorPair {
    val dark = isDark
    return when (kind) {
        EntryType.book -> if (dark) {
            ColorPair(BookContainerDark, BookOnContainerDark, BookColor)
        } else {
            ColorPair(BookContainerLight, BookOnContainerLight, BookColor)
        }

        EntryType.game -> if (dark) {
            ColorPair(GameContainerDark, GameOnContainerDark, GameColor)
        } else {
            ColorPair(GameContainerLight, GameOnContainerLight, GameColor)
        }

        EntryType.movie -> if (dark) {
            ColorPair(MovieContainerDark, MovieOnContainerDark, MovieColor)
        } else {
            ColorPair(MovieContainerLight, MovieOnContainerLight, MovieColor)
        }

        EntryType.music -> if (dark) {
            ColorPair(MusicContainerDark, MusicOnContainerDark, MusicColor)
        } else {
            ColorPair(MusicContainerLight, MusicOnContainerLight, MusicColor)
        }

        EntryType.performance -> if (dark) {
            ColorPair(PerformanceContainerDark, PerformanceOnContainerDark, PerformanceColor)
        } else {
            ColorPair(PerformanceContainerLight, PerformanceOnContainerLight, PerformanceColor)
        }

        EntryType.tv -> if (dark) {
            ColorPair(TVContainerDark, TVOnContainerDark, TVColor)
        } else {
            ColorPair(TVContainerLight, TVOnContainerLight, TVColor)
        }

        EntryType.podcast -> if (dark) {
            ColorPair(PodcastContainerDark, PodcastOnContainerDark, PodcastColor)
        } else {
            ColorPair(PodcastContainerLight, PodcastOnContainerLight, PodcastColor)
        }

        EntryType.default -> if (dark) {
            ColorPair(OtherContainerDark, OtherOnContainerDark, OtherColor)
        } else {
            ColorPair(OtherContainerLight, OtherOnContainerLight, OtherColor)
        }
    }
}

@Composable
fun ColorScheme.statusColor(shelfType: ShelfType): Color = when (shelfType) {
    ShelfType.wishlist -> PerformanceColor
    ShelfType.progress -> PodcastColor
    ShelfType.complete -> MusicColor
    ShelfType.dropped -> GameColor
}

@Composable
fun ColorScheme.statusColorPair(shelfType: ShelfType): ColorPair {
    val dark = isDark
    return when (shelfType) {
        ShelfType.wishlist -> if (dark) {
            ColorPair(PerformanceContainerDark, PerformanceOnContainerDark, PerformanceColor)
        } else {
            ColorPair(PerformanceContainerLight, PerformanceOnContainerLight, PerformanceColor)
        }

        ShelfType.progress -> if (dark) {
            ColorPair(PodcastContainerDark, PodcastOnContainerDark, PodcastColor)
        } else {
            ColorPair(PodcastContainerLight, PodcastOnContainerLight, PodcastColor)
        }

        ShelfType.complete -> if (dark) {
            ColorPair(MusicContainerDark, MusicOnContainerDark, MusicColor)
        } else {
            ColorPair(MusicContainerLight, MusicOnContainerLight, MusicColor)
        }

        ShelfType.dropped -> if (dark) {
            ColorPair(GameContainerDark, GameOnContainerDark, GameColor)
        } else {
            ColorPair(GameContainerLight, GameOnContainerLight, GameColor)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NeoDBYouTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> {
                DarkColorScheme
            }

            else -> {
                LightColorScheme
            }
        }

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        typography = Typography,
        motionScheme = MotionScheme.expressive(),
        shapes = Shapes(),
        content = content,
    )
}
