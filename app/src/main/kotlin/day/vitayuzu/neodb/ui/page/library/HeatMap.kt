package day.vitayuzu.neodb.ui.page.library

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp
import day.vitayuzu.neodb.ui.page.library.HeatMapBlockType.Double
import day.vitayuzu.neodb.ui.page.library.HeatMapBlockType.None
import day.vitayuzu.neodb.ui.page.library.HeatMapBlockType.Quadruple
import day.vitayuzu.neodb.ui.page.library.HeatMapBlockType.Single
import day.vitayuzu.neodb.ui.page.library.HeatMapBlockType.Triple
import day.vitayuzu.neodb.ui.theme.kindColors
import day.vitayuzu.neodb.util.EntryType

@Composable
fun HeatMap(
    weeks: List<HeatMapWeekUiState>,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
        reverseLayout = true, // Newest week is at the end of the weeks, so reverse it
        modifier = modifier,
    ) {
        items(items = weeks, key = { it.index }) {
            HeatMapWeekColumn(it.blocks)
            Log.d("HeatMapWeeks", "${it.index}: ${it.blocks}")
        }
    }
}

@Composable
fun HeatMapWeekColumn(
    blocks: List<HeatMapDayData>,
    modifier: Modifier = Modifier,
) {
    val blockModifier =
        Modifier
            .size(24.dp)
            .clip(MaterialTheme.shapes.extraSmall)

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
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
fun DefaultHeatMapBlock(modifier: Modifier = Modifier) {
    Box(modifier = modifier.background(MaterialTheme.colorScheme.onPrimary))
}

@Composable
fun SingleHeatMapBlock(
    modifier: Modifier = Modifier,
    kind: EntryType = EntryType.default,
) {
    Box(
        modifier =
            modifier
                .background(MaterialTheme.colorScheme.kindColors(kind))
                .clickable { Log.d("HeatMap", kind.name) },
    )
}

@Composable
fun DoubleHeatMapBlock(
    topRight: EntryType,
    bottomLeft: EntryType,
    modifier: Modifier = Modifier,
) {
    val bottomLeftColor = MaterialTheme.colorScheme.kindColors(bottomLeft)
    Box(
        modifier =
            modifier
                .clickable { Log.d("HeatMap", "${topRight.name} ${bottomLeft.name}") }
                .background(MaterialTheme.colorScheme.kindColors(topRight)) // Top right
                .drawWithCache {
                    // Button left
                    val path =
                        Path().apply {
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
fun TripleHeatMapBlock(
    kind1: EntryType,
    kind2: EntryType,
    kind3: EntryType,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier.clickable {
                Log.d(
                    "HeatMap",
                    "${kind1.name} ${kind2.name} ${kind3.name}",
                )
            },
    ) {
        Box(
            modifier =
                Modifier
                    .size(8.dp, 24.dp)
                    .background(MaterialTheme.colorScheme.kindColors(kind1)),
        )
        Box(
            modifier =
                Modifier
                    .size(8.dp, 24.dp)
                    .background(MaterialTheme.colorScheme.kindColors(kind2)),
        )
        Box(
            modifier =
                Modifier
                    .size(8.dp, 24.dp)
                    .background(MaterialTheme.colorScheme.kindColors(kind3)),
        )
    }
}

@Composable
fun QuadrupleHeatMapBlock(
    kind1: EntryType,
    kind2: EntryType,
    kind3: EntryType,
    kind4: EntryType,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier.clickable {
                Log.d(
                    "HeatMap",
                    "${kind1.name} ${kind2.name} ${kind3.name} ${kind4.name}",
                )
            },
    ) {
        Row {
            Box(
                modifier =
                    Modifier
                        .size(12.dp)
                        .background(MaterialTheme.colorScheme.kindColors(kind1)),
            )
            Box(
                modifier =
                    Modifier
                        .size(12.dp)
                        .background(MaterialTheme.colorScheme.kindColors(kind2)),
            )
        }
        Row {
            Box(
                modifier =
                    Modifier
                        .size(12.dp)
                        .background(MaterialTheme.colorScheme.kindColors(kind3)),
            )
            Box(
                modifier =
                    Modifier
                        .size(12.dp)
                        .background(MaterialTheme.colorScheme.kindColors(kind4)),
            )
        }
    }
}
