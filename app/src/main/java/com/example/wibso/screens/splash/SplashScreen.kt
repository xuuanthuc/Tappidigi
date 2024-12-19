package com.example.wibso.screens.splash

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import com.example.wibso.AppViewModel
import com.example.wibso.screens.profile.ProfileViewModel

@Composable
fun SplashScreen() {
    val viewModel: AppViewModel = koinInject<AppViewModel>()
    val profileViewModel: ProfileViewModel = koinInject<ProfileViewModel>()

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            delay(500)
            viewModel.checkAuthentication(profileViewModel)
        }
    }
    Text("Splash Screen")
}