package day.vitayuzu.neodb.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import day.vitayuzu.neodb.R
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme

/**
 * Expandable [Text] composable with clean ellipsis truncation and a dedicated non-overlapping
 * expand/collapse footer row (>= 48.dp touch target).
 */
@Composable
fun ExpandableText(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    @Suppress("UNUSED_PARAMETER") animationSpec: AnimationSpec<Int>? = null,
    @Suppress("UNUSED_PARAMETER") overlayColor: Color = MaterialTheme.colorScheme.background,
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
    overflow: TextOverflow = TextOverflow.Ellipsis,
    softWrap: Boolean = true,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalTextStyle.current,
) {
    var expanded by rememberSaveable(text) { mutableStateOf(false) }
    var isExpandable by rememberSaveable(text, maxLines) { mutableStateOf(false) }
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "expand_arrow_rotation",
    )

    Column(
        modifier = modifier.animateContentSize(),
    ) {
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
            maxLines = if (expanded) Int.MAX_VALUE else maxLines,
            minLines = minLines,
            style = style,
            onTextLayout = { result ->
                if (!expanded && result.hasVisualOverflow) {
                    isExpandable = true
                }
                onTextLayout?.invoke(result)
            },
        )
        if (isExpandable) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .clip(MaterialTheme.shapes.small)
                    .clickable { expanded = !expanded },
            ) {
                Text(
                    text = stringResource(
                        if (expanded) R.string.collapse_text else R.string.expand_text,
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Icon(
                    painter = painterResource(R.drawable.expand_indicator_vector),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(16.dp)
                        .graphicsLayer { rotationZ = arrowRotation },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewExpandableText() {
    NeoDBYouTheme {
        Column(modifier = Modifier.size(200.dp)) {
            ExpandableText(
                "This is a long review comment that spans multiple lines to verify that the expand button sits cleanly below the truncated text.",
                maxLines = 2,
            )
        }
    }
}
