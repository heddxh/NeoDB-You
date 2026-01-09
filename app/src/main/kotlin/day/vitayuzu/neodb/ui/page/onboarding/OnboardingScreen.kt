package day.vitayuzu.neodb.ui.page.onboarding

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.valentinilk.shimmer.shimmer
import day.vitayuzu.neodb.R
import day.vitayuzu.neodb.data.schema.PublicInstanceSchema
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import day.vitayuzu.neodb.util.AUTH_CALLBACK
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onSkip: () -> Unit,
    paddingValues: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = uiState.currentPage,
        transitionSpec = {
            if (targetState > initialState) {
                slideInHorizontally { it } + fadeIn() togetherWith
                    slideOutHorizontally { -it } + fadeOut()
            } else {
                slideInHorizontally { -it } + fadeIn() togetherWith
                    slideOutHorizontally { it } + fadeOut()
            }
        },
        label = "onboarding_page_transition",
    ) { page ->
        when (page) {
            0 -> WelcomePage(
                onNext = { viewModel.goToNextPage() },
                onSkip = {
                    viewModel.completeOnboarding()
                    onSkip()
                },
            )

            1 -> InstanceSelectionPage(
                uiState = uiState,
                onSelectInstance = { viewModel.selectInstance(it) },
                onFetchInstanceInfo = { viewModel.fetchInstanceInfo(it) },
                onClearSelection = { viewModel.clearSelectedInstance() },
                getClientId = { viewModel.getClientId() },
                onBack = { viewModel.goToPreviousPage() },
                onResetOauthState = { viewModel.resetOauthState() },
            )
        }
    }
}

@Composable
private fun WelcomePage(
    onNext: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize().padding(24.dp),
    ) {
        Spacer(Modifier.weight(1f))

        Image(
            painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier.size(180.dp),
        )

        Spacer(Modifier.height(24.dp))

        Text(
            stringResource(R.string.onboarding_welcome_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(16.dp))

        Text(
            stringResource(R.string.onboarding_welcome_subtitle),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(32.dp))

        // Feature highlights
        FeatureItem(
            icon = R.drawable.outline_collections_bookmark_24,
            title = stringResource(R.string.onboarding_feature_track_title),
            description = stringResource(R.string.onboarding_feature_track_desc),
        )

        Spacer(Modifier.height(16.dp))

        FeatureItem(
            icon = R.drawable.star_empty,
            title = stringResource(R.string.onboarding_feature_review_title),
            description = stringResource(R.string.onboarding_feature_review_desc),
        )

        Spacer(Modifier.height(16.dp))

        FeatureItem(
            icon = R.drawable.internet_outline,
            title = stringResource(R.string.onboarding_feature_fediverse_title),
            description = stringResource(R.string.onboarding_feature_fediverse_desc),
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.onboarding_get_started))
        }

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = onSkip,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.onboarding_skip))
        }
    }
}

@Composable
private fun FeatureItem(
    icon: Int,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        Icon(
            painterResource(icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp),
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
            )
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(FlowPreview::class, ExperimentalMaterial3Api::class)
@Composable
private fun InstanceSelectionPage(
    uiState: OnboardingUiState,
    onSelectInstance: (PublicInstanceSchema) -> Unit,
    onFetchInstanceInfo: (String) -> Unit,
    onClearSelection: () -> Unit,
    getClientId: () -> Flow<String>,
    onBack: () -> Unit,
    onResetOauthState: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val textFieldState = rememberTextFieldState()
    var isInputting by remember { mutableStateOf(false) }
    val focus = LocalFocusManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(onFetchInstanceInfo) {
        snapshotFlow { textFieldState.text }
            .filter { Patterns.WEB_URL.matcher(it).matches() }
            .debounce(500)
            .collectLatest {
                Log.d("InstanceSelectionPage", "instanceUrl: $it")
                onFetchInstanceInfo(it.toString())
            }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(WindowInsets.ime.asPaddingValues()),
    ) {
        // Header
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
        ) {
            Text(
                stringResource(R.string.onboarding_select_instance_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.onboarding_select_instance_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // URL input field
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
                if (uiState.selectedInstanceUrl.isNotEmpty()) {
                    scope.launch {
                        launchAuthTab(context, uiState.selectedInstanceUrl, getClientId)
                    }
                }
                default()
            },
            label = { Text(stringResource(R.string.textfield_instanceUrl)) },
            supportingText = {
                Text(stringResource(R.string.onboarding_instance_input_hint))
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.internet_outline),
                    contentDescription = null,
                )
            },
            trailingIcon = {
                if (textFieldState.text.isNotEmpty()) {
                    IconButton(onClick = {
                        textFieldState.clearText()
                        onClearSelection()
                        focus.clearFocus()
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = null)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .onFocusChanged { isInputting = it.isFocused },
        )

        // Selected instance card (when custom URL is entered)
        AnimatedVisibility(
            visible = uiState.selectedInstanceUrl.isNotEmpty() &&
                isInputting &&
                !uiState.isFetchingInstanceInfo,
        ) {
            SelectedInstanceCard(
                instanceUrl = uiState.selectedInstanceUrl,
                name = uiState.selectedInstanceName,
                version = uiState.selectedInstanceVersion,
                userCount = uiState.selectedInstanceUserCount,
                isLoading = uiState.isPreparingOauth || uiState.isExchangingAccessToken,
                onClick = {
                    scope.launch {
                        launchAuthTab(context, uiState.selectedInstanceUrl, getClientId)
                    }
                },
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            )
        }

        // Loading indicator for custom URL
        AnimatedVisibility(visible = uiState.isFetchingInstanceInfo && isInputting) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        // Popular instances section
        Text(
            stringResource(R.string.onboarding_popular_instances),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 24.dp),
        )

        Spacer(Modifier.height(8.dp))

        if (uiState.isLoadingInstances) {
            // Loading shimmer
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 24.dp),
            ) {
                repeat(3) {
                    ShimmerInstanceCard()
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                modifier = Modifier.weight(1f),
            ) {
                items(uiState.publicInstances) { instance ->
                    PublicInstanceCard(
                        instance = instance,
                        isSelected = instance.domain == uiState.selectedInstanceUrl,
                        isLoading = instance.domain == uiState.selectedInstanceUrl &&
                            (uiState.isPreparingOauth || uiState.isExchangingAccessToken),
                        onClick = {
                            onSelectInstance(instance)
                            scope.launch {
                                launchAuthTab(context, instance.domain, getClientId)
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun PublicInstanceCard(
    instance: PublicInstanceSchema,
    isSelected: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        colors = if (isSelected) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            )
        } else {
            CardDefaults.cardColors()
        },
        modifier = modifier
            .fillMaxWidth()
            .then(if (isLoading) Modifier.shimmer() else Modifier),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    instance.title.ifEmpty { instance.domain },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    instance.domain,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (instance.description.isNotEmpty()) {
                    Text(
                        instance.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            "${instance.totalUsers}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    // Server version
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.spacedBy(4.dp),
//                    ) {
//                        Icon(
//                            Icons.Default.Info,
//                            contentDescription = null,
//                            modifier = Modifier.size(14.dp),
//                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
//                        )
//                        Text(
//                            instance.version,
//                            style = MaterialTheme.typography.labelSmall,
//                            color = MaterialTheme.colorScheme.onSurfaceVariant,
//                        )
//                    }
                }
            }
            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun SelectedInstanceCard(
    instanceUrl: String,
    name: String,
    version: String,
    userCount: Int,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        modifier = modifier
            .fillMaxWidth()
            .then(if (isLoading) Modifier.shimmer() else Modifier),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                name.ifEmpty { instanceUrl },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
            )
            Text(
                instanceUrl,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        "$userCount",
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        version,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.onboarding_tap_to_login),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun ShimmerInstanceCard(modifier: Modifier = Modifier) {
    val baseModifier = Modifier
        .clip(MaterialTheme.shapes.extraSmall)
        .shimmer()
        .background(Color.LightGray)

    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                Modifier
                    .height(20.dp)
                    .fillMaxWidth(.4f)
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
                    .height(32.dp)
                    .fillMaxWidth(.8f)
                    .then(baseModifier),
            )
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
        Log.e("OnboardingScreen", "Failed to get client id")
        return
    }
    val intent = CustomTabsIntent.Builder().build()
    val url = "https://$instanceUrl/oauth/authorize"
        .toUri()
        .buildUpon()
        .appendQueryParameter("response_type", "code")
        .appendQueryParameter("client_id", clientId)
        .appendQueryParameter("redirect_uri", AUTH_CALLBACK)
        .appendQueryParameter("scope", "read write")
        .build()
    intent.launchUrl(context, url)
}

@Preview
@Composable
private fun PreviewWelcomePage() {
    NeoDBYouTheme {
        WelcomePage(
            onNext = {},
            onSkip = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun PreviewPublicInstanceCard() {
    NeoDBYouTheme {
        PublicInstanceCard(
            instance = PublicInstanceSchema(
                domain = "neodb.social",
                version = "neodb/0.11.7.3",
                title = "NeoDB",
                description = "The official NeoDB instance",
                totalUsers = 22135,
            ),
            isSelected = false,
            isLoading = false,
            onClick = {},
        )
    }
}
