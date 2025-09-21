package day.vitayuzu.neodb.ui.page.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import day.vitayuzu.neodb.ui.component.EntryMarkCard
import day.vitayuzu.neodb.ui.component.EntryTypeFilterChipsRow
import day.vitayuzu.neodb.ui.component.SharedSearchFab
import day.vitayuzu.neodb.util.LocalNavigator
import day.vitayuzu.neodb.util.ShelfType

@Composable
fun LibraryPage(
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = hiltViewModel(),
    bottomBar: @Composable () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LocalNavigator.current
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
            LazyColumn(Modifier.fillMaxSize()) {
                stickyHeader {
                    // FIXME: In some locale like Chinese we don't need scrollable row
                    //  so it is not centered.
                    PrimaryScrollableTabRow(
                        ShelfType.entries.indexOf(uiState.selectedShelfType),
                        edgePadding = 16.dp,
                    ) {
                        for (type in ShelfType.entries) {
                            Tab(
                                selected = type == uiState.selectedShelfType,
                                onClick = { viewModel.switchShelfType(type) },
                                text = { Text(stringResource(type.toR()), softWrap = false) },
                            )
                        }
                    }
                    EntryTypeFilterChipsRow(
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                        selectedEntryTypes = uiState.selectedEntryTypes,
                        onClick = viewModel::toggleSelectedEntryType,
                        onClearFilter = viewModel::resetEntryType,
                    )
                }
                item { HeatMap(uiState.heatMap) }
                items(
                    items = uiState.displayedMarks,
                    key = { it.entry.url },
                ) {
                    EntryMarkCard(entry = it.entry, mark = it)
                }
            }
        }
    }
}
