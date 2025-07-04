package day.vitayuzu.neodb

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import day.vitayuzu.neodb.ui.page.login.LoginViewModel
import day.vitayuzu.neodb.util.AUTH_CALLBACK
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OauthActivity : ComponentActivity() {
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = intent.data
        if (url != null && url.toString().startsWith(AUTH_CALLBACK)) {
            val authCode = url.getQueryParameter("code")
            if (!authCode.isNullOrBlank()) {
                lifecycleScope.launch {
                    viewModel.handleAuthCode(authCode).join()
                    finish()
                    // FIXME
                    this@OauthActivity.startActivity(
                        Intent(this@OauthActivity, MainActivity::class.java).addFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK,
                        ),
                    )
                }
            } else {
                Log.d("OauthActivity", "Can't get auth code")
                finish()
            }
        }
    }
}
