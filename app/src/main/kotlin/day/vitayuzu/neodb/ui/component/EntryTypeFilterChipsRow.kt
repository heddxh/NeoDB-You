package day.vitayuzu.neodb.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import day.vitayuzu.neodb.util.EntryType

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EntryTypeFilterChipsRow(
    modifier: Modifier = Modifier,
    selectedEntryTypes: Set<EntryType> = emptySet(),
    onClick: (EntryType) -> Unit = {},
    onClearFilter: () -> Unit = {},
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        AnimatedVisibility(
            visible = selectedEntryTypes.isNotEmpty(),
            modifier = Modifier.height(FilterChipDefaults.Height),
            enter = expandHorizontally() + fadeIn(),
            exit = shrinkHorizontally() + fadeOut(),
        ) {
            FilledIconButton(onClick = onClearFilter, shape = FilterChipDefaults.shape) {
                Icon(Icons.Filled.Clear, "clear")
            }
        }
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Spacer(
                Modifier.animateContentSize().width(
                    if (selectedEntryTypes.isEmpty()) 8.dp else 0.dp,
                ),
            )
            for (type in EntryType.entries) {
                FilterChip(
                    label = { Text(stringResource(type.toR())) },
                    selected = type in selectedEntryTypes,
                    onClick = { onClick(type) },
                )
            }
            Spacer(Modifier.width(8.dp))
        }
    }
}

@Preview
@Composable
private fun PreviewEntryTypeFilterChipsRow() {
    EntryTypeFilterChipsRow(selectedEntryTypes = setOf(EntryType.game))
}
