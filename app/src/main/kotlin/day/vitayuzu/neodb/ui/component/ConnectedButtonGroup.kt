package day.vitayuzu.neodb.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButtonColors
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TonalToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ConnectedButtonGroup(
    optionNumber: Int,
    selectedOption: Int,
    onSelectedChange: (Int) -> Unit,
    optionContent: @Composable (Int) -> Unit,
    modifier: Modifier = Modifier,
    colors: @Composable (Int) -> ToggleButtonColors = {
        ToggleButtonDefaults.tonalToggleButtonColors()
    },
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            ButtonGroupDefaults.ConnectedSpaceBetween,
        ),
    ) {
        for (index in 0 until optionNumber) {
            TonalToggleButton(
                checked = selectedOption == index,
                onCheckedChange = { onSelectedChange(index) },
                modifier = Modifier.semantics { role = Role.RadioButton },
                colors = colors(index),
                shapes =
                    when (index) {
                        0 -> {
                            ButtonGroupDefaults.connectedLeadingButtonShapes()
                        }

                        optionNumber - 1 -> {
                            ButtonGroupDefaults.connectedTrailingButtonShapes()
                        }

                        else -> {
                            ButtonGroupDefaults.connectedMiddleButtonShapes()
                        }
                    },
            ) {
                optionContent(index)
            }
        }
    }
}

@Preview
@Composable
private fun ConnectedButtonGroupPreview() {
    NeoDBYouTheme {
        ConnectedButtonGroup(
            optionNumber = 4,
            selectedOption = 1,
            onSelectedChange = {},
            optionContent = { index ->
                Text("Option $index")
            },
        )
    }
}
