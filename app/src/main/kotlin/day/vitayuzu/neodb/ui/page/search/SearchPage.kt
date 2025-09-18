package day.vitayuzu.neodb.ui.page.search

import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import day.vitayuzu.neodb.R
import day.vitayuzu.neodb.ui.component.EntryMarkCard
import day.vitayuzu.neodb.util.sharedBoundsTransition
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun SearchPage(modifier: Modifier = Modifier, viewModel: SearchViewModel = hiltViewModel()) {
    val state = rememberSearchBarState(SearchBarValue.Expanded)
    val textFieldState = rememberTextFieldState()
    val scope = rememberCoroutineScope()

    Surface(
        modifier = modifier.fillMaxSize().sharedBoundsTransition(
            SearchPageKey,
            enter = scaleIn(),
            exit = scaleOut() + fadeOut(),
        ),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            SearchBarDefaults.InputField(
                modifier = Modifier.safeDrawingPadding(),
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

            // Perform search request
            LaunchedEffect(textFieldState.text) {
                snapshotFlow { textFieldState.text }
                    .debounce(500)
                    .filterNot { it.isEmpty() }
                    .collectLatest { query ->
                        viewModel.onSearch(query.toString())
                    }
            }

            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(viewModel.searchResult.toList(), key = { it.uuid }) {
                    EntryMarkCard(entry = it, mark = null)
                }
            }
        }
    }
}

data object SearchPageKey
