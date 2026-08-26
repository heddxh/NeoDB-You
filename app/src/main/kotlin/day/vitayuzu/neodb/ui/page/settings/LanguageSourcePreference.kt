@file:Suppress("MayBeConstant")

package day.vitayuzu.neodb.ui.page.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButtonColors
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import day.vitayuzu.neodb.R
import day.vitayuzu.neodb.data.ContentLanguageSource
import day.vitayuzu.neodb.ui.component.ConnectedButtonGroup
import day.vitayuzu.neodb.ui.component.connectedShapes
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import day.vitayuzu.neodb.util.Supported_Languages
import java.util.Locale

@Composable
fun LanguageSourcePreference(
    selectedSource: ContentLanguageSource,
    onSourceChange: (ContentLanguageSource) -> Unit,
    selectedLanguage: String,
    onLanguageChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        val entries = ContentLanguageSource.entries
        ConnectedButtonGroup(Modifier.fillMaxWidth().selectableGroup()) {
            entries.forEachIndexed { index, source ->
                item(
                    checked = source == selectedSource,
                    onCheckedChange = { onSourceChange(source) },
                    modifier = Modifier.semantics { role = Role.RadioButton },
                    shapes = if (source == ContentLanguageSource.User) {
                        connectedShapes(
                            isFirst = index == 0,
                            isLast = index == entries.lastIndex,
                            attachedCorner = AttachedCorner,
                        )
                    } else {
                        null
                    },
                ) { Text(stringResource(source.toR())) }
            }
        }
        AnimatedVisibility(
            visible = selectedSource == ContentLanguageSource.User,
            enter = expandVertically(MaterialTheme.motionScheme.fastSpatialSpec()) +
                fadeIn(MaterialTheme.motionScheme.fastEffectsSpec()),
            exit = shrinkVertically(MaterialTheme.motionScheme.fastSpatialSpec()) +
                fadeOut(MaterialTheme.motionScheme.fastEffectsSpec()),
        ) {
            LanguagePanel(
                languages = Supported_Languages,
                selected = selectedLanguage,
                onSelect = onLanguageChange,
                modifier = Modifier.padding(top = PanelGap),
            )
        }
    }
}

@Composable
private fun LanguagePanel(
    languages: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val chipColors = languages.map { languageChipColors(it == selected) }
    // Scroll the selected chip into view when the panel opens or selection changes.
    val selectedChipRequester = remember { BringIntoViewRequester() }
    LaunchedEffect(selected) { selectedChipRequester.bringIntoView() }
    ConnectedButtonGroup(
        modifier = modifier
            .fillMaxWidth()
            .clip(PanelShape)
            .background(MaterialTheme.colorScheme.primary)
            .horizontalScroll(rememberScrollState())
            .padding(PanelPadding)
            .width(IntrinsicSize.Max)
            .selectableGroup(),
    ) {
        languages.forEachIndexed { index, tag ->
            item(
                checked = tag == selected,
                onCheckedChange = { onSelect(tag) },
                modifier = Modifier
                    .semantics { role = Role.RadioButton }
                    .then(
                        if (tag == selected) {
                            Modifier.bringIntoViewRequester(selectedChipRequester)
                        } else {
                            Modifier
                        },
                    ),
                colors = chipColors[index],
            ) {
                Text(tag.toDisplayName(), maxLines = 1)
            }
        }
    }
}

/**
 * Colors of a language button inside [LanguagePanel], inverted against the
 * primary-colored panel: checked pops as surface, unchecked is a subtle tint.
 */
@Composable
private fun languageChipColors(checked: Boolean): ToggleButtonColors {
    val containerColor by animateColorAsState(
        targetValue = if (checked) {
            MaterialTheme.colorScheme.surface
        } else {
            MaterialTheme.colorScheme.onPrimary
                .copy(alpha = ChipTintRatio)
                .compositeOver(MaterialTheme.colorScheme.primary)
        },
        animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
        label = "Chip container color",
    )
    val contentColor by animateColorAsState(
        targetValue = if (checked) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onPrimary.copy(alpha = ChipContentAlpha)
        },
        animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
        label = "Chip content color",
    )
    return ToggleButtonDefaults.tonalToggleButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        checkedContainerColor = containerColor,
        checkedContentColor = contentColor,
    )
}

/** String resource of the label for a [ContentLanguageSource] option. */
private fun ContentLanguageSource.toR(): Int = when (this) {
    ContentLanguageSource.Server -> R.string.settings_preference_languageSource_server
    ContentLanguageSource.App -> R.string.settings_preference_languageSource_app
    ContentLanguageSource.User -> R.string.settings_preference_languageSource_user
}

/** Native display name of a BCP-47 language tag, e.g. "zh-hans" -> "简体中文". */
private fun String.toDisplayName(): String = with(Locale.forLanguageTag(this)) {
    getDisplayName(this).replaceFirstChar { it.titlecase(this) }
}

// Corner shared by the User button and the panel, tighter than the group's corners.
private val AttachedCorner = 4.dp
private val PanelCorner = 12.dp
private val PanelGap = 4.dp
private val PanelPadding = 8.dp
private val PanelShape = RoundedCornerShape(
    topStart = PanelCorner,
    topEnd = AttachedCorner,
    bottomEnd = PanelCorner,
    bottomStart = PanelCorner,
)
private val ChipTintRatio = 0.13f
private val ChipContentAlpha = 0.85f

@PreviewLightDark
@Composable
private fun PreviewLanguageSourcePreference() {
    NeoDBYouTheme {
        Surface {
            var source by remember { mutableStateOf(ContentLanguageSource.User) }
            var lang by remember { mutableStateOf("zh-hans") }
            LanguageSourcePreference(
                selectedSource = source,
                onSourceChange = { source = it },
                selectedLanguage = lang,
                onLanguageChange = { lang = it },
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
