package day.vitayuzu.neodb.ui.page.settings

import android.content.Intent
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import day.vitayuzu.neodb.OauthActivity
import day.vitayuzu.neodb.R
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme

@Composable
fun SettingsPage(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.refresh()
    }

    val context = LocalContext.current

    Surface(modifier = modifier.fillMaxSize()) {
        if (uiState.isLogin) {
            UserProfilePart(
                avatar = uiState.avatar,
                username = uiState.username,
                fediAccount = uiState.fediAccount,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                logOut = viewModel::logout,
            ) {
                try {
                    val intent = CustomTabsIntent.Builder().build() // TODO: warmup
                    intent.launchUrl(context, uiState.url.toUri())
                } catch (e: Exception) {
                    Log.e("SettingsPage", "Failed to open ${uiState.url}", e)
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                Button(
                    modifier = Modifier.align(Alignment.Center),
                    onClick = {
                        context.startActivity(Intent(context, OauthActivity::class.java))
                    },
                ) { Text("Log in") }
            }
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
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        AsyncImage(
            model = avatar,
            contentDescription = "Your avatar image",
            placeholder = painterResource(R.drawable.avatar_placeholder),
            fallback = painterResource(R.drawable.avatar_placeholder),
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
        Row(
            modifier = Modifier.width(IntrinsicSize.Max).padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        ) {
            // Account Setting
            Button(
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
                onClick = openAccountSetting,
            ) {
                Icon(Icons.Default.AccountBox, null)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Account setting", softWrap = false)
            }
            // Log Out
            Button(
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.error,
                ),
                onClick = logOut,
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, null)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Log out", softWrap = false)
            }
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
