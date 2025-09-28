package day.vitayuzu.neodb.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import day.vitayuzu.neodb.ui.theme.kindColors
import day.vitayuzu.neodb.util.EntryType

@Composable
fun EntryTypeText(type: EntryType, modifier: Modifier = Modifier) {
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val color = MaterialTheme.colorScheme.kindColors(type)
    Box(modifier) {
        textLayoutResult?.let {
            val width = with(LocalDensity.current) { it.size.width.toDp() }
            val height = with(LocalDensity.current) { it.size.height.toDp() / 8 }
            val offset = with(LocalDensity.current) { (it.lastBaseline - it.size.height).toDp() }
            val shape = MaterialTheme.shapes.extraLarge
            Box(
                Modifier
                    .size(width, height)
                    .align(Alignment.BottomCenter)
                    .offset(y = offset * 0.7f)
                    .dropShadow(
                        shape = shape,
                        shadow = Shadow(
                            radius = 8.dp,
                            color = Color(0x4D000000),
                            offset = DpOffset(x = 0.dp, 2.dp),
                        ),
                    ).background(color, shape),
            )
        }
        Text(
            text = stringResource(type.toR()),
            onTextLayout = { textLayoutResult = it },
        )
    }
}

@Preview
@Composable
private fun Preview() {
    NeoDBYouTheme {
        Column {
            ProvideTextStyle(
                MaterialTheme.typography.headlineLarge,
            ) { EntryTypeText(EntryType.game) }
            ProvideTextStyle(
                MaterialTheme.typography.titleLarge,
            ) { EntryTypeText(EntryType.game) }
            ProvideTextStyle(
                MaterialTheme.typography.bodyLarge,
            ) { EntryTypeText(EntryType.game) }
        }
    }
}
