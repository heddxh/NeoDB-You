package day.vitayuzu.neodb.ui.page.library

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import day.vitayuzu.neodb.ui.component.EntryMarkCard
import day.vitayuzu.neodb.ui.component.EntryTypeFilterChipsRow
import day.vitayuzu.neodb.ui.component.SharedSearchFab
import day.vitayuzu.neodb.ui.model.Mark
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import day.vitayuzu.neodb.util.EntryType
import day.vitayuzu.neodb.util.EntryType.book
import day.vitayuzu.neodb.util.EntryType.movie
import day.vitayuzu.neodb.util.EntryType.tv
import day.vitayuzu.neodb.util.ShelfType
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList

@Composable
fun LibraryPage(
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = hiltViewModel(),
    bottomBar: @Composable () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        bottomBar = bottomBar,
        floatingActionButton = { SharedSearchFab() },
    ) {
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = viewModel::refresh,
            modifier = Modifier.padding(it).consumeWindowInsets(it),
        ) {
            LibraryContent(
                uiState = uiState,
                onShelfTypeChange = viewModel::switchShelfType,
                onEntryTypeToggle = viewModel::toggleSelectedEntryType,
                onClearFilter = viewModel::resetEntryType,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LibraryContent(
    uiState: LibraryUiState,
    modifier: Modifier = Modifier,
    onShelfTypeChange: (ShelfType) -> Unit = {},
    onEntryTypeToggle: (EntryType) -> Unit = {},
    onClearFilter: () -> Unit = {},
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 88.dp),
    ) {
        stickyHeader(contentType = "sticky") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface),
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
//                    ButtonGroup(
//                        overflowIndicator = { state ->
//                            ButtonGroupDefaults.OverflowIndicator(state)
//                        },
//                    ) {
//                        ShelfType.entries.forEachIndexed { index, type ->
//                            toggleableItem(
//                                label = "",
//                                icon = {
//                                    Text(
//                                        stringResource(type.toR()),
//                                        maxLines = 1,
//                                        softWrap = false,
//                                    )
//                                },
//                                checked = type == uiState.selectedShelfType,
//                                onCheckedChange = { onShelfTypeChange(type) },
//                            )
//                        }
//                    }
                    SingleChoiceSegmentedButtonRow(
                        Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 8.dp),
                    ) {
                        ShelfType.entries.forEachIndexed { index, type ->
                            SegmentedButton(
                                selected = type == uiState.selectedShelfType,
                                onClick = { onShelfTypeChange(type) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index,
                                    ShelfType.entries.size,
                                ),
                            ) {
                                Text(
                                    stringResource(type.toR()),
                                    maxLines = 1,
                                    softWrap = false,
                                )
                            }
                        }
                    }
                }
                EntryTypeFilterChipsRow(
                    selectedEntryTypes = uiState.selectedEntryTypes,
                    onClick = onEntryTypeToggle,
                    onClearFilter = onClearFilter,
                )
            }
        }
        item(contentType = "heatmap") {
            HeatMapCard(uiState.heatMap.toImmutableList())
        }
        items(
            contentType = { "marks" },
            items = uiState.displayedMarks,
            key = { it.entry.uuid },
        ) {
            EntryMarkCard(
                entry = it.entry,
                mark = it,
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}

@Preview
@Composable
private fun PreviewLibraryContent() {
    val sampleMarks = listOf(Mark.TEST)
    val sampleHeatMap = listOf(
        HeatMapWeekUiState(
            index = 0,
            blocks = listOf(
                HeatMapDayData(
                    weekIndex = 0,
                    dayIndex = 0,
                    type = HeatMapBlockType.Single(book),
                ),
                HeatMapDayData(
                    weekIndex = 0,
                    dayIndex = 2,
                    type = HeatMapBlockType.Double(movie, tv),
                ),
            ),
        ),
        HeatMapWeekUiState(index = 1, blocks = emptyList()),
    )
    NeoDBYouTheme {
        LibraryContent(
            uiState = LibraryUiState(
                displayedMarks = sampleMarks,
                selectedEntryTypes = persistentSetOf(),
                selectedShelfType = ShelfType.complete,
                heatMap = sampleHeatMap,
            ),
        )
    }
}
