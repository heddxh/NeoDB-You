package day.vitayuzu.neodb.ui.library

import androidx.compose.foundation.background
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
import day.vitayuzu.neodb.ui.library.HeatMapBlockType.DefaultHeatMapEntry
import day.vitayuzu.neodb.ui.library.HeatMapBlockType.DoubleHeatMapEntry
import day.vitayuzu.neodb.ui.library.HeatMapBlockType.QuadrupleHeatMapEntry
import day.vitayuzu.neodb.ui.library.HeatMapBlockType.SingleHeatMapEntry
import day.vitayuzu.neodb.ui.library.HeatMapBlockType.TripleHeatMapEntry
import day.vitayuzu.neodb.ui.theme.kindColors
import day.vitayuzu.neodb.util.EntryType

@Composable
fun HeatMap(
    weeks: List<HeatMapWeekUiState>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
        reverseLayout = true, // Newest week is at the end of the weeks, so reverse it
        modifier = modifier,
    ) {
        items(items = weeks, key = { it.index }) {
            HeatMapWeekColumn(it.blocks)
        }
    }
}

@Composable
fun HeatMapWeekColumn(
    blocks: List<HeatMapDayData>,
    modifier: Modifier = Modifier
) {

    val blockModifier = Modifier
        .size(24.dp)
        .clip(MaterialTheme.shapes.extraSmall)

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        for (i in 0..6) {
            val block = blocks.find { it.dayIndex == i }
            if (block == null) {
                DefaultHeatMapBlock(blockModifier)
            } else {
                when (block.type) {
                    is DefaultHeatMapEntry -> DefaultHeatMapBlock(blockModifier)
                    is SingleHeatMapEntry -> SingleHeatMapBlock(
                        kind = block.type.kind,
                        modifier = blockModifier
                    )
                    is DoubleHeatMapEntry -> DoubleHeatMapBlock(
                        topRight = block.type.kind1,
                        bottomLeft = block.type.kind2,
                        modifier = blockModifier
                    )
                    is TripleHeatMapEntry -> TripleHeatMapBlock(
                        kind1 = block.type.kind1,
                        kind2 = block.type.kind2,
                        kind3 = block.type.kind3,
                        modifier = blockModifier
                    )
                    is QuadrupleHeatMapEntry -> QuadrupleHeatMapBlock(
                        kind1 = block.type.kind1,
                        kind2 = block.type.kind2,
                        kind3 = block.type.kind3,
                        kind4 = block.type.kind4,
                        modifier = blockModifier
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
    kind: EntryType = EntryType.Default
) {
    Box(modifier = modifier.background(MaterialTheme.colorScheme.kindColors(kind)))
}

@Composable
fun DoubleHeatMapBlock(
    topRight: EntryType,
    bottomLeft: EntryType,
    modifier: Modifier = Modifier
) {
    val bottomLeftColor = MaterialTheme.colorScheme.kindColors(bottomLeft)
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.kindColors(topRight)) // Top right
            .drawWithCache { // Button left
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                }
                onDrawBehind {
                    drawPath(
                        path, bottomLeftColor, style = Fill
                    )
                }
            })
}

@Composable
fun TripleHeatMapBlock(
    kind1: EntryType,
    kind2: EntryType,
    kind3: EntryType,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Box(
            modifier = Modifier
                .size(8.dp, 24.dp)
                .background(MaterialTheme.colorScheme.kindColors(kind1))
        )
        Box(
            modifier = Modifier
                .size(8.dp, 24.dp)
                .background(MaterialTheme.colorScheme.kindColors(kind2))
        )
        Box(
            modifier = Modifier
                .size(8.dp, 24.dp)
                .background(MaterialTheme.colorScheme.kindColors(kind3))
        )
    }
}

@Composable
fun QuadrupleHeatMapBlock(
    kind1: EntryType,
    kind2: EntryType,
    kind3: EntryType,
    kind4: EntryType,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(MaterialTheme.colorScheme.kindColors(kind1))
            )
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(MaterialTheme.colorScheme.kindColors(kind2))
            )
        }
        Row {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(MaterialTheme.colorScheme.kindColors(kind3))
            )
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(MaterialTheme.colorScheme.kindColors(kind4))
            )
        }
    }
}


//// Preview
//@Preview
//@Composable
//private fun PreviewHeatMap() {
//    val blocks = listOf(
//        SingleHeatMapEntry(EntryType.Book),
//        DoubleHeatMapEntry(EntryType.Movie, EntryType.Music),
//        TripleHeatMapEntry(EntryType.Game, EntryType.Book, EntryType.Music),
//        QuadrupleHeatMapEntry(
//            EntryType.Book, EntryType.Movie, EntryType.Music, EntryType.Game
//        ),
//        SingleHeatMapEntry(EntryType.Book),
//        DoubleHeatMapEntry(EntryType.Movie, EntryType.Music),
//        TripleHeatMapEntry(EntryType.Game, EntryType.Book, EntryType.Music),
//    )
//    val week1 = HeatMapBlockWeek(
//        blocks = blocks, index = 0
//    )
//    val week2 = HeatMapBlockWeek(
//        blocks = blocks, index = 1
//    )
//    HeatMap(
//        listOf(
//            week1, week2
//        )
//    )
//}
//
//@Preview
//@Composable
//private fun PreviewWeekBlocks() {
//    HeatMapWeekColumn(
//        listOf(
//            SingleHeatMapEntry(EntryType.Book),
//            DoubleHeatMapEntry(EntryType.Movie, EntryType.Music),
//            TripleHeatMapEntry(EntryType.Game, EntryType.Book, EntryType.Music),
//            QuadrupleHeatMapEntry(
//                EntryType.Book, EntryType.Movie, EntryType.Music, EntryType.Game
//            ),
//            SingleHeatMapEntry(EntryType.Book),
//            DoubleHeatMapEntry(EntryType.Movie, EntryType.Music),
//            TripleHeatMapEntry(EntryType.Game, EntryType.Book, EntryType.Music),
//        )
//    )
//}
//
//@Preview
//@Composable
//private fun PreviewBlock() {
//    SingleHeatMapBlock(Modifier.size(24.dp))
//}
//
//@Preview
//@Composable
//private fun PreviewDouble() {
//    DoubleHeatMapBlock(EntryType.Book, EntryType.Movie, Modifier.size(24.dp))
//}
//
//@Preview
//@Composable
//private fun PreviewTriple() {
//    TripleHeatMapBlock(EntryType.Book, EntryType.Movie, EntryType.Music, Modifier.size(24.dp))
//}
//
//@Preview
//@Composable
//private fun PreviewQuadruple() {
//    QuadrupleHeatMapBlock(
//        EntryType.Book, EntryType.Movie, EntryType.Music, EntryType.Game, Modifier.size(24.dp)
//    )
//}
