package day.vitayuzu.neodb.ui.theme

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.ui.unit.IntOffset

object MotionTheme {
    val springSpec = spring<IntOffset>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium,
    )

    fun slideHorizontally(direction: MotionNavigationDirection) =
        if (direction == MotionNavigationDirection.Forward) {
            slideInHorizontally(springSpec) { it } togetherWith
                slideOutHorizontally(springSpec) { -it }
        } else {
            slideInHorizontally(springSpec) { -it } togetherWith
                slideOutHorizontally(springSpec) { it }
        }
}

enum class MotionNavigationDirection { Forward, Backward }
