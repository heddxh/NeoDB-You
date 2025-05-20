package day.vitayuzu.neodb.ui.page.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import day.vitayuzu.neodb.data.schema.MarkInSchema
import day.vitayuzu.neodb.ui.component.StarsWithScores
import day.vitayuzu.neodb.util.ShelfType
import kotlinx.datetime.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostComposeModal(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onSend: (MarkInSchema) -> Unit = {},
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismiss,
    ) {
        ComposeModalContent(onSend = onSend)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ComposeModalContent(
    modifier: Modifier = Modifier,
    onSend: (MarkInSchema) -> Unit = {},
) {
    val shelfTypes = ShelfType.entries
    var selectedShelfTypeIndex by remember { mutableIntStateOf(2) }

    var composeContent by remember { mutableStateOf("") }
    Column(
        modifier = modifier.padding(horizontal = 8.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            shelfTypes.forEachIndexed { index, button ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index, 4),
                    onClick = { selectedShelfTypeIndex = index },
                    selected = selectedShelfTypeIndex == index,
                ) {
                    Text(stringResource(button.toR()))
                }
            }
        }
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = composeContent,
            onValueChange = { composeContent = it },
            minLines = 5,
            label = { Text("Content") },
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            var ratingSliderValue by remember { mutableFloatStateOf(0f) }
            AnimatedVisibility(visible = selectedShelfTypeIndex != 0) {
                Slider(
                    modifier = Modifier.animateEnterExit(),
                    value = ratingSliderValue,
                    valueRange = 0f..10f,
                    steps = 9,
                    onValueChange = { ratingSliderValue = it },
                    track = {
                        StarsWithScores(
                            size = 48,
                            rating = ratingSliderValue,
                            showScores = false,
                        )
                    },
                    thumb = {},
                )
            }
            Spacer(Modifier.weight(1f))
            Button(
                onClick = {
                    onSend(
                        MarkInSchema(
                            shelfType = shelfTypes[selectedShelfTypeIndex],
                            visibility = 0, // TODO
                            commentText = composeContent,
                            ratingGrade = ratingSliderValue.toInt(),
                            tags = emptyList(), // TODO
                            createdTime = Clock.System.now().toString(), // TODO
                            postToFediverse = false, // TODO
                        ),
                    )
                },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                )
            }
        }
    }
}
