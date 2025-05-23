package day.vitayuzu.neodb.ui.page.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import day.vitayuzu.neodb.R
import day.vitayuzu.neodb.data.schema.MarkInSchema
import day.vitayuzu.neodb.ui.component.StarsWithScores
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import day.vitayuzu.neodb.util.ShelfType
import day.vitayuzu.neodb.util.Visibility
import day.vitayuzu.neodb.util.toDateString
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostComposeModal(
    postDate: Instant,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onSend: (MarkInSchema) -> Unit = {},
    onShowDatePicker: () -> Unit = {},
) {
    // Avoid last row to be covered by navigation bar when open more settings
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        modifier = modifier,
    ) {
        ComposeModalContent(
            postDate = postDate,
            onSend = onSend,
            onShowDatePicker = onShowDatePicker,
            modifier = Modifier.padding(WindowInsets.navigationBars.asPaddingValues()),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ComposeModalContent(
    postDate: Instant,
    modifier: Modifier = Modifier,
    onSend: (MarkInSchema) -> Unit = {},
    onShowDatePicker: () -> Unit = {},
) {
    val shelfTypes = ShelfType.entries
    var selectedShelfTypeIndex by remember { mutableIntStateOf(2) }

    var composeContent by remember { mutableStateOf("") }

    Box(modifier = modifier) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            var isShowMoreSettings by remember { mutableStateOf(false) }
            var isPostToFedi by remember { mutableStateOf(false) }
            var postVisibility by remember { mutableStateOf(Visibility.Public) }
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
            // Review content
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = composeContent,
                onValueChange = { composeContent = it },
                minLines = 5,
                label = { Text(stringResource(R.string.textfield_review)) },
            )
            // More settings
            AnimatedVisibility(visible = isShowMoreSettings) {
                MoreSettingsContent(
                    postDate = postDate.toDateString(),
                    isPostToFedi = isPostToFedi,
                    onTogglePostToFedi = { isPostToFedi = it },
                    onShowDatePicker = onShowDatePicker,
                    postVisibility = postVisibility,
                    onSetVisibility = { postVisibility = it },
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                var ratingSliderValue by remember { mutableFloatStateOf(0f) }
                AnimatedVisibility(visible = selectedShelfTypeIndex != 0) {
                    Slider(
                        value = ratingSliderValue,
                        valueRange = 0f..10f,
                        steps = 9,
                        onValueChange = { ratingSliderValue = it },
                        track = {
                            StarsWithScores(
                                size = 36,
                                rating = ratingSliderValue,
                                showScores = false,
                            )
                        },
                        thumb = {}, // clear thumb on the bottom
                    )
                }
                Spacer(Modifier.weight(1f))
                // Toggle showing more settings
                IconToggleButton(
                    checked = isShowMoreSettings,
                    onCheckedChange = { isShowMoreSettings = it },
                ) {
                    if (isShowMoreSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Show more settings",
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Show more settings",
                        )
                    }
                }
                // Send
                Button(
                    onClick = {
                        onSend(
                            MarkInSchema(
                                shelfType = shelfTypes[selectedShelfTypeIndex],
                                visibility = postVisibility.ordinal,
                                commentText = composeContent,
                                ratingGrade = ratingSliderValue.toInt(),
                                tags = emptyList(), // TODO
                                createdTime = postDate.toString(),
                                postToFediverse = isPostToFedi,
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
}

@Composable
private fun MoreSettingsContent(
    postDate: String,
    modifier: Modifier = Modifier,
    isPostToFedi: Boolean = false,
    onTogglePostToFedi: (Boolean) -> Unit = {},
    onShowDatePicker: () -> Unit = {},
    postVisibility: Visibility = Visibility.Public,
    onSetVisibility: (Visibility) -> Unit = {},
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = postDate,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.textfield_postdate)) },
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = {}) {
                    Icon(
                        painter = painterResource(R.drawable.calendar_outline),
                        contentDescription = "Select post date",
                        modifier = Modifier.clickable(onClick = onShowDatePicker),
                    )
                }
            },
        )
        // FIXME: they are ugly
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isPostToFedi) {
                Button(onClick = { onTogglePostToFedi(false) }) {
                    Text(stringResource(R.string.toggle_postToFedi))
                }
            } else {
                OutlinedButton(onClick = { onTogglePostToFedi(true) }) {
                    Text(stringResource(R.string.toggle_postToFedi))
                }
            }
            // Set visibility
            var isShowVisibilityMenu by remember { mutableStateOf(false) }
            Box {
                AnimatedContent(targetState = postVisibility) {
                    OutlinedButton(onClick = { isShowVisibilityMenu = true }) {
                        Text(stringResource(it.toR()))
                    }
                }
                DropdownMenu(
                    expanded = isShowVisibilityMenu,
                    onDismissRequest = { isShowVisibilityMenu = false },
                ) {
                    Visibility.entries.forEach {
                        DropdownMenuItem(
                            text = { Text(stringResource(it.toR())) },
                            onClick = {
                                isShowVisibilityMenu = false // dismiss the menu
                                onSetVisibility(it)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewComposeModal() {
    NeoDBYouTheme {
        Surface {
            ComposeModalContent(postDate = Clock.System.now())
        }
    }
}

@Preview
@Composable
private fun PreviewMoreSettingsContent() {
    NeoDBYouTheme {
        Surface {
            MoreSettingsContent(postDate = "2025-01-23")
        }
    }
}
