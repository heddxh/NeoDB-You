package day.vitayuzu.neodb.ui.component

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopSearchBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import day.vitayuzu.neodb.R
import day.vitayuzu.neodb.ui.model.Entry
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import day.vitayuzu.neodb.util.EntryType
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@Suppress("ktlint:compose:modifier-missing-check")
@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun SearchModal(
    modifier: Modifier = Modifier,
    state: SearchBarState = rememberSearchBarState(),
    onSearch: (String) -> Flow<List<Entry>> = { flowOf() },
    onClickEntry: (EntryType, String) -> Unit = { _, _ -> },
) {
    val textFieldState = rememberTextFieldState()
    val scope = rememberCoroutineScope()

    val inputField = @Composable {
        SearchBarDefaults.InputField(
            textFieldState = textFieldState,
            searchBarState = state,
            onSearch = {},
            placeholder = { Text(stringResource(R.string.textfield_search)) },
            leadingIcon = {
                if (state.targetValue == SearchBarValue.Collapsed) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                    )
                } else {
                    IconButton(
                        onClick = { scope.launch { state.animateToCollapsed() } },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                }
            },
        )
    }

    TopSearchBar(state, inputField, modifier)

    // Auto expand the search bar when enter composition
    LaunchedEffect(true) {
        state.animateToExpanded()
    }

    ExpandedFullScreenSearchBar(state, inputField) {
        val resultList = remember { mutableStateSetOf<Entry>() }

        // Perform search request
        LaunchedEffect(onSearch) {
            snapshotFlow { textFieldState.text }
                .debounce(500)
                .filterNot { it.isEmpty() }
                .collectLatest { query ->
                    resultList.clear()
                    onSearch(query.toString()).collect {
                        resultList.addAll(it)
                    }
                }
        }
        // Clear input field when collapsed
        DisposableEffect(resultList) {
            onDispose {
                textFieldState.clearText()
            }
        }

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            stickyHeader {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(8.dp)
                        .fillMaxWidth(),
                ) {
                    Text("Press to fetch from external website")
                    Switch(checked = false, onCheckedChange = {})
                }
            }
            items(resultList.toList(), key = { it.uuid }) {
                EntryMarkCard(entry = it, mark = null) { type, uuid ->
                    onClickEntry(type, uuid)
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewSearchModal() {
    NeoDBYouTheme {
        Scaffold(topBar = { SearchModal() }) {}
    }
}
