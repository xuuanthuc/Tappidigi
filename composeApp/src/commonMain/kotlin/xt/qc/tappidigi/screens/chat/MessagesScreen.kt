package xt.qc.tappidigi.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import tappidigi.composeapp.generated.resources.Res
import tappidigi.composeapp.generated.resources.search

@Composable
fun MessagesScreen() {
    val contentController: MutableState<TextFieldValue> = remember {
        mutableStateOf(
            TextFieldValue()
        )
    }
    Column {
        TextField(
            value = contentController.value,
            onValueChange = { contentController.value = it },
            label = { Text(stringResource(Res.string.search)) },
        )
        Box(modifier = Modifier.height(200.dp).fillMaxWidth().background(Color.Red))
        Box(modifier = Modifier.height(200.dp).fillMaxWidth().background(Color.Blue))
        Box(modifier = Modifier.height(200.dp).fillMaxWidth().background(Color.Yellow))
        Box(modifier = Modifier.height(200.dp).fillMaxWidth().background(Color.Blue))
    }
}