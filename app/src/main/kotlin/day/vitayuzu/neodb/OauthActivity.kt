package day.vitayuzu.neodb

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import day.vitayuzu.neodb.ui.page.login.LoginPage
import day.vitayuzu.neodb.ui.page.login.LoginViewModel
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import day.vitayuzu.neodb.util.AUTH_CALLBACK
import kotlinx.coroutines.launch

/**
 * Activity that starts the auth flow and receive oauth callback.
 * Since Firefox on Android implement app links incorrectly, we need to use this activity to
 * start custom tab instead of the main activity.
 * In detail, Firefox on Android won't finish it self when navigating to app links from custom tabs,
 * because of a missing intent flag.
 * As a workaround, we use `singleTask` for this activity.
 * When open a custom tab:
 * Task 1 -> MainActivity
 * Task 2 -> OauthActivity -> CustomTab
 * When user click the app link(a callback to our app):
 * Task 1 -> MainActivity
 * Task 2 -> OauthActivity(onNewIntent), CustomTab is destroyed.
 * Task 3 -> MainActivity <- we are here.
 * In the end we back to MainActivity.
 */
@AndroidEntryPoint
class OauthActivity : ComponentActivity() {
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NeoDBYouTheme {
                Scaffold {
                    LoginPage(modifier = Modifier.padding(it), viewModel = viewModel) {}
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val url = intent.data
        if (url != null && url.toString().startsWith(AUTH_CALLBACK)) {
            val authCode = url.getQueryParameter("code")
            if (!authCode.isNullOrBlank()) {
                lifecycleScope.launch {
                    viewModel.handleAuthCode(authCode).join() // wait until finish
                    finish()
                }
            } else {
                Log.d("OauthActivity", "Can't get auth code")
                finish()
            }
        }
    }

    // Handle oauth call back.
    override fun onNewIntent(intent: Intent) {
        println("On New Intent")
        super.onNewIntent(intent)
        setIntent(intent)
    }
}
