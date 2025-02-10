package com.example.wibso.screens.authentication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wibso.AppViewModel
import com.example.wibso.screens.profile.ProfileViewModel
import org.koin.compose.koinInject

@Composable
fun LoginScreen() {
    val viewModel: LoginViewModel = viewModel { LoginViewModel() }
    val profile = koinInject<ProfileViewModel>()
    val appViewModel = koinInject<AppViewModel>()
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Login")
        Button(
            onClick = {
                viewModel.loginWithGoogle(context, onSuccess = {
                    appViewModel.checkAuthentication(profile)
                })
            },
        ) {
            Text("Login with gg")
        }
    }
}