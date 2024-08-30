package xt.qc.tappidigi.screens.authentication

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import xt.qc.tappidigi.AppViewModel
import xt.qc.tappidigi.screens.profile.ProfileViewModel

@Composable
fun LoginScreen() {
    val viewModel: LoginViewModel = viewModel { LoginViewModel() }
    val manager = koinInject<SignInWithGoogleManager>()
    val profile = koinInject<ProfileViewModel>()
    val appViewModel = koinInject<AppViewModel>()

    Text("Login")
    Button(onClick = {
        viewModel.loginWithGoogle(manager, onSuccess = {
            appViewModel.checkAuthentication(profile)
        })
    }) {
        Text("Login with gg")
    }
}