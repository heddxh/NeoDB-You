package day.vitayuzu.neodb.ui.library

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import day.vitayuzu.neodb.R
import day.vitayuzu.neodb.ui.component.EntryMarkCard
import day.vitayuzu.neodb.util.EntryType
import day.vitayuzu.neodb.util.ShelfType

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun LibraryPage(
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = viewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(stringResource(R.string.library_title))
                })
        },
    ) { innerPadding ->
        PullToRefreshBox(
            modifier = Modifier.padding(innerPadding),
            isRefreshing = uiState.isLoading,
            onRefresh = viewModel::refresh
        ) {
            LazyColumn {
                stickyHeader {
                    PrimaryTabRow(
                        selectedTabIndex = ShelfType.entries.indexOf(uiState.selectedShelfType)
                    ) {
                        for (type in ShelfType.entries) {
                            Tab(
                                selected = type == uiState.selectedShelfType,
                                onClick = { viewModel.switchShelfType(type) },
                                text = { Text(stringResource(type.toR())) })
                        }
                    }
                    EntryTypeFilterChipsRow(
                        selectedEntryTypes = uiState.selectedEntryTypes,
                        onClick = viewModel::toggleSelectedEntryType,
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    )
                }
                item { HeatMap(uiState.heatMap) }
                items(
                    items = uiState.displayedMarks,
                    key = { it.entry.url }) {
                    EntryMarkCard(
                        it.entry,
                        it
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
        horizontalArrangement = Arrangement.spacedBy(8.dp)
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