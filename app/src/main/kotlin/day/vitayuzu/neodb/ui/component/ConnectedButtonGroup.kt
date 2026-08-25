@file:Suppress("MayBeConstant")

package day.vitayuzu.neodb.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButtonColors
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.ToggleButtonShapes
import androidx.compose.material3.TonalToggleButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.takeOrElse
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme

@Composable
fun ConnectedButtonGroup(
    modifier: Modifier = Modifier,
    columns: Int = Int.MAX_VALUE,
    spacing: Dp = GroupSpacing,
    checkedWeight: Float = MultiCheckedWeight,
    content: ConnectedButtonGroupScope.() -> Unit,
) {
    val items = ConnectedButtonGroupScopeImpl().apply(content).items
    items.chunked(columns).forEach { row ->
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(spacing),
        ) {
            row.forEachIndexed { index, item ->
                ConnectedButton(
                    item = item,
                    isFirst = index == 0,
                    isLast = index == row.lastIndex,
                    checkedWeight = checkedWeight,
                )
            }
            if (columns != Int.MAX_VALUE) {
                val missing = columns - row.size
                if (missing > 0) {
                    Spacer(Modifier.weight(missing.toFloat()))
                }
            }
        }
    }
}

/**
 * Single-choice [ConnectedButtonGroup].
 *
 * [selected] is matched against [options] by structural equality ([Any.equals]),
 * so [T] should be a value type (enum, data class, String, ...) and [options]
 * should not contain duplicates.
 */
@Composable
fun <T> ConnectedButtonGroup(
    options: List<T>,
    selected: T,
    onSelectedChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    spacing: Dp = GroupSpacing,
    checkedWeight: Float = SingleCheckedWeight,
    accent: @Composable (T) -> Color = { MaterialTheme.colorScheme.primary },
    optionContent: @Composable (T) -> Unit,
) {
    val accents = options.map { accent(it) }
    ConnectedButtonGroup(
        modifier = modifier.selectableGroup(),
        spacing = spacing,
        checkedWeight = checkedWeight,
    ) {
        options.forEachIndexed { index, option ->
            item(
                checked = option == selected,
                onCheckedChange = { onSelectedChange(option) },
                modifier = Modifier.semantics { role = Role.RadioButton },
                accent = accents[index],
            ) { optionContent(option) }
        }
    }
}

interface ConnectedButtonGroupScope {
    /**
     * A toggle button of the group.
     *
     * @param accent container color when checked, also tinting the unchecked state.
     * [Color.Unspecified] falls back to the theme's primary color.
     * @param shapes overrides the default [connectedShapes], e.g. to tighten corners
     * toward a panel attached below the button. `null` uses the default.
     * @param colors overrides the default tonal colors, e.g. for a group placed on
     * a colored container where the accent-over-surface scheme doesn't work.
     * `null` uses the default derived from [accent].
     */
    fun item(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        accent: Color = Color.Unspecified,
        shapes: ToggleButtonShapes? = null,
        colors: ToggleButtonColors? = null,
        content: @Composable () -> Unit,
    )
}

private class ConnectedButtonGroupScopeImpl : ConnectedButtonGroupScope {
    val items = mutableListOf<ConnectedButtonItem>()

    override fun item(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        modifier: Modifier,
        enabled: Boolean,
        accent: Color,
        shapes: ToggleButtonShapes?,
        colors: ToggleButtonColors?,
        content: @Composable () -> Unit,
    ) {
        items +=
            ConnectedButtonItem(
                checked,
                onCheckedChange,
                modifier,
                enabled,
                accent,
                shapes,
                colors,
                content,
            )
    }
}

private class ConnectedButtonItem(
    val checked: Boolean,
    val onCheckedChange: (Boolean) -> Unit,
    val modifier: Modifier,
    val enabled: Boolean,
    val accent: Color,
    val shapes: ToggleButtonShapes?,
    val colors: ToggleButtonColors?,
    val content: @Composable () -> Unit,
)

private val GroupSpacing = 8.dp
private val CheckedCorner = 12.dp
private val OuterCorner = 12.dp
private val InnerCorner = 8.dp
private val PressedCorner = 16.dp
private val SingleCheckedWeight = 2f
private val MultiCheckedWeight = 1.4f
private val PressedWeight = 1.15f
private val PressedScale = 0.97f
private val UncheckedAccentRatio = 0.13f
private val UncheckedContentAccentRatio = 0.7f

@Composable
private fun RowScope.ConnectedButton(
    item: ConnectedButtonItem,
    isFirst: Boolean,
    isLast: Boolean,
    checkedWeight: Float,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val weight by animateFloatAsState(
        targetValue = when {
            item.checked -> checkedWeight
            pressed -> PressedWeight
            else -> 1f
        },
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
        label = "Width weight",
    )
    val scale by animateFloatAsState(
        targetValue = if (pressed) PressedScale else 1f,
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
        label = "Pressing scale",
    )

    TonalToggleButton(
        checked = item.checked,
        onCheckedChange = item.onCheckedChange,
        enabled = item.enabled,
        shapes = item.shapes ?: connectedShapes(isFirst, isLast),
        colors = item.colors ?: connectedColors(item.accent, item.checked),
        contentPadding = PaddingValues(0.dp),
        interactionSource = interactionSource,
        modifier = item.modifier
            // The spring may undershoot below the resting weight, but must stay positive.
            .weight(weight.coerceAtLeast(0.1f))
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
    ) { item.content() }
}

/**
 * Default shapes of a button in a [ConnectedButtonGroup].
 *
 * [attachedCorner] tightens the bottom corners of the pressed/checked shapes,
 * so the button reads as connected to a panel expanded right below it.
 */
internal fun connectedShapes(
    isFirst: Boolean,
    isLast: Boolean,
    attachedCorner: Dp = Dp.Unspecified,
): ToggleButtonShapes {
    val start = if (isFirst) OuterCorner else InnerCorner
    val end = if (isLast) OuterCorner else InnerCorner
    return ToggleButtonShapes(
        shape = RoundedCornerShape(
            topStart = start,
            bottomStart = start,
            topEnd = end,
            bottomEnd = end,
        ),
        pressedShape = RoundedCornerShape(
            topStart = PressedCorner,
            topEnd = PressedCorner,
            bottomEnd = attachedCorner.takeOrElse { PressedCorner },
            bottomStart = attachedCorner.takeOrElse { PressedCorner },
        ),
        checkedShape = RoundedCornerShape(
            topStart = CheckedCorner,
            topEnd = CheckedCorner,
            bottomEnd = attachedCorner.takeOrElse { CheckedCorner },
            bottomStart = attachedCorner.takeOrElse { CheckedCorner },
        ),
    )
}

@Composable
private fun connectedColors(accent: Color, checked: Boolean): ToggleButtonColors {
    val resolved = accent.takeOrElse { MaterialTheme.colorScheme.primary }
    val containerColor by animateColorAsState(
        targetValue = if (checked) {
            resolved
        } else {
            resolved
                .copy(alpha = UncheckedAccentRatio)
                .compositeOver(MaterialTheme.colorScheme.surface)
        },
        animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
        label = "Container color",
    )
    val contentColor by animateColorAsState(
        targetValue = if (checked) {
            MaterialTheme.colorScheme.contentColorFor(resolved).takeOrElse { Color.White }
        } else {
            lerp(MaterialTheme.colorScheme.onSurface, resolved, UncheckedContentAccentRatio)
        },
        animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
        label = "Content color",
    )
    return ToggleButtonDefaults.tonalToggleButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        checkedContainerColor = containerColor,
        checkedContentColor = contentColor,
    )
}

@Preview
@Composable
private fun ConnectedButtonGroupPreview() {
    NeoDBYouTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Single-select
            var selected by remember { mutableStateOf("周") }
            ConnectedButtonGroup(
                options = listOf("日", "周", "月", "年"),
                selected = selected,
                onSelectedChange = { selected = it },
            ) { Text(it) }
            // Multi-select through the DSL
            val labels = listOf("一", "二", "三", "四", "五")
            val checked = remember { mutableStateListOf(false, true, true, false, false) }
            ConnectedButtonGroup {
                labels.forEachIndexed { index, label ->
                    item(
                        checked = checked[index],
                        onCheckedChange = { checked[index] = it },
                    ) { Text(label) }
                }
            }
        }
    }
}
