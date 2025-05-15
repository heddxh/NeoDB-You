package day.vitayuzu.neodb.ui.page.settings

import android.util.Log
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import day.vitayuzu.neodb.R

@Composable
fun SettingsPage(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val uriHandler = LocalUriHandler.current

    Surface(modifier = modifier.fillMaxSize()) {
        if (uiState.isLogin) {
            UserProfilePart(
                avatar = uiState.avatar,
                username = uiState.username,
                fediAccount = uiState.fediAccount,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            ) {
                try {
                    uriHandler.openUri(uiState.url) // FIXME: open in chrome tab
                } catch (e: Exception) {
                    Log.e("SettingsPage", "Failed to open ${uiState.url}", e)
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                Button(
                    modifier = Modifier.align(Alignment.Center),
                    onClick = {},
                ) { Text("Refresh") }
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
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceDim)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            )
        }
        // Button row
        Row(
            modifier = Modifier.width(IntrinsicSize.Max).padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        ) {
            Button(
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
                onClick = {
                    openAccountSetting()
                },
            ) {
                Icon(Icons.Default.AccountBox, null)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Account Setting", softWrap = false)
            }
            Button(
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.error,
                ),
                onClick = {},
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, null)
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Log out", softWrap = false)
            }
        }
    }
}

@Preview()
@Composable
private fun PreviewUserAvatarAndName() {
    UserProfilePart(
        avatar = null,
        username = "Heddxh",
        fediAccount = "@vita_yuzu_wine@neodb.social",
        modifier = Modifier.fillMaxWidth(),
    )
}
