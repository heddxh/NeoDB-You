package day.vitayuzu.neodb.ui.page.detail

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun PullRibbon(
    onPull: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val maxPullDistance = with(density) { 40.dp.toPx() }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val displayedOffset by animateFloatAsState(
        targetValue = dragOffset - maxPullDistance,
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
        label = "Pull ribbon offset",
    )
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

    val arrowRotated by animateFloatAsState(
        targetValue = dragOffset / maxPullDistance * 180f,
        animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
        label = "Pull ribbon arrow rotation",
    )

    val ribbonModifier = Modifier
        .offset { IntOffset(0, displayedOffset.roundToInt()) }
        .width(48.dp)
        .height(130.dp)
        .draggable(
            orientation = Orientation.Vertical,
            state = rememberDraggableState { delta ->
                dragOffset = (dragOffset + delta).coerceIn(0f, maxPullDistance)
            },
            onDragStopped = {
                val pulled = dragOffset >= maxPullDistance
                dragOffset = 0f
                if (pulled) onPull()
            },
        ).shadow(6.dp, ribbonShape)
        .clip(ribbonShape)
        .background(MaterialTheme.colorScheme.secondaryContainer)

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.align(Alignment.TopEnd).then(ribbonModifier),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .size(40.dp)
                    .alpha(.3f)
                    .rotate(arrowRotated),
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
