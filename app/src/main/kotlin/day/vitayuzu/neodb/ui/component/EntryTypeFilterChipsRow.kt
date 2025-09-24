package day.vitayuzu.neodb.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import day.vitayuzu.neodb.util.EntryType

@Composable
fun EntryTypeFilterChipsRow(
    modifier: Modifier = Modifier,
    selectedEntryTypes: Set<EntryType> = emptySet(),
    onClick: (EntryType) -> Unit = {},
    onClearFilter: () -> Unit = {},
) {
    val state = rememberLazyListState()

    // Selected type first
    val sortedEntryTypes = buildList {
        addAll(selectedEntryTypes)
        addAll(EntryType.entries - selectedEntryTypes)
    }

    // Scroll to top when selected type changes.
    // Using side effect instead of inside onClick callback to avoid race condition between animateItem and animateScrollToItem.
    LaunchedEffect(sortedEntryTypes.firstOrNull()) {
        state.animateScrollToItem(0)
    }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        // Clear filters
        AnimatedVisibility(
            visible = selectedEntryTypes.isNotEmpty(),
            modifier = Modifier.height(FilterChipDefaults.Height),
            enter = expandHorizontally() + fadeIn(),
            exit = shrinkHorizontally() + fadeOut(),
        ) {
            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                FilledIconButton(
                    modifier = Modifier.padding(start = 8.dp),
                    shape = FilterChipDefaults.shape,
                    onClick = onClearFilter,
                ) {
                    Icon(Icons.Filled.Clear, "clear")
                }
            }
        }
        // Filters
        LazyRow(
            state = state,
            contentPadding = PaddingValues(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(sortedEntryTypes, key = { it.name }) { type ->
                FilterChip(
                    modifier = Modifier.animateItem(),
                    label = { Text(stringResource(type.toR())) },
                    selected = type in selectedEntryTypes,
                    onClick = { onClick(type) },
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewEntryTypeFilterChipsRow() {
    EntryTypeFilterChipsRow(selectedEntryTypes = setOf(EntryType.game))
}
