package day.vitayuzu.neodb.ui.page.library

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import day.vitayuzu.neodb.ui.component.EntryMarkCard
import day.vitayuzu.neodb.util.EntryType
import day.vitayuzu.neodb.util.ShelfType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun LibraryPage(
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = hiltViewModel(),
    onClickEntry: (EntryType, String) -> Unit = { _, _ -> },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = modifier,
    ) {
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = viewModel::refresh,
        ) {
            LazyColumn {
                stickyHeader {
                    // FIXME: In some locale like Chinese we don't need scrollable row.
                    PrimaryScrollableTabRow(ShelfType.entries.indexOf(uiState.selectedShelfType)) {
                        for (type in ShelfType.entries) {
                            Tab(
                                selected = type == uiState.selectedShelfType,
                                onClick = { viewModel.switchShelfType(type) },
                                text = { Text(stringResource(type.toR()), softWrap = false) },
                            )
                        }
                    }
                    EntryTypeFilterChipsRow(
                        selectedEntryTypes = uiState.selectedEntryTypes,
                        onClick = viewModel::toggleSelectedEntryType,
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                    )
                }
                item { HeatMap(uiState.heatMap) }
                items(
                    items = uiState.displayedMarks,
                    key = { it.entry.url },
                ) {
                    EntryMarkCard(
                        entry = it.entry,
                        mark = it,
                        onClickEntry = onClickEntry,
                    )
                }
            }
        }
    }
}

@Composable
private fun EntryTypeFilterChipsRow(
    modifier: Modifier = Modifier,
    selectedEntryTypes: Set<EntryType> = emptySet(),
    onClick: (EntryType) -> Unit = {},
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        for (type in EntryType.entries) {
            item {
                FilterChip(
                    label = { Text(stringResource(type.toR())) },
                    selected = type in selectedEntryTypes,
                    onClick = { onClick(type) },
                )
            }
        }
    }
}
