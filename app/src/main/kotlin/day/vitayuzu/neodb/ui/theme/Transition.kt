package day.vitayuzu.neodb.ui.theme

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import kotlin.math.roundToInt

/**
 * @author https://github.com/ReadYouApp/ReadYou/blob/main/app/src/main/java/me/ash/reader/ui/motion/ExpressiveTransitions.kt
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun sharedXAxisTransition(direction: MotionHorizontallyDirection): ContentTransform {
    val distanceRatio = when (direction) {
        MotionHorizontallyDirection.Forward -> 0.1f
        MotionHorizontallyDirection.Backward -> -0.1f
    }
    val animationDuration = 300
    val exit = (animationDuration * .35f).roundToInt()
    val enter = animationDuration - exit
    return (
        slideInHorizontally(
            initialOffsetX = { (it * distanceRatio).toInt() },
            animationSpec = tween(
                durationMillis = animationDuration,
                easing = FastOutSlowInEasing,
            ),
        ) + fadeIn(
            animationSpec = tween(
                delayMillis = exit,
                durationMillis = enter,
                easing = LinearOutSlowInEasing,
            ),
        )
    ) togetherWith (
        slideOutHorizontally(
            targetOffsetX = { (it * -distanceRatio).toInt() },
            animationSpec = tween(
                durationMillis = animationDuration,
                easing = FastOutSlowInEasing,
            ),
        ) + fadeOut(tween(durationMillis = exit, easing = FastOutLinearInEasing))
    )
}

enum class MotionHorizontallyDirection { Forward, Backward }
