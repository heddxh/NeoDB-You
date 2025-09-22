package day.vitayuzu.neodb.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity

/**
 * Add translucent background under status bar when scrolling.
 * See: https://developer.android.com/develop/ui/compose/system/system-bars
 */
@Suppress("ktlint:compose:modifier-missing-check")
@Composable
fun StatusBarProtection(
    scrollState: ScrollableState,
    color: Color = MaterialTheme.colorScheme.surfaceContainerLow,
) {
    AnimatedVisibility(scrollState.canScrollBackward) {
        val height = WindowInsets.statusBars.getTop(LocalDensity.current).toFloat()
        Canvas(Modifier.fillMaxSize()) {
            val gradient = Brush.verticalGradient(
                colors = listOf(
                    color.copy(alpha = 1f),
                    color.copy(alpha = .8f),
                    Color.Transparent,
                ),
                startY = 0f,
                endY = height,
            )
            drawRect(
                brush = gradient,
                size = Size(size.width, height),
            )
        }
    }
}
