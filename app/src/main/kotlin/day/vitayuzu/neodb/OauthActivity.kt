package day.vitayuzu.neodb

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Scaffold
import dagger.hilt.android.AndroidEntryPoint
import day.vitayuzu.neodb.ui.page.onboarding.OnboardingScreen
import day.vitayuzu.neodb.ui.page.onboarding.OnboardingViewModel
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import day.vitayuzu.neodb.util.AUTH_CALLBACK
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This Activity initiates the authentication flow and handles the OAuth callback.
 *
 * Due to an issue with how Firefox on Android handles app links (it doesn't correctly finish itself
 * when navigating to app links from custom tabs due to a missing intent flag), this Activity is
 * used to launch the custom tab instead of the [MainActivity].
 *
 * To address this, `singleTask` launch mode is used for this Activity.
 * The task flow is as follows:
 *
 * When opening a custom tab for authentication:
 * `MainActivity -> OauthActivity -> CustomTab`
 *
 * When the user clicks the app link (the OAuth callback to our app):
 * `MainActivity -> OauthActivity` (call [onNewIntent]), and the CustomTab is destroyed.
 * In the end we call [finish] to back to MainActivity.
 */
@AndroidEntryPoint
class OauthActivity : ComponentActivity() {
    @Inject @AppScope lateinit var appScope: CoroutineScope

    private val viewModel: OnboardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NeoDBYouTheme {
                Scaffold {
                    OnboardingScreen(
                        onSkip = { finish() },
                        paddingValues = it,
                        viewModel = viewModel,
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // When user cancel the login process by clicking back button or closing custom tab,
        // that is, before oauth callback with new intent, re-show the input field.
        if (intent.data?.toString()?.startsWith(AUTH_CALLBACK) == true ||
            viewModel.uiState.value.isPreparingOauth
        ) {
            return
        }
        Log.d("OauthActivity", "Resume without oauth callback, reset oauth state")
        viewModel.resetOauthState()
    }

    /**
     * Handles the OAuth callback.
     *
     * Note: This callback is only invoked if an [OauthActivity] instance is already running.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        Log.d("OauthActivity", "On New Intent")
        val url = intent.data
        if (url != null && url.toString().startsWith(AUTH_CALLBACK)) {
            val authCode = url.getQueryParameter("code")
            if (!authCode.isNullOrBlank()) {
                appScope.launch {
                    Log.d("OauthActivity", "Auth code received")
                    viewModel
                        .handleAuthCode(authCode)
                        .onCompletion {
                            // Finish when successfully exchanged access token.
                            // If failed(it != null), remain at this activity.
                            if (it == null) {
                                viewModel.completeOnboarding()
                                finish()
                            }
                        }.collect()
                }
            } else {
                Log.d("OauthActivity", "Can't get auth code")
                finish()
            }
        }
    }
}
