package day.vitayuzu.neodb.ui.page.detail

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.graphics.vector.PathData
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import kotlin.math.roundToInt

@Composable
fun PullRibbon(
    onPull: () -> Unit,
    onLongPull: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val hapticFeedback = LocalHapticFeedback.current

    val ribbonShape = remember {
        GenericShape { size, _ ->
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height)
            lineTo(size.width / 2f, size.height - size.width * 0.24f)
            lineTo(0f, size.height)
            close()
        }
    }

    val maxPullDistance = with(density) { 36.dp.toPx() }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val displayedOffset by animateFloatAsState(
        targetValue = dragOffset - maxPullDistance,
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
        label = "Pull ribbon offset",
    )
    val isAtMax = dragOffset >= maxPullDistance

    var longPullTriggered by remember { mutableStateOf(false) }
    val currentOnLongPull by rememberUpdatedState(onLongPull)

    val longPullProgress by animateFloatAsState(
        targetValue = if (isAtMax) 1f else 0f,
        label = "Long pull progress",
        animationSpec = tween(durationMillis = 800, easing = LinearEasing),
        finishedListener = {
            if (it == 1f && isAtMax && !longPullTriggered) {
                longPullTriggered = true
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                currentOnLongPull()
            }
        },
    )

    val ribbonModifier = Modifier
        .offset { IntOffset(0, displayedOffset.roundToInt()) }
        .width(48.dp)
        .height(128.dp)
        .draggable(
            orientation = Orientation.Vertical,
            state = rememberDraggableState { delta ->
                dragOffset = (dragOffset + delta).coerceIn(0f, maxPullDistance)
            },
            onDragStarted = {
                longPullTriggered = false
            },
            onDragStopped = {
                val pulled = dragOffset >= maxPullDistance
                dragOffset = 0f
                if (pulled && !longPullTriggered) {
                    onPull()
                }
            },
        ).shadow(8.dp, ribbonShape)
        .clip(ribbonShape)
        .background(MaterialTheme.colorScheme.secondaryContainer)

    Box(modifier = modifier) {
        Box(
            modifier = Modifier.align(Alignment.TopEnd).then(ribbonModifier),
            contentAlignment = Alignment.BottomCenter,
        ) {
            val iconActiveColor = MaterialTheme.colorScheme.onSecondaryContainer
            val tint = iconActiveColor
                .copy(alpha = .3f)
                .compositeOver(MaterialTheme.colorScheme.secondaryContainer)

            Icon(
                painter = rememberVectorPainter(
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                    autoMirror = false,
                ) { _, _ ->
                    // Morph the chevron into the links between the three share nodes.
                    Path(
                        pathData = PathData {
                            moveTo(lerp(7f, 17f, longPullProgress), lerp(10f, 5f, longPullProgress))
                            lineTo(lerp(12f, 5f, longPullProgress), lerp(15f, 12f, longPullProgress))
                            lineTo(17f, lerp(10f, 19f, longPullProgress))
                        },
                        stroke = SolidColor(Color.Black),
                        strokeLineWidth = 2f,
                        strokeLineCap = StrokeCap.Round,
                        strokeLineJoin = StrokeJoin.Round,
                    )
                    Path(
                        pathData = PathData {
                            for ([x, y] in listOf(
                                lerp(7f, 17f, longPullProgress) to lerp(10f, 5f, longPullProgress),
                                lerp(12f, 5f, longPullProgress) to lerp(15f, 12f, longPullProgress),
                                17f to lerp(10f, 19f, longPullProgress),
                            )) {
                                moveTo(x + 2.5f, y)
                                arcToRelative(2.5f, 2.5f, 0f, true, true, -5f, 0f)
                                arcToRelative(2.5f, 2.5f, 0f, true, true, 5f, 0f)
                                close()
                            }
                        },
                        fill = SolidColor(Color.Black),
                        fillAlpha = longPullProgress,
                    )
                },
                contentDescription = null,
                tint = tint,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .size(40.dp)
                    .graphicsLayer {
                        compositingStrategy = CompositingStrategy.Offscreen
                        // Match the arrow's 12-unit span to the share icon's 19-unit span.
                        val scale = lerp(1f, 12f / 19f, longPullProgress)
                        scaleX = scale
                        scaleY = scale
                    }.drawWithContent {
                        drawContent()
                        drawRect(
                            color = iconActiveColor,
                            size = size.copy(width = size.width * longPullProgress),
                            blendMode = BlendMode.SrcAtop,
                        )
                    },
            )
        }

        content()

        Box(
            modifier = Modifier
                .alpha(0f)
                .align(Alignment.TopEnd)
                .then(ribbonModifier),
        )
    }
}

@Preview
@Composable
private fun PreviewPullRibbon() {
    NeoDBYouTheme {
        Box(Modifier.fillMaxSize()) {
            PullRibbon(
                onPull = {},
                onLongPull = {},
                modifier = Modifier.fillMaxSize(.5f),
            ) {}
        }
    }
}
