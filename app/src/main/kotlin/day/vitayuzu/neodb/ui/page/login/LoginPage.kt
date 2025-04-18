package day.vitayuzu.neodb.ui.page.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LoginPage(
    onLoggedIn: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.value.isLoggedIn) {
        onLoggedIn()
    }
    Scaffold(modifier = modifier) {
        Box(modifier = Modifier.padding(it).fillMaxSize()) {
            Button(
                modifier = Modifier.align(Alignment.Center),
                onClick = { viewModel.login() },
            ) { Text("Login") }
        }
    }
}
