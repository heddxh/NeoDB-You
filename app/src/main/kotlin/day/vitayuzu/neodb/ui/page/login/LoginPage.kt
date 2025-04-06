package day.vitayuzu.neodb.ui.page.login

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import org.publicvalue.multiplatform.oidc.appsupport.AndroidCodeAuthFlowFactory

@Composable
fun LoginPage(
    authFlowFactory: AndroidCodeAuthFlowFactory,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(),
) {
    Scaffold {
        Button(
            modifier =
                Modifier
                    .padding(it)
                    .fillMaxWidth(),
            onClick = {
                viewModel.login(authFlowFactory)
            },
        ) {
            Text("Login")
        }
    }
}
