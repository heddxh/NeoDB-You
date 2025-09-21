package day.vitayuzu.neodb.ui.page.search

import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import day.vitayuzu.neodb.R
import day.vitayuzu.neodb.ui.component.EntryMarkCard
import day.vitayuzu.neodb.ui.component.EntryTypeFilterChipsRow
import day.vitayuzu.neodb.ui.model.Entry
import day.vitayuzu.neodb.util.EntryType
import day.vitayuzu.neodb.util.LocalNavigator
import day.vitayuzu.neodb.util.sharedBoundsTransition
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNot

@OptIn(FlowPreview::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchPage(modifier: Modifier = Modifier, viewModel: SearchViewModel = hiltViewModel()) {
    Surface(
        modifier = modifier.fillMaxSize().sharedBoundsTransition(
            key = SearchPageKey,
            enter = scaleIn(),
            exit = scaleOut() + fadeOut(),
        ),
    ) {
        val textFieldState = rememberTextFieldState()

        // Perform search request
        LaunchedEffect(textFieldState.text) {
            snapshotFlow { textFieldState.text }
                .debounce(500)
                .filterNot { it.isEmpty() }
                .collectLatest { viewModel.onSearch(it.toString()) }
        }

        Box(Modifier.fillMaxSize()) {
            if (viewModel.isSearching) {
                LoadingIndicator(
                    Modifier.align(Alignment.Center).size(128.dp),
                )
            }
            SearchPageContent(
                state = textFieldState,
                result = viewModel.searchResult.toList(),
            )
        }
    }
}

data object SearchPageKey

@Composable
private fun SearchPageContent(
    modifier: Modifier = Modifier,
    state: TextFieldState = rememberTextFieldState(),
    result: List<Entry> = emptyList(),
) {
    val selectedEntryTypes = remember { mutableStateSetOf<EntryType>() }

    LazyColumn(
        modifier = modifier.safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            SearchBarInputField(
                state = state,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(top = 8.dp),
            )
        }
        item {
            EntryTypeFilterChipsRow(
                selectedEntryTypes = selectedEntryTypes,
                onClick = {
                    if (it in selectedEntryTypes) {
                        selectedEntryTypes.remove(it)
                    } else {
                        selectedEntryTypes.add(it)
                    }
                },
                onClearFilter = { selectedEntryTypes.clear() },
            )
        }
        items(
            result.filter { selectedEntryTypes.isEmpty() || it.category in selectedEntryTypes },
            key = { it.uuid },
        ) {
            EntryMarkCard(entry = it, mark = null)
        }
    }
}

@Composable
private fun SearchBarInputField(
    modifier: Modifier = Modifier,
    state: TextFieldState = rememberTextFieldState(),
) {
    val appNavigator = LocalNavigator.current
    val imeController = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        state = state,
        modifier = modifier,
        lineLimits = TextFieldLineLimits.SingleLine,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        onKeyboardAction = { imeController?.hide() },
        shape = MaterialTheme.shapes.small,
        // TODO: string resource and instance name
//        placeholder = { Text("Explore neodb.social", maxLines = 1) },
//        supportingText = { Text(stringResource(R.string.textfield_search)) },
        placeholder = { Text(stringResource(R.string.textfield_search)) },
        leadingIcon = {
            IconButton(onClick = {
                imeController?.hide()
                appNavigator.back()
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "back")
            }
        },
        trailingIcon = {
            IconButton(onClick = { state.clearText() }) {
                Icon(Icons.Default.Clear, "clear")
            }
        },
    )
}

@Preview
@Composable
private fun PreviewSearchPage() {
    val result = buildList {
        repeat(10) { index ->
            add(Entry.TEST.copy(uuid = index.toString()))
        }
    }
    Surface(Modifier.fillMaxSize()) { SearchPageContent(result = result) }
}
