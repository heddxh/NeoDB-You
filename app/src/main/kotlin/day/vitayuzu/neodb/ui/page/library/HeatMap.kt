package day.vitayuzu.neodb.ui.page.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer
import day.vitayuzu.neodb.R
import day.vitayuzu.neodb.ui.model.Mark
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import day.vitayuzu.neodb.ui.theme.kindColors
import day.vitayuzu.neodb.util.EntryType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val DAYS_PER_WEEK = 7
private const val BLOCK_SIZE = 14
private const val BLOCK_SPACING = 2
private const val WEEK_HEIGHT = DAYS_PER_WEEK * BLOCK_SIZE + (DAYS_PER_WEEK - 1) * BLOCK_SPACING
private val tripleBlockWidth = (BLOCK_SIZE / 3f).dp
private val quadBlockSize = (BLOCK_SIZE / 2f).dp

private const val SHIMMER_WEEKS = 24

@OptIn(ExperimentalTime::class)
@Composable
fun HeatMapCard(
    weeks: ImmutableList<HeatMapWeekUiState>,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    val currentYear = Clock.System.todayIn(TimeZone.currentSystemDefault()).year

    Card(
        modifier = modifier.fillMaxWidth().padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            HeatMapHeader(currentYear)
            if (isLoading) {
                ShimmerHeatMapGrid()
            } else {
                HeatMapGrid(weeks)
            }
            HeatMapLegend()
        }
    }
}

@Composable
private fun ShimmerHeatMapGrid(modifier: Modifier = Modifier) {
    val shimmerBlockModifier = Modifier
        .size(BLOCK_SIZE.dp)
        .clip(MaterialTheme.shapes.extraSmall)
        .shimmer()
        .background(Color.LightGray)

    Row(
        horizontalArrangement = Arrangement.spacedBy(BLOCK_SPACING.dp, Alignment.End),
        modifier = modifier.fillMaxWidth().height(WEEK_HEIGHT.dp),
    ) {
        repeat(SHIMMER_WEEKS) {
            Column(verticalArrangement = Arrangement.spacedBy(BLOCK_SPACING.dp)) {
                repeat(DAYS_PER_WEEK) {
                    Box(shimmerBlockModifier)
                }
            }
        }
    }
}

@Composable
private fun HeatMapHeader(year: Int, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.library_activity_title, year),
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier,
    )
}

@Composable
private fun HeatMapGrid(weeks: ImmutableList<HeatMapWeekUiState>, modifier: Modifier = Modifier) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(BLOCK_SPACING.dp, Alignment.End),
        reverseLayout = true,
        modifier = modifier.heightIn(min = WEEK_HEIGHT.dp),
    ) {
        items(items = weeks, key = { it.index }) { week ->
            HeatMapWeekColumn(week.blocks)
        }
    }
}

@Composable
private fun HeatMapLegend(modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        EntryType.entries.forEach { type ->
            HeatMapLegendItem(type)
        }
    }
}

@Composable
private fun HeatMapLegendItem(type: EntryType, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .background(MaterialTheme.colorScheme.kindColors(type)),
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = stringResource(type.toR()),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun HeatMapWeekColumn(blocks: List<HeatMapDayData>, modifier: Modifier = Modifier) {
    val blockModifier = Modifier
        .size(BLOCK_SIZE.dp)
        .clip(MaterialTheme.shapes.extraSmall)

    val blocksByDay = remember(blocks) {
        blocks.associateBy { it.dayIndex }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(BLOCK_SPACING.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        repeat(DAYS_PER_WEEK) { dayIndex ->
            HeatMapBlock(
                type = blocksByDay[dayIndex]?.type,
                modifier = blockModifier,
            )
        }
    }
}

@Composable
private fun HeatMapBlock(type: HeatMapBlockType?, modifier: Modifier = Modifier) {
    when (type) {
        null, HeatMapBlockType.None -> {
            DefaultHeatMapBlock(modifier)
        }

        is HeatMapBlockType.Single -> {
            SingleHeatMapBlock(
                kind = type.kind,
                modifier = modifier,
            )
        }

        is HeatMapBlockType.Double -> {
            DoubleHeatMapBlock(
                topRight = type.kind1,
                bottomLeft = type.kind2,
                modifier = modifier,
            )
        }

        is HeatMapBlockType.Triple -> {
            TripleHeatMapBlock(
                kind1 = type.kind1,
                kind2 = type.kind2,
                kind3 = type.kind3,
                modifier = modifier,
            )
        }

        is HeatMapBlockType.Quadruple -> {
            QuadrupleHeatMapBlock(
                kind1 = type.kind1,
                kind2 = type.kind2,
                kind3 = type.kind3,
                kind4 = type.kind4,
                modifier = modifier,
            )
        }
    }
}

data class HeatMapWeekUiState(
    val index: Int = 0,
    val blocks: List<HeatMapDayData> = emptyList(),
)

/**
 * Data for a day in heat map.
 * @param weekIndex: 0-based week index of this year
 * @param dayIndex: 0-based day index of this week (Monday = 0, Sunday = 6)
 * @param type: One of [HeatMapBlockType]
 */
data class HeatMapDayData(
    val weekIndex: Int = 0,
    val dayIndex: Int = 0,
    val type: HeatMapBlockType = HeatMapBlockType.None,
) {
    constructor(
        weekIndex: Int,
        marks: List<Mark>, // Assume marks is not empty
    ) : this(
        weekIndex = weekIndex,
        dayIndex = marks
            .first()
            .date.dayOfWeek.isoDayNumber - 1,
        // isoDayNumber is 1-based
        type = determineBlockType(marks),
    )
}

sealed interface HeatMapBlockType {
    data object None : HeatMapBlockType

    data class Single(val kind: EntryType) : HeatMapBlockType

    data class Double(
        val kind1: EntryType,
        val kind2: EntryType,
    ) : HeatMapBlockType

    data class Triple(
        val kind1: EntryType,
        val kind2: EntryType,
        val kind3: EntryType,
    ) : HeatMapBlockType

    data class Quadruple(
        val kind1: EntryType,
        val kind2: EntryType,
        val kind3: EntryType,
        val kind4: EntryType,
    ) : HeatMapBlockType
}

private fun determineBlockType(marks: List<Mark>): HeatMapBlockType {
    val distinctCategories = marks.map { it.entry.category }.distinct()
    return when (distinctCategories.size) {
        1 -> {
            HeatMapBlockType.Single(distinctCategories[0])
        }

        2 -> {
            HeatMapBlockType.Double(distinctCategories[0], distinctCategories[1])
        }

        3 -> {
            HeatMapBlockType.Triple(
                distinctCategories[0],
                distinctCategories[1],
                distinctCategories[2],
            )
        }

        4 -> {
            HeatMapBlockType.Quadruple(
                distinctCategories[0],
                distinctCategories[1],
                distinctCategories[2],
                distinctCategories[3],
            )
        }

        else -> {
            HeatMapBlockType.None
        }
    }
}

@Composable
private fun DefaultHeatMapBlock(modifier: Modifier = Modifier) {
    Box(modifier = modifier.background(MaterialTheme.colorScheme.surfaceContainerHighest))
}

@Composable
private fun SingleHeatMapBlock(kind: EntryType, modifier: Modifier = Modifier) {
    Box(modifier = modifier.background(MaterialTheme.colorScheme.kindColors(kind)))
}

@Composable
private fun DoubleHeatMapBlock(
    topRight: EntryType,
    bottomLeft: EntryType,
    modifier: Modifier = Modifier,
) {
    val bottomLeftColor = MaterialTheme.colorScheme.kindColors(bottomLeft)
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.kindColors(topRight))
            .drawWithCache {
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                }
                onDrawBehind {
                    drawPath(path, bottomLeftColor, style = Fill)
                }
            },
    )
}

@Composable
private fun TripleHeatMapBlock(
    kind1: EntryType,
    kind2: EntryType,
    kind3: EntryType,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        Box(
            modifier = Modifier
                .size(tripleBlockWidth, BLOCK_SIZE.dp)
                .background(MaterialTheme.colorScheme.kindColors(kind1)),
        )
        Box(
            modifier = Modifier
                .size(tripleBlockWidth, BLOCK_SIZE.dp)
                .background(MaterialTheme.colorScheme.kindColors(kind2)),
        )
        Box(
            modifier = Modifier
                .size(tripleBlockWidth, BLOCK_SIZE.dp)
                .background(MaterialTheme.colorScheme.kindColors(kind3)),
        )
    }
}

@Composable
private fun QuadrupleHeatMapBlock(
    kind1: EntryType,
    kind2: EntryType,
    kind3: EntryType,
    kind4: EntryType,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row {
            Box(
                modifier = Modifier
                    .size(quadBlockSize)
                    .background(MaterialTheme.colorScheme.kindColors(kind1)),
            )
            Box(
                modifier = Modifier
                    .size(quadBlockSize)
                    .background(MaterialTheme.colorScheme.kindColors(kind2)),
            )
        }
        Row {
            Box(
                modifier = Modifier
                    .size(quadBlockSize)
                    .background(MaterialTheme.colorScheme.kindColors(kind3)),
            )
            Box(
                modifier = Modifier
                    .size(quadBlockSize)
                    .background(MaterialTheme.colorScheme.kindColors(kind4)),
            )
        }
    }
}

@Preview
@Composable
private fun PreviewHeatMapCard() {
    val sampleWeeks = persistentListOf(
        HeatMapWeekUiState(
            index = 0,
            blocks = listOf(
                HeatMapDayData(0, 0, HeatMapBlockType.Single(EntryType.book)),
                HeatMapDayData(0, 2, HeatMapBlockType.Double(EntryType.movie, EntryType.tv)),
                HeatMapDayData(
                    0,
                    4,
                    HeatMapBlockType.Triple(EntryType.game, EntryType.music, EntryType.podcast),
                ),
                HeatMapDayData(
                    0,
                    6,
                    HeatMapBlockType.Quadruple(
                        EntryType.book,
                        EntryType.movie,
                        EntryType.game,
                        EntryType.music,
                    ),
                ),
            ),
        ),
        HeatMapWeekUiState(
            index = 1,
            blocks = listOf(
                HeatMapDayData(1, 1, HeatMapBlockType.Single(EntryType.movie)),
                HeatMapDayData(1, 3, HeatMapBlockType.Single(EntryType.game)),
            ),
        ),
        HeatMapWeekUiState(index = 2, blocks = emptyList()),
        HeatMapWeekUiState(
            index = 3,
            blocks = listOf(
                HeatMapDayData(3, 0, HeatMapBlockType.Double(EntryType.book, EntryType.music)),
            ),
        ),
    )
    NeoDBYouTheme {
        HeatMapCard(weeks = sampleWeeks, isLoading = false)
    }
}
