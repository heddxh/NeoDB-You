package day.vitayuzu.neodb.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import day.vitayuzu.neodb.R
import day.vitayuzu.neodb.ui.model.Entry
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun SearchModal(
    modifier: Modifier = Modifier,
    state: SearchBarState = rememberSearchBarState(SearchBarValue.Expanded),
    onSearch: (String) -> Flow<List<Entry>> = { flowOf() },
) {
    val textFieldState = rememberTextFieldState()

    val inputField = @Composable {
        SearchBarDefaults.InputField(
            textFieldState = textFieldState,
            searchBarState = state,
            onSearch = {},
            placeholder = { Text(stringResource(R.string.textfield_search)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                )
            },
        )
    }

    AnimatedVisibility(
        visible = state.targetValue == SearchBarValue.Expanded,
        enter = fadeIn() + slideInHorizontally { it },
        exit = fadeOut() + slideOutHorizontally { it },
    ) {
        SearchBar(
            state = state,
            inputField = inputField,
            modifier = Modifier.padding(WindowInsets.statusBars.asPaddingValues()).fillMaxWidth(),
        )
    }

    ExpandedFullScreenSearchBar(
        state = state,
        modifier = modifier,
        inputField = inputField,
    ) {
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

        LazyColumn {
            if (resultList.isEmpty()) {
                item {
                    Text(text = "EMPTY")
                }
            } else {
                items(resultList.toList()) {
                    EntryMarkCard(entry = it, mark = null)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewSearchModal() {
    NeoDBYouTheme {
        SearchModal()
    }
}
