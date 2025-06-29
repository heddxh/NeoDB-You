package day.vitayuzu.neodb.ui.page.login

import android.util.Log
import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.valentinilk.shimmer.shimmer
import day.vitayuzu.neodb.R
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter

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
        var instanceUrl by remember { mutableStateOf("") }

        LaunchedEffect(true) {
            snapshotFlow { instanceUrl }
                .filter { Patterns.WEB_URL.matcher(it).matches() }
                .debounce(500)
                .collectLatest {
                    Log.d("LoginPage", "instanceUrl: $it")
                    viewModel.fetchInstanceInfo(it)
                }
        }

        // TODO: Logo and welcome words here.

        var isInputting by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = instanceUrl,
            onValueChange = { instanceUrl = it },
            label = { Text(stringResource(R.string.textfield_instanceUrl)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.internet_outline),
                    contentDescription = "Input instance address",
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Uri),
            singleLine = true,
            modifier = Modifier.fillMaxWidth().onFocusChanged { isInputting = it.isFocused },
        )

        AnimatedVisibility(
            visible = isInputting,
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
                )
            }
        }
    }
}

@Composable
private fun InstanceCard(
    instanceUrl: String,
    name: String,
    version: String,
    peopleCount: Int,
    modifier: Modifier = Modifier,
) {
    Card(shape = MaterialTheme.shapes.small, modifier = modifier) {
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
private fun PreviewInstanceCard() {
    InstanceCard("neodb.social", "NeoDB", "neodb/0.11.7.3", 22135, Modifier.fillMaxWidth())
}

@Preview
@Composable
private fun PreviewShimmerInstanceCard() {
    ShimmerInstanceCard(Modifier.fillMaxWidth())
}
