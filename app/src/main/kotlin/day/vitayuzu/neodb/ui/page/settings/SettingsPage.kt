package day.vitayuzu.neodb.ui.page.settings

import android.content.Intent
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import day.vitayuzu.neodb.BuildConfig
import day.vitayuzu.neodb.OauthActivity
import day.vitayuzu.neodb.R
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme

@Composable
fun SettingsPage(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    openLicensePage: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Refresh everytime entering this page
    LaunchedEffect(true) {
        viewModel.refresh()
    }

    val context = LocalContext.current

    // WORKAROUND: Only add 8dp padding to UserProfilePart to avoid left button wrapping in English locale
    Column(modifier = modifier.padding(horizontal = 8.dp)) {
        if (uiState.isLogin) { // logged in
            UserProfilePart(
                avatar = uiState.avatar,
                username = uiState.username,
                fediAccount = uiState.fediAccount,
                modifier = Modifier.fillMaxWidth(),
                logOut = viewModel::logout,
            ) {
                try {
                    val intent = CustomTabsIntent.Builder().build() // TODO: warmup
                    intent.launchUrl(context, uiState.url.toUri())
                } catch (e: Exception) {
                    Log.e("SettingsPage", "Failed to open ${uiState.url}", e)
                }
            }
        } else { // need login
            LoginPart(Modifier.padding(horizontal = 8.dp))
        }
        AboutCard(
            Modifier.padding(horizontal = 8.dp),
            newVersionUrl = uiState.newVersionUrl,
            openLicensePage = openLicensePage,
            checkUpdate = viewModel::checkUpdate,
        )
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
                modifier = Modifier.weight(1f).padding(4.dp),
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
    openAccountSetting: () -> Unit = {},
) {
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
                    onClick = openAccountSetting,
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
    openLicensePage: () -> Unit = {},
    checkUpdate: () -> Unit = {},
) {
    val context = LocalContext.current
    val intent = CustomTabsIntent.Builder().build()
    Column(modifier = modifier) {
        Text("About App", Modifier.alpha(.6f).padding(8.dp))
        // Main Card
        Card(modifier = Modifier) {
            // Version
            AboutField(
                key = { Text(stringResource(R.string.settings_about_version)) },
                value = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (newVersionUrl != null) Text("Go to release page", Modifier.alpha(.6f))
                        Text(BuildConfig.VERSION_NAME)
                    }
                },
                icon = {
                    BadgedBox(
                        badge = {
                            if (newVersionUrl != null) Badge()
                        },
                    ) {
                        Icon(Icons.Default.Info, null)
                    }
                },
                modifier = Modifier.fillMaxWidth().clickable {
                    if (newVersionUrl != null) {
                        intent.launchUrl(context, newVersionUrl.toUri())
                    } else {
                        checkUpdate()
                    }
                },
            )
            // Developer
            AboutField(
                key = { Text(stringResource(R.string.settings_about_developer)) },
                value = { Text("@heddxh(Yuzu Vita)") },
                icon = { Icon(Icons.Default.AccountCircle, null) },
                modifier = Modifier.fillMaxWidth(),
            )
            AboutField(
                key = { Text("GitHub") },
                value = {
                    Icon(
                        painterResource(R.drawable.outline_arrow_outward_24),
                        "Go to GitHub repo",
                        Modifier.size(16.dp),
                    )
                },
                icon = {
                    Icon(
                        painterResource(R.drawable.github_mark),
                        null,
                        // Editing GitHub trademark is not permitted.
                        // Add padding to make all icons the same size.
                        Modifier.padding(2.dp),
                    )
                },
                modifier = Modifier.fillMaxWidth().clickable {
                    intent.launchUrl(
                        context,
                        "https://github.com/heddxh/NeoDB-You".toUri(),
                    )
                },
            )
            AboutField(
                key = { Text(stringResource(R.string.settings_about_license)) },
                value = {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        "See open source license",
                        Modifier.size(16.dp),
                    )
                },
                icon = { Icon(painterResource(R.drawable.baseline_balance_24), null) },
                modifier = Modifier.fillMaxWidth().clickable(onClick = openLicensePage),
            )
        }
    }
}

@Composable
fun AboutField(
    key: @Composable () -> Unit,
    value: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(24.dp), Alignment.Center) {
            icon()
        }
        Spacer(Modifier.width(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            key()
            value()
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
