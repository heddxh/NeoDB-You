package day.vitayuzu.neodb.ui.page.settings

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import day.vitayuzu.neodb.ui.component.ConnectedButtonGroup
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SelectablePreference(
    optionNumber: Int,
    selectedOption: Int,
    onSelectedChange: (Int) -> Unit,
    shape: Shape,
    modifier: Modifier = Modifier,
    color: Color = CardDefaults.cardColors().containerColor,
    title: @Composable () -> Unit = {},
    optionContent: @Composable (Int) -> Unit = {},
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = color),
    ) {
        title()

        val scrollState = rememberScrollState()
        LaunchedEffect(scrollState, selectedOption) {
            if (selectedOption == 0) {
                scrollState.animateScrollTo(0)
            } else if (selectedOption == optionNumber - 1) {
                scrollState.animateScrollTo(scrollState.maxValue)
            }
        }
        ConnectedButtonGroup(
            optionNumber = optionNumber,
            selectedOption = selectedOption,
            onSelectedChange = onSelectedChange,
            optionContent = optionContent,
            modifier = Modifier
                .padding(start = 56.dp, end = 8.dp)
                .horizontalScroll(scrollState),
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
private fun SelectablePreferencePreview() {
    NeoDBYouTheme {
        SelectablePreference(
            optionNumber = 4,
            selectedOption = 1,
            onSelectedChange = {},
            shape = MaterialTheme.shapes.medium,
            title = {
                Text(
                    text = "Select Option",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium,
                )
            },
            optionContent = { index ->
                Text("Option $index")
            },
        )
    }
}
