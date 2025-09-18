package day.vitayuzu.neodb.ui.page.settings

import android.content.Intent
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import day.vitayuzu.neodb.BuildConfig
import day.vitayuzu.neodb.OauthActivity
import day.vitayuzu.neodb.R
import day.vitayuzu.neodb.data.AppSettings
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import day.vitayuzu.neodb.util.AppNavigator
import day.vitayuzu.neodb.util.LocalNavigator

@Composable
fun SettingsPage(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    bottomBar: @Composable () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(modifier, bottomBar = bottomBar) {
        // WORKAROUND: Only add 8dp padding to UserProfilePart to avoid left button wrapping in English locale
        Column(
            modifier = Modifier
                .padding(it)
                .consumeWindowInsets(it)
                .padding(horizontal = 8.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            if (uiState.isLogin) { // logged in
                UserProfilePart(
                    avatar = uiState.avatar,
                    username = uiState.username,
                    fediAccount = uiState.fediAccount,
                    modifier = Modifier.fillMaxWidth(),
                    logOut = viewModel::logout,
                    instanceUrl = uiState.url,
                )
            } else { // need login
                LoginPart(Modifier.padding(horizontal = 8.dp))
            }
            AboutCard(
                Modifier.padding(horizontal = 8.dp),
                newVersionUrl = uiState.newVersionUrl,
                checkUpdate = viewModel::checkUpdate,
            )
            ExperimentalCard(
                Modifier.padding(horizontal = 8.dp).padding(bottom = 8.dp),
                appSettings = uiState.appSettings,
                onClearAuthData = viewModel::logout,
                onToggleVerboseLog = viewModel::onToggleVerboseLog,
            )
        }
    }
}

@Preview
@Composable
private fun LoginPart(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Card(
        modifier = modifier,
        onClick = { context.startActivity(Intent(context, OauthActivity::class.java)) },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(R.drawable.ic_launcher_foreground), "Logo")
            Text(
                stringResource(R.string.settings_logincard_welcomewords),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f).padding(8.dp),
            )
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, Modifier.padding(end = 8.dp))
        }
    }
}

@Composable
private fun UserProfilePart(
    avatar: String?,
    username: String,
    fediAccount: String?,
    modifier: Modifier = Modifier,
    logOut: () -> Unit = {},
    instanceUrl: String = "",
) {
    val context = LocalContext.current
    val intent = CustomTabsIntent.Builder().build()
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AsyncImage(
            model = avatar,
            contentDescription = "Your avatar image",
            placeholder = painterResource(R.drawable.avatar_placeholder),
            error = painterResource(R.drawable.avatar_placeholder),
            modifier = Modifier.clip(MaterialTheme.shapes.small),
        )
        Text(
            text = username,
            style = MaterialTheme.typography.titleMedium,
        )
        if (fediAccount != null) {
            Text(
                text = fediAccount,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            )
        }
        // Button row
        // WORKAROUND: Disable minimum interactive size for button to avoid excessive height
        //  see: https://developer.android.com/develop/ui/compose/accessibility/api-defaults#minimum-target-sizes
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
            Row(
                modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .height(IntrinsicSize.Min)
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            ) {
                // Account Settings
                Button(
                    onClick = { intent.launchUrl(context, instanceUrl.toUri()) },
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
                ) {
                    Icon(Icons.Default.AccountBox, null)
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(stringResource(R.string.settings_account_settings))
                }
                // Log Out
                Button(
                    onClick = logOut,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, null)
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(stringResource(R.string.settings_account_logOut))
                }
            }
        }
    }
}

@Preview
@Composable
private fun AboutCard(
    modifier: Modifier = Modifier,
    newVersionUrl: String? = null,
    checkUpdate: () -> Unit = {},
) {
    val context = LocalContext.current
    val intent = CustomTabsIntent.Builder().build()
    val appNavigator = LocalNavigator.current
    Column(modifier = modifier) {
        Text(stringResource(R.string.settings_title_about), Modifier.alpha(.6f).padding(8.dp))
        // Main Card
        Card {
            val itemColors = ListItemDefaults.colors().copy(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            )
            // Version
            ListItem(
                colors = itemColors,
                headlineContent = { Text(stringResource(R.string.settings_about_version)) },
                trailingContent = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (newVersionUrl != null) Text("Go to release page", Modifier.alpha(.6f))
                        Text(BuildConfig.VERSION_NAME)
                    }
                },
                leadingContent = {
                    BadgedBox(
                        badge = {
                            if (newVersionUrl != null) Badge()
                        },
                    ) {
                        Icon(Icons.Default.Info, null)
                    }
                },
                modifier = Modifier.clickable {
                    if (newVersionUrl != null) {
                        intent.launchUrl(context, newVersionUrl.toUri())
                    } else {
                        checkUpdate()
                    }
                },
            )
            // Developer
            ListItem(
                colors = itemColors,
                headlineContent = { Text(stringResource(R.string.settings_about_developer)) },
                trailingContent = { Text("@heddxh(Yuzu Vita)") },
                leadingContent = { Icon(Icons.Default.AccountCircle, null) },
            )
            ListItem(
                colors = itemColors,
                headlineContent = { Text("GitHub") },
                trailingContent = {
                    Icon(
                        painterResource(R.drawable.outline_arrow_outward_24),
                        "Go to GitHub repo",
                        Modifier.size(16.dp),
                    )
                },
                leadingContent = {
                    Icon(
                        painterResource(R.drawable.github_mark),
                        null,
                        // Editing GitHub trademark is not permitted.
                        // Add padding to make all icons the same size.
                        Modifier.size(24.dp).padding(2.dp),
                    )
                },
                modifier = Modifier.clickable {
                    intent.launchUrl(
                        context,
                        "https://github.com/heddxh/NeoDB-You".toUri(),
                    )
                },
            )
            ListItem(
                colors = itemColors,
                headlineContent = { Text(stringResource(R.string.settings_about_license)) },
                trailingContent = {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        "See open source license",
                        Modifier.size(16.dp),
                    )
                },
                leadingContent = { Icon(painterResource(R.drawable.baseline_balance_24), null) },
                modifier = Modifier.clickable { appNavigator goto AppNavigator.License },
            )
        }
    }
}

@Preview
@Composable
private fun ExperimentalCard(
    modifier: Modifier = Modifier,
    appSettings: AppSettings = AppSettings(),
    onClearAuthData: () -> Unit = {},
    onToggleVerboseLog: (Boolean) -> Unit = {},
) {
    Column(modifier = modifier) {
        Text(
            stringResource(R.string.settings_title_experimental),
            Modifier.alpha(.6f).padding(8.dp),
        )
        Card {
            val itemColors = ListItemDefaults.colors().copy(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                leadingIconColor = MaterialTheme.colorScheme.onErrorContainer,
                headlineColor = MaterialTheme.colorScheme.onErrorContainer,
            )
            ListItem(
                colors = itemColors,
                headlineContent = {
                    Text(stringResource(R.string.settings_experimental_clearAuth))
                },
                supportingContent = {
                    Text(stringResource(R.string.settings_experimental_clearAuth_supportText))
                },
                modifier = Modifier.clickable(onClick = onClearAuthData),
            )
            ListItem(
                colors = itemColors,
                headlineContent = {
                    Text(stringResource(R.string.settings_experimental_verbose))
                },
                supportingContent = {
                    Text(stringResource(R.string.settings_experimental_verbose_supportText))
                },
                trailingContent = {
                    Switch(checked = appSettings.verboseLog, onCheckedChange = onToggleVerboseLog)
                },
            )
            ListItem(
                colors = itemColors,
                headlineContent = {
                    Text(stringResource(R.string.settings_experimental_print))
                },
                modifier = Modifier.clickable {
                    Log.d("ExperimentalCard", appSettings.toString())
                },
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewUserAvatarAndName() {
    NeoDBYouTheme {
        Surface {
            UserProfilePart(
                avatar = null,
                username = "Heddxh",
                fediAccount = "@vita_yuzu_wine@neodb.social",
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
