package xt.qc.tappidigi.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import tappidigi.composeapp.generated.resources.Res
import tappidigi.composeapp.generated.resources.search
import xt.qc.tappidigi.AppViewModel
import xt.qc.tappidigi.models.Chat
import xt.qc.tappidigi.screens.chat.widgets.MessageComponent
import xt.qc.tappidigi.screens.chat.widgets.MessagePosition
import xt.qc.tappidigi.screens.profile.ProfileViewModel

@Composable
fun ChatScreen(group: Chat.GroupChat? = null, private: Chat.PrivateChat? = null) {
    val profile = koinInject<ProfileViewModel>()
    val chatViewModel: ChatViewModel = viewModel {
        ChatViewModel(
            groupUsers = group?.users, sender = private?.sender, receiver = private?.receiver
        )
    }
    val appViewModel: AppViewModel = koinInject<AppViewModel>()
    val contentController: MutableState<TextFieldValue> = remember {
        mutableStateOf(
            TextFieldValue()
        )
    }
    val messages = chatViewModel.message.collectAsState().value
    LaunchedEffect(Unit) {
        if (private != null) {
            chatViewModel.checkChatRoomExists(private)
        }
    }
    Column(modifier = Modifier.imePadding().safeDrawingPadding()) {
        Text(private?.sender?.displayName ?: "")
        Text(private?.receiver?.displayName ?: "")
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom,
            reverseLayout = true
        ) {
            items(messages.size) {
                val msg = messages[it]
                var position = MessagePosition.MIDDLE
                val prevMsg = messages.getOrNull(it - 1)
                val nextMsg = messages.getOrNull(it + 1)

                if (prevMsg == null) {
                    position = MessagePosition.FIRST
                } else if (nextMsg == null) {
                    position = MessagePosition.LAST
                } else if (msg.ownerId != prevMsg.ownerId) {
                    position = MessagePosition.FIRST
                } else if (nextMsg.ownerId != msg.ownerId) {
                    position = MessagePosition.LAST
                }

                MessageComponent(msg, group, private, position)
            }
        }
        Row {
            TextField(
                modifier = Modifier.weight(1f),
                value = contentController.value,
                onValueChange = { contentController.value = it },
                label = { Text(stringResource(Res.string.search)) },
            )
            Button(onClick = {
                CoroutineScope(Dispatchers.Main).launch {
                    chatViewModel.sendMessage(
                        contentController.value.text, private?.sender?.uid ?: ""
                    )
                }
            }) {
                Text("Send")
            }
        }
    }
}