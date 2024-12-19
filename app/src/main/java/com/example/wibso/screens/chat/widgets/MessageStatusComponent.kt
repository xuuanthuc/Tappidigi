package com.example.wibso.screens.chat.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.wibso.models.Message
import com.example.wibso.models.MessageStatus

@Composable
fun MessageStatusComponent(
    message: Message,
) {
    val showSpinner = remember { mutableStateOf(false) }

    LaunchedEffect(message) {
        snapshotFlow { message.status.value }.collect { status ->

            when (status) {
                MessageStatus.SENDING -> {
                    delay(300)
                    showSpinner.value = true
                }

                MessageStatus.ERROR -> {
                    delay(300)
                    showSpinner.value = false
                }

                MessageStatus.SENT -> {
                    delay(1000)
                    showSpinner.value = false
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
        AnimatedVisibility(
            visible = showSpinner.value,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                message.status.value.toContent(), style = TextStyle(
                    color = Color.Gray, fontSize = 10.sp
                ), modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}