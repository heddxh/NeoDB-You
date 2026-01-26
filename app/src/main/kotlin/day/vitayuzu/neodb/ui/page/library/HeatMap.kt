package day.vitayuzu.neodb.ui.page.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import day.vitayuzu.neodb.R
import day.vitayuzu.neodb.ui.page.library.HeatMapBlockType.Double
import day.vitayuzu.neodb.ui.page.library.HeatMapBlockType.None
import day.vitayuzu.neodb.ui.page.library.HeatMapBlockType.Quadruple
import day.vitayuzu.neodb.ui.page.library.HeatMapBlockType.Single
import day.vitayuzu.neodb.ui.page.library.HeatMapBlockType.Triple
import day.vitayuzu.neodb.ui.theme.kindColors
import day.vitayuzu.neodb.util.EntryType
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun HeatMapCard(weeks: List<HeatMapWeekUiState>, modifier: Modifier = Modifier) {
    val currentYear = Clock.System.todayIn(TimeZone.currentSystemDefault()).year

    Card(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            HeatMapHeader(year = currentYear)
            Spacer(Modifier.size(8.dp))
            HeatMapGrid(weeks)
            Spacer(Modifier.size(8.dp))
            HeatMapLegend()
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HeatMapLegend(modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        EntryType.entries.forEach { type ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
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
    }
}

@Composable
private fun HeatMapGrid(weeks: List<HeatMapWeekUiState>, modifier: Modifier = Modifier) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.End),
        reverseLayout = true,
        modifier = modifier,
    ) {
        items(items = weeks, key = { it.index }) {
            HeatMapWeekColumn(it.blocks)
        }
    }
}

private const val BLOCK_SIZE = 14
private const val BLOCK_GAP = 2

@Composable
private fun HeatMapWeekColumn(blocks: List<HeatMapDayData>, modifier: Modifier = Modifier) {
    val blockModifier = Modifier
        .size(BLOCK_SIZE.dp)
        .clip(MaterialTheme.shapes.extraSmall)

    Column(
        verticalArrangement = Arrangement.spacedBy(BLOCK_GAP.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        (0..6).map { index ->
            val block = blocks.find { it.dayIndex == index }
            if (block == null) {
                DefaultHeatMapBlock(blockModifier)
            } else {
                when (block.type) {
                    is None -> DefaultHeatMapBlock(blockModifier)
                    is Single ->
                        SingleHeatMapBlock(
                            kind = block.type.kind,
                            modifier = blockModifier,
                        )

                    is Double ->
                        DoubleHeatMapBlock(
                            topRight = block.type.kind1,
                            bottomLeft = block.type.kind2,
                            modifier = blockModifier,
                        )

                    is Triple ->
                        TripleHeatMapBlock(
                            kind1 = block.type.kind1,
                            kind2 = block.type.kind2,
                            kind3 = block.type.kind3,
                            modifier = blockModifier,
                        )

                    is Quadruple ->
                        QuadrupleHeatMapBlock(
                            kind1 = block.type.kind1,
                            kind2 = block.type.kind2,
                            kind3 = block.type.kind3,
                            kind4 = block.type.kind4,
                            modifier = blockModifier,
                        )
                }
            }
        }
    }
}

@Composable
private fun DefaultHeatMapBlock(modifier: Modifier = Modifier) {
    Box(modifier = modifier.background(MaterialTheme.colorScheme.surfaceContainerHighest))
}

@Composable
private fun SingleHeatMapBlock(modifier: Modifier = Modifier, kind: EntryType = EntryType.default) {
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

private val tripleBlockWidth = (BLOCK_SIZE / 3f).dp
private val quadBlockSize = (BLOCK_SIZE / 2f).dp

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
