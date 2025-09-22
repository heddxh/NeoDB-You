package day.vitayuzu.neodb.ui.component

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import day.vitayuzu.neodb.R

/**
 * A wrapper version of [Text] composable that can be expanded to show more text.
 * Optimized reduce recomposition during size animation.
 *
 * @param maxLines Control max number of lines when in collapsed state.
 * @param animationSpec The animation for expanding/collapsing, default is spring.
 * @see Text
 */
@Composable
fun ExpandableText(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    animationSpec: AnimationSpec<Int>? = null,
    overlayColor: Color = MaterialTheme.colorScheme.background,
    color: Color = Color.Unspecified,
    autoSize: TextAutoSize? = null,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalTextStyle.current,
) {
    BoxWithConstraints(modifier = modifier) {
        val textMeasurer = rememberTextMeasurer()

        val expandedHeightMeasuredSize = remember(style, textMeasurer, this.constraints) {
            textMeasurer.measure(text, style, constraints = this.constraints)
        }.size.height
        val collapsedHeightMeasuredSize = remember(style, textMeasurer, this.constraints) {
            textMeasurer.measure(text, style, constraints = this.constraints, maxLines = maxLines)
        }.size.height

        var state by rememberSaveable {
            mutableStateOf(
                if (expandedHeightMeasuredSize > collapsedHeightMeasuredSize) {
                    ExpandableTextState.Collapsed
                } else {
                    ExpandableTextState.None
                },
            )
        }

        var targetHeight by remember { mutableIntStateOf(collapsedHeightMeasuredSize) }
        var targetRotation by remember { mutableFloatStateOf(0f) }

        LaunchedEffect(state) {
            if (state == ExpandableTextState.Expanded) {
                targetHeight = expandedHeightMeasuredSize
                targetRotation = 180f
            } else if (state == ExpandableTextState.Collapsed) {
                targetHeight = collapsedHeightMeasuredSize
                targetRotation = 0f
            }
        }

        val animatedHeight by animationSpec?.let {
            animateIntAsState(targetHeight, animationSpec = it)
        } ?: animateIntAsState(targetHeight)

        val animatedRotation by animateFloatAsState(targetRotation)

        Column(
            modifier = Modifier
                .clip(MaterialTheme.shapes.extraSmall)
                .clickable(enabled = state != ExpandableTextState.None) {
                    if (state == ExpandableTextState.Expanded) {
                        state = ExpandableTextState.Collapsed
                    } else if (state == ExpandableTextState.Collapsed) {
                        state = ExpandableTextState.Expanded
                    }
                },
        ) {
            // Main text.
            Text(
                text = text,
                color = color,
                autoSize = autoSize,
                fontSize = fontSize,
                fontStyle = fontStyle,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                letterSpacing = letterSpacing,
                textDecoration = textDecoration,
                textAlign = textAlign,
                lineHeight = lineHeight,
                overflow = overflow,
                softWrap = softWrap,
                maxLines = Int.MAX_VALUE, // Constrain text height by Modifier.height
                minLines = minLines,
                style = style,
                onTextLayout = onTextLayout,
                modifier = Modifier
                    .padding(2.dp)
                    // Deferred state reading to layout phase.
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        layout(placeable.width, animatedHeight) {
                            placeable.placeRelative(0, 0)
                        }
                    },
            )
            // "Show More" indicator.
            if (state != ExpandableTextState.None) {
                val baseModifier = Modifier
                    .height(16.dp)
                    .fillMaxWidth()
                val backgroundModifier = Modifier.background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, overlayColor),
                    ),
                )
                Icon(
                    painterResource(R.drawable.expand_indicator_vector),
                    contentDescription = "Show more",
                    modifier = baseModifier
                        .then(
                            if (state == ExpandableTextState.Collapsed) {
                                backgroundModifier
                            } else {
                                Modifier
                            },
                        ).graphicsLayer {
                            rotationZ = animatedRotation
                        }, // Do not rotate background
                )
            }
        }
    }
}

private enum class ExpandableTextState { None, Expanded, Collapsed, }

@Preview
@Composable
private fun PreviewExpandableText() {
    Column(modifier = Modifier.size(100.dp)) {
        ExpandableText(
            "fooooooooooooooooooooooooooooooooooooooooooooooooo",
            maxLines = 1,
        )
    }
}
