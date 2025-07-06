package day.vitayuzu.neodb.ui.page.login

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.valentinilk.shimmer.shimmer
import day.vitayuzu.neodb.R
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        modifier = modifier.padding(horizontal = 16.dp).fillMaxSize(),
    ) {
        val textFieldState = rememberTextFieldState()
        var isInputting by remember { mutableStateOf(false) }

        LaunchedEffect(true) {
            snapshotFlow { textFieldState.text }
                .filter { Patterns.WEB_URL.matcher(it).matches() }
                .debounce(500)
                .collectLatest {
                    Log.d("LoginPage", "instanceUrl: $it")
                    viewModel.fetchInstanceInfo(it.toString())
                }
        }

        WelcomeWords(Modifier.fillMaxWidth())

        val focus = LocalFocusManager.current
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        AnimatedVisibility(uiState.isShowTextField) {
            OutlinedTextField(
                state = textFieldState,
                lineLimits = TextFieldLineLimits.SingleLine,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Go,
                ),
                onKeyboardAction = { default ->
                    if (uiState.url.isNotEmpty()) {
                        scope.launch {
                            launchAuthTab(context, uiState.url, viewModel::getClientId)
                        }
                    }
                    default()
                },
                outputTransformation = UrlOutputTransformation(),
                label = { Text(stringResource(R.string.textfield_instanceUrl)) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.internet_outline),
                        contentDescription = "Input instance address",
                    )
                },
                trailingIcon = {
                    IconButton(onClick = {
                        textFieldState.clearText()
                        focus.clearFocus()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged {
                        isInputting = it.isFocused
                    },
            )
        }

        // Show when textfield is hidden or focus on input.
        AnimatedVisibility(
            visible = isInputting || !uiState.isShowTextField,
            modifier = Modifier.fillMaxWidth().padding(WindowInsets.ime.asPaddingValues()),
        ) {
            if (uiState.isFetchingInstanceInfo) {
                ShimmerInstanceCard()
            } else {
                InstanceCard(
                    instanceUrl = uiState.url,
                    name = uiState.name,
                    version = uiState.version,
                    peopleCount = uiState.peopleCount,
                    isShimmering = uiState.isExchangingAccessToken || uiState.isPreparingOauth,
                    modifier = Modifier
                        .clickable {
                            scope.launch {
                                launchAuthTab(
                                    context,
                                    uiState.url,
                                    viewModel::getClientId,
                                )
                            }
                        },
                )
            }
        }
    }
}

private suspend fun launchAuthTab(
    context: Context,
    instanceUrl: String,
    getClientId: () -> Flow<String>,
) {
    val clientId = getClientId().firstOrNull()
    if (clientId == null) {
        Log.e("LoginPage", "Failed to get client id")
        return
    }
    val intent = androidx.browser.customtabs.CustomTabsIntent
        .Builder()
        .build()
    val url = "https://$instanceUrl/oauth/authorize"
        .toUri()
        .buildUpon()
        .appendQueryParameter("response_type", "code")
        .appendQueryParameter("client_id", clientId)
        .appendQueryParameter("redirect_uri", day.vitayuzu.neodb.util.AUTH_CALLBACK)
        // NOTE: Fuck it should no be "+" or it will be encoded to %2B
        .appendQueryParameter("scope", "read write")
        .build()
    intent.launchUrl(context, url)
}

/**
 * Auto append url scheme(https://) if it is missing.
 */
private class UrlOutputTransformation : OutputTransformation {
    override fun TextFieldBuffer.transformOutput() {
        if (length > 0 && !originalText.startsWith("http")) {
            insert(0, "https://")
        }
    }
}

@Composable
fun WelcomeWords(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        // TODO: Logo
        Image(
            painterResource(R.drawable.ic_launcher_foreground),
            null,
        )
        Text(
            stringResource(R.string.welcome_firstline),
            style = MaterialTheme.typography.displaySmall,
        )
        Text(
            stringResource(R.string.welcome_secondline),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun InstanceCard(
    instanceUrl: String,
    name: String,
    version: String,
    peopleCount: Int,
    modifier: Modifier = Modifier,
    isShimmering: Boolean = false, // show shimmer when handling oauth callback.
) {
    Card(
        shape = MaterialTheme.shapes.small,
        modifier = modifier.then(if (isShimmering) Modifier.shimmer() else Modifier),
    ) {
        Text(name, Modifier.padding(8.dp))
        Text(
            instanceUrl,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 8.dp).alpha(.8f),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(Icons.Default.Info, "Version", Modifier.size(12.dp))
                Text(version, style = MaterialTheme.typography.bodySmall)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(Icons.Default.Person, "Account number", Modifier.size(12.dp))
                Text(peopleCount.toString(), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun ShimmerInstanceCard(modifier: Modifier = Modifier) {
    val baseModifier =
        Modifier.clip(MaterialTheme.shapes.extraSmall).shimmer().background(Color.LightGray)

    Card(modifier = modifier, shape = MaterialTheme.shapes.small) {
        Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                Modifier
                    .height(20.dp)
                    .fillMaxWidth(.2f)
                    .then(baseModifier),
            )
            Box(
                Modifier
                    .height(16.dp)
                    .fillMaxWidth(.3f)
                    .then(baseModifier),
            )
            Box(
                Modifier
                    .height(20.dp)
                    .fillMaxWidth(.5f)
                    .then(baseModifier),
            )
        }
    }
}

@Preview
@Composable
private fun PreviewWelcomeWords() {
    WelcomeWords()
}

@PreviewLightDark
@Composable
private fun PreviewInstanceCard() {
    NeoDBYouTheme {
        InstanceCard(
            "neodb.social",
            "NeoDB",
            "neodb/0.11.7.3",
            22135,
            Modifier.fillMaxWidth(),
            true,
        )
    }
}

@Preview
@Composable
private fun PreviewShimmerInstanceCard() {
    ShimmerInstanceCard(Modifier.fillMaxWidth())
}
