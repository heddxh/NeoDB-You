package day.vitayuzu.neodb.ui.page.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
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
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import day.vitayuzu.neodb.R
import day.vitayuzu.neodb.data.schema.MarkInSchema
import day.vitayuzu.neodb.ui.component.StarsWithScores
import day.vitayuzu.neodb.ui.model.Mark
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import day.vitayuzu.neodb.util.ShelfType
import day.vitayuzu.neodb.util.Visibility
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun PostComposeModal(
    modifier: Modifier = Modifier,
    originMark: Mark? = null,
    onDismiss: () -> Unit = {},
    onSend: (MarkInSchema) -> Unit = {},
) {
    // Avoid last row to be covered by navigation bar when open more settings
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded),
    )
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        modifier = modifier,
    ) {
        val scope = rememberCoroutineScope()
        ComposeModalContent(
            modifier = Modifier.safeContentPadding(),
            originMark = originMark,
            onSend = {
                onSend(it)
                scope.launch {
                    sheetState.hide()
                    onDismiss()
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
private fun ComposeModalContent(
    modifier: Modifier = Modifier,
    originMark: Mark? = null,
    onSend: (MarkInSchema) -> Unit = {},
) {
    val shelfTypes = ShelfType.entries

    var isShowMoreSettings by rememberSaveable { mutableStateOf(false) }
    var isShowDatePicker by rememberSaveable { mutableStateOf(false) }

    var selectedShelfTypeIndex by rememberSaveable {
        mutableIntStateOf(originMark?.shelfType?.ordinal ?: 2)
    }

    val commentState = rememberTextFieldState(originMark?.comment ?: "")

    var ratingSliderValue by rememberSaveable {
        mutableFloatStateOf(
            originMark?.rating?.toFloat() ?: 0f,
        )
    }

    var postDate by rememberSaveable {
        mutableStateOf(
            (originMark?.date ?: Clock.System.todayIn(TimeZone.currentSystemDefault())),
        )
    }

    var isPostToFedi by rememberSaveable { mutableStateOf(false) }

    var postVisibility by rememberSaveable { mutableStateOf(Visibility.Public) }

    if (isShowDatePicker) {
        DatePickerModal(
            initialDate = postDate,
            onConfirm = {
                postDate = Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC).date
            },
            onDismiss = { isShowDatePicker = false },
        )
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // BUG: https://issuetracker.google.com/issues/436988693
        // FIXME: Long text will cause padding between first icon and the outer button too small
        SingleChoiceSegmentedButtonRow(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        ) {
            shelfTypes.forEachIndexed { index, button ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index, shelfTypes.size),
                    onClick = { selectedShelfTypeIndex = index },
                    selected = selectedShelfTypeIndex == index,
                ) {
                    Text(stringResource(button.toR()), softWrap = false)
                }
            }
        }
        // Review content
        OutlinedTextField(
            state = commentState,
            lineLimits = TextFieldLineLimits.MultiLine(minHeightInLines = 5),
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.textfield_review)) },
        )
        // More settings
        AnimatedVisibility(visible = isShowMoreSettings) {
            MoreSettingsContent(
                postDate = postDate.toString(),
                isPostToFedi = isPostToFedi,
                onTogglePostToFedi = { isPostToFedi = it },
                onShowDatePicker = { isShowDatePicker = true },
                postVisibility = postVisibility,
                onSetVisibility = { postVisibility = it },
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val haptic = LocalHapticFeedback.current
            AnimatedVisibility(visible = selectedShelfTypeIndex != 0) {
                Slider(
                    value = ratingSliderValue,
                    valueRange = 0f..10f,
                    steps = 9,
                    onValueChange = {
                        haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                        ratingSliderValue = it
                    },
                    track = {
                        StarsWithScores(
                            size = 36,
                            rating = ratingSliderValue,
                            showScores = false,
                            starSpace = 4,
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
                            commentText = commentState.text.toString(),
                            ratingGrade = ratingSliderValue.toInt(),
                            tags = emptyList(), // TODO: allow add tags
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
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = postDate,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.textfield_postdate)) },
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = onShowDatePicker) {
                    Icon(
                        painter = painterResource(R.drawable.calendar_outline),
                        contentDescription = "Select post date",
                    )
                }
            },
        )
        // FIXME: they are ugly
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(isPostToFedi, { onTogglePostToFedi(it) })
                Text(stringResource(R.string.toggle_postToFedi))
            }
            // Set visibility
            var isShowVisibilityMenu by rememberSaveable { mutableStateOf(false) }
            Box {
                AnimatedContent(targetState = postVisibility) {
                    OutlinedButton(onClick = { isShowVisibilityMenu = true }) {
                        Text(stringResource(it.toR()))
                        Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                        Icon(Icons.Default.MoreVert, null, Modifier.size(ButtonDefaults.IconSize))
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

@Composable
private fun DatePickerModal(
    initialDate: LocalDate,
    onConfirm: (Long) -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    val datePickerState =
        rememberDatePickerState(
            initialSelectedDateMillis = initialDate
                .atStartOfDayIn(TimeZone.UTC)
                .toEpochMilliseconds(),
        )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onConfirm(datePickerState.selectedDateMillis!!)
                onDismiss()
            }) {
                Text(stringResource(R.string.common_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.common_cancel))
            }
        },
    ) {
        DatePicker(state = datePickerState, showModeToggle = false)
    }
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
private fun PreviewComposeModal() {
    NeoDBYouTheme {
        Surface {
            ComposeModalContent()
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
