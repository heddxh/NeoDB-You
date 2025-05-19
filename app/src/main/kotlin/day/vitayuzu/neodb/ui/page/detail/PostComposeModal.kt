package day.vitayuzu.neodb.ui.page.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import day.vitayuzu.neodb.ui.component.Star
import day.vitayuzu.neodb.ui.component.StarIcon
import day.vitayuzu.neodb.ui.component.StarsWithScores
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostComposeModal(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismiss,
    ) {
        ComposeModalContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ComposeModalContent(modifier: Modifier = Modifier) {
    val buttons = listOf("todo", "doing", "done", "drop")
    var selectedIndex by remember { mutableIntStateOf(2) }

    var composeContent by remember { mutableStateOf("") }
    Column(
        modifier = modifier.padding(horizontal = 8.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            buttons.forEachIndexed { index, button ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index, 4),
                    onClick = { selectedIndex = index },
                    selected = selectedIndex == index,
                ) {
                    Text(button)
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
            var sliderValue by remember { mutableFloatStateOf(0f) }
            AnimatedVisibility(visible = selectedIndex != 0) {
                Slider(
                    modifier = Modifier.animateEnterExit(),
                    value = sliderValue,
                    valueRange = 0f..10f,
                    steps = 9,
                    onValueChange = { sliderValue = it },
                    track = {
                        StarsWithScores(
                            size = 48,
                            rating = sliderValue,
                            showScores = false,
                        )
                    },
                    thumb = {},
                )
            }
            Spacer(Modifier.weight(1f))
            Button(
                onClick = {},
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                )
            }
        }
    }
}

@Composable
private fun ClickableRatingStars(
    modifier: Modifier = Modifier,
    size: Int = 16,
) {
    var clickedIndex by remember { mutableIntStateOf(-1) }
    Row(modifier = modifier) {
        repeat(5) {
            ClickableStar(
                index = it,
                clickedIndex = clickedIndex,
                size = size,
            ) { clickedIndex = it }
        }
    }
}

@Composable
private fun ClickableStar(
    index: Int,
    clickedIndex: Int,
    modifier: Modifier = Modifier,
    size: Int = 16,
    onClick: () -> Unit = {},
) {
    var clicked by remember { mutableIntStateOf(0) } // 0 for left, 1 for right
    val kind by remember(clickedIndex, clicked) {
        if (clickedIndex > index) {
            mutableStateOf(Star.FULL)
        } else if (clickedIndex < index) {
            mutableStateOf(Star.EMPTY)
        } else {
            // This is the clicked star
            if (clicked == 0) {
                mutableStateOf(Star.HALF)
            } else {
                mutableStateOf(Star.FULL)
            }
        }
    }
    Box(modifier = modifier.size(size.dp)) {
        StarIcon(kind = kind, size = size)
        Row {
            // Left
            Surface(
                onClick = {
                    clicked = 0
                    onClick()
                },
                modifier = Modifier.width(size.div(2).dp),
            ) {}
            // Right
            Surface(
                onClick = {
                    clicked = 1
                    onClick()
                },
                modifier = Modifier.width(size.div(2).dp),
            ) {}
        }
    }
}

@PreviewLightDark
@Preview
@Composable
private fun PreviewPostComposeModal() {
    NeoDBYouTheme {
        Surface(Modifier.fillMaxSize()) {
            ComposeModalContent()
        }
    }
}

@Preview
@Composable
private fun PreviewClickableStar() {
    ClickableRatingStars(size = 64)
}
