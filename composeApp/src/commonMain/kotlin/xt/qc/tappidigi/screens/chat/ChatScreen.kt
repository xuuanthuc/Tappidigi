package xt.qc.tappidigi.screens.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun ChatScreen() {
    // Use rememberSaveable to preserve the state
    val counter = rememberSaveable { mutableStateOf(0) }

    // UI for HomeScreen
    Column {
        Text("Chat Screen")
        Button(onClick = { counter.value++ }) {
            Text("Counter: ${counter.value}")
        }
    }
}