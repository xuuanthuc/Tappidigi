package xt.qc.tappidigi

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import tappidigi.composeapp.generated.resources.Res
import tappidigi.composeapp.generated.resources.compose_multiplatform

@Composable
fun Detail() {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        val platform = koinInject<Platform>()
        val viewModel = koinInject<GreetingViewModel>()
        val greeting = remember { Greeting().greet(platform.name) }
        Button(onClick = { viewModel.onShowContentClicked() }) {
            Text("Click me!")
        }
        Button(onClick = {
            viewModel.viewModelScope.launch(Dispatchers.Main) {
                viewModel.addData()
            }
        }) {
            Text("Add data")
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