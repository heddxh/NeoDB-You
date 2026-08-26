package day.vitayuzu.neodb.ui.page.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import day.vitayuzu.neodb.ui.component.ConnectedButtonGroup
import day.vitayuzu.neodb.ui.component.ConnectedButtonGroupScope

object SelectablePreferenceCard {
    @Composable
    fun <T> Single(
        options: List<T>,
        selected: T,
        onSelectedChange: (T) -> Unit,
        shape: Shape,
        modifier: Modifier = Modifier,
        color: Color = CardDefaults.cardColors().containerColor,
        title: @Composable () -> Unit = {},
        optionContent: @Composable (T) -> Unit = {},
    ) {
        Card(
            modifier = modifier,
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = color),
        ) {
            val scrollState = rememberScrollState()
            LaunchedEffect(scrollState, selected) {
                if (options.first() == selected) {
                    scrollState.animateScrollTo(0)
                } else if (options.last() == selected) {
                    scrollState.animateScrollTo(scrollState.maxValue)
                }
            }

            title()
            BoxWithConstraints {
                ConnectedButtonGroup(
                    options = options,
                    selected = selected,
                    onSelectedChange = onSelectedChange,
                    optionContent = optionContent,
                    checkedWeight = 1.4f,
                    modifier = Modifier
                        .horizontalScroll(scrollState)
                        .padding(start = 56.dp, end = 8.dp, bottom = 8.dp)
                        .width(IntrinsicSize.Max)
                        .widthIn(min = maxWidth - 64.dp),
                )
            }
        }
    }

    @Composable
    fun Multi(
        modifier: Modifier = Modifier,
        shape: Shape,
        title: @Composable () -> Unit = {},
        content: ConnectedButtonGroupScope.() -> Unit,
    ) {
        Card(
            shape = shape,
            modifier = modifier,
        ) {
            title()
            ConnectedButtonGroup(
                columns = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(start = 56.dp, bottom = 8.dp, end = 8.dp),
            ) {
                content()
            }
        }
    }
}
