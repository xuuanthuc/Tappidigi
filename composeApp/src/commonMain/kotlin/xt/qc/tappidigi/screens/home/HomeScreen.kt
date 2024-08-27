package xt.qc.tappidigi.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import tappidigi.composeapp.generated.resources.Res
import tappidigi.composeapp.generated.resources.compose_multiplatform
import xt.qc.tappidigi.Greeting
import xt.qc.tappidigi.GreetingViewModel
import xt.qc.tappidigi.utils.Platform

@Composable
fun HomeScreen() {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        val platform = koinInject<Platform>()
        val viewModel = koinInject<GreetingViewModel>()
        val greeting = rememberSaveable { Greeting().greet(platform.name) }
        Button(onClick = { viewModel.onShowContentClicked() }) {
            Text("Click me!")
        }
        AnimatedVisibility(viewModel.showContent.value) {
            Column(
                Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(painterResource(Res.drawable.compose_multiplatform), null)
                Text("Compose: $greeting")
            }
        }
    }
}