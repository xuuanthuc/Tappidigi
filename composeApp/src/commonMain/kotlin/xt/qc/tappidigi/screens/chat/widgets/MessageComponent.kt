package xt.qc.tappidigi.screens.chat.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import xt.qc.tappidigi.models.Message

@Composable
fun MessageComponent(message: Message) {
    Box(modifier = Modifier.padding(20.dp).background(Color.Red)){
        Text(message.content)
    }
}