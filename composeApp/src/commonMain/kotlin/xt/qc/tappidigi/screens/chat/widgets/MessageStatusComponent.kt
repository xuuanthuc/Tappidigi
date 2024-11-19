package xt.qc.tappidigi.screens.chat.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import xt.qc.tappidigi.models.MessageStatus
import xt.qc.tappidigi.screens.chat.ChatViewModel

@Composable
fun MessageStatusComponent(
    chatViewModel: ChatViewModel,
) {
    val latestMessage = chatViewModel.message.collectAsState().value.firstOrNull()
    val showSpinner = remember { mutableStateOf(false) }

    latestMessage?.let {
        LaunchedEffect(it) {
            snapshotFlow { it.status.value }.collect { status ->
                if (status == MessageStatus.SENDING) {
                    delay(300)
                    showSpinner.value = true
                } else if (status == MessageStatus.SENT) {
                    delay(1000)
                    showSpinner.value = false
                } else {
                    delay(300)
                    showSpinner.value = false
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
        if (latestMessage != null) {
            AnimatedVisibility(
                visible = latestMessage.status.value == MessageStatus.SENDING && showSpinner.value,
                enter = fadeIn(),
                exit = fadeOut(tween(durationMillis = 300))
            ) {
                Text(
                    "Sending!", style = TextStyle(
                        color = Color.Red, fontSize = 12.sp
                    ), modifier = Modifier.padding(end = 8.dp)
                )
            }
        }

        if (latestMessage != null) {
            AnimatedVisibility(
                visible = latestMessage.status.value == MessageStatus.SENT && showSpinner.value,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    "Sent!", style = TextStyle(
                        color = Color.Red, fontSize = 12.sp
                    ), modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
    }
}