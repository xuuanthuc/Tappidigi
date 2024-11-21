package xt.qc.tappidigi.screens.chat

import MessageTextField
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject
import xt.qc.tappidigi.models.Chat
import xt.qc.tappidigi.models.MessagePosition
import xt.qc.tappidigi.screens.chat.widgets.ChatEmojisComponent
import xt.qc.tappidigi.screens.chat.widgets.ChatHeadingComponent
import xt.qc.tappidigi.screens.chat.widgets.MessageComponent
import xt.qc.tappidigi.screens.chat.widgets.MessageStatusComponent
import xt.qc.tappidigi.screens.profile.ProfileViewModel

enum class ActionChat {
    WAIT, SEND, RESEND
}

@Composable
fun ChatScreen(group: Chat.GroupChat? = null, private: Chat.PrivateChat? = null) {
    val profile = koinInject<ProfileViewModel>()
    val action: MutableState<ActionChat?> = remember { mutableStateOf(ActionChat.WAIT) }
    val chatViewModel: ChatViewModel = viewModel {
        ChatViewModel(
            groupUsers = group?.users, sender = private?.sender, receiver = private?.receiver
        )
    }
    val contentController: MutableState<TextFieldValue> = remember {
        mutableStateOf(
            TextFieldValue()
        )
    }
    val messages = chatViewModel.message.collectAsState().value
    val focusManager = LocalFocusManager.current
    val lazyColumnListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (private != null) {
            launch(Dispatchers.IO) {
                chatViewModel.getEmojiProvider()
            }
            chatViewModel.checkChatRoomExists(private)
        }
    }

    LaunchedEffect(action.value) {
        if (action.value == ActionChat.SEND) {
            lazyColumnListState.animateScrollToItem(0)
            action.value = ActionChat.WAIT
        }
    }


    Column(modifier = Modifier.imePadding().safeDrawingPadding()
        .background(chatViewModel.theme.value.backgroundColor).pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
            })
        }) {
        ChatHeadingComponent(chatViewModel = chatViewModel, private = private)
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom,
            reverseLayout = true,
            state = lazyColumnListState
        ) {
            items(messages.size) {
                val msg = messages[it]
                val prevMsg = messages.getOrNull(it + 1)
                val nextMsg = messages.getOrNull(it - 1)
                val position: MessagePosition = when {
                    prevMsg == null -> MessagePosition.FIRST

                    nextMsg == null -> if (msg.ownerId != prevMsg.ownerId) MessagePosition.SINGLE else MessagePosition.LAST

                    msg.ownerId != prevMsg.ownerId && msg.ownerId != nextMsg.ownerId -> MessagePosition.SINGLE

                    msg.ownerId != prevMsg.ownerId -> MessagePosition.FIRST

                    msg.ownerId != nextMsg.ownerId -> MessagePosition.LAST

                    else -> MessagePosition.MIDDLE
                }

                MessageComponent(
                    message = msg,
                    group = group,
                    private = private,
                    position = position,
                    theme = chatViewModel.theme.value,
                    onResend = {
                        CoroutineScope(Dispatchers.Main).launch {
                            chatViewModel.reSendMessage(it)
                        }
                    },
                )
            }
        }
        MessageStatusComponent(chatViewModel = chatViewModel)
        MessageTextField(
            chatViewModel = chatViewModel,
            contentController = contentController,
            onSend = {
                scope.launch {
                    lazyColumnListState.animateScrollToItem(0)
                    chatViewModel.sendMessage(
                        it, private?.sender?.uid ?: ""
                    )
                }
            })
        ChatEmojisComponent(chatViewModel = chatViewModel, contentController = contentController)
    }
}
