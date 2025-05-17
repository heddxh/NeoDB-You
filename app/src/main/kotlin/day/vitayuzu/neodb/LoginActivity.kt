package day.vitayuzu.neodb

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import dagger.hilt.android.AndroidEntryPoint
import day.vitayuzu.neodb.data.AuthRepository
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    @Inject lateinit var repository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NeoDBYouTheme {
                LoginPage {
                    Log.d("LoginActivity", "Login Instance: $it")
                    val intent = CustomTabsIntent.Builder().build()
                    intent.launchUrl(this, it.toUri())
                }
            }
        }
    }
}

@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit = {},
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        ) {
            var inputInstance by remember { mutableStateOf("") }
            Text(
                text = "Login Instance",
                style = MaterialTheme.typography.titleLarge,
            )
            TextField(
                value = inputInstance,
                onValueChange = { inputInstance = it },
            )
            Button(onClick = { onClick(inputInstance) }) {
                Text("Log in")
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun LoginPagePreview() {
    NeoDBYouTheme {
        LoginPage()
    }
}
