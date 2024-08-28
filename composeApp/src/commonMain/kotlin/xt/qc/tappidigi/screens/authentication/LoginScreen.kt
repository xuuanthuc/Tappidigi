package xt.qc.tappidigi.screens.authentication

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import xt.qc.tappidigi.AppViewModel

@Composable
fun LoginScreen(navController: NavHostController) {
    val viewModel: LoginViewModel = viewModel { LoginViewModel() }
    val manager = koinInject<SignInWithGoogleManager>()
    val appViewModel = koinInject<AppViewModel>()

    Text("Login")
    Button(onClick = {
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.loginWithGoogle(manager, onSuccess = {
                appViewModel.checkAuthentication(navController, manager)
            })
        }
    }) {
        Text("Login with gg")
    }
}