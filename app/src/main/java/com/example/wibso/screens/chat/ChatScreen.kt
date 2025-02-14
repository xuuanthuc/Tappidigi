package com.example.wibso.screens.chat

import MessageTextField
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wibso.AppViewModel
import com.example.wibso.models.Chat
import com.example.wibso.models.MessageType
import com.example.wibso.screens.chat.widgets.AlbumComponent
import com.example.wibso.screens.chat.widgets.CameraComponent
import com.example.wibso.screens.chat.widgets.ChatEmojisComponent
import com.example.wibso.screens.chat.widgets.ChatHeadingComponent
import com.example.wibso.screens.chat.widgets.MessageComponent
import com.example.wibso.utils.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

enum class ActionChat {
    WAIT, SEND, RESEND
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun ChatScreen(group: Chat.GroupChat? = null, private: Chat.PrivateChat? = null) {
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
    val appViewModel: AppViewModel = koinInject<AppViewModel>()

    val messages = chatViewModel.message.collectAsState().value
    val showingDateId = chatViewModel.showingDateId.collectAsState().value
    val focusManager = LocalFocusManager.current
    val lazyColumnListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val toolsViewModel = viewModel { ActionToolsViewModel() }

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

    BackHandler {
        if (chatViewModel.actionState.value != ActionToolState.NONE) {
            chatViewModel.actionState.value = ActionToolState.NONE
        } else {
            appViewModel.navHostController.popBackStack()
        }
    }

    Box {
        Column(modifier = Modifier
            .imePadding()
            .safeDrawingPadding()
            .background(chatViewModel.theme.value.backgroundColor)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    if (chatViewModel.actionState.value == ActionToolState.AUDIO) {
                        return@detectTapGestures
                    }
                    focusManager.clearFocus()
                    chatViewModel.actionState.value = ActionToolState.NONE
                })
            }) {
            ChatHeadingComponent(chatViewModel = chatViewModel, private = private)
            when (chatViewModel.status.value) {
                Status.LOADING -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = chatViewModel.theme.value.sendButtonColor)
                    }
                }

                Status.LOADED -> {
                    if (messages.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                "No messages here yet...", style = TextStyle(
                                    color = Color.White
                                ), modifier = Modifier
                                    .background(
                                        chatViewModel.theme.value.sendButtonColor.copy(
                                            alpha = 0.5f
                                        ), shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(30.dp)
                            )

                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.Bottom,
                            reverseLayout = true,
                            state = lazyColumnListState
                        ) {
                            items(messages.size) {
                                val msg = messages[it]
                                val prevMsg = messages.getOrNull(it + 1)
                                val nextMsg = messages.getOrNull(it - 1)

                                Column {
                                    MessageComponent(
                                        message = msg,
                                        group = group,
                                        private = private,
                                        theme = chatViewModel.theme.value,
                                        nextMsg = nextMsg,
                                        prevMsg = prevMsg,
                                        onResend = {
                                            CoroutineScope(Dispatchers.Main).launch {
                                                chatViewModel.reSendMessage(it)
                                            }
                                        },
                                        onShowingDate = { m ->
                                            chatViewModel.onShowingDate(m)
                                        },
                                        showingDateId = showingDateId ?: ""
                                    )
                                }
                            }
                        }
                    }
                }

                Status.ERROR -> {

                }
            }

            MessageTextField(
                chatViewModel = chatViewModel,
                private = private,
                contentController = contentController,
                onSend = {
                    scope.launch {
                        lazyColumnListState.animateScrollToItem(0)
                        when (it.messageType) {
                            MessageType.TEXT.ordinal -> {
                                chatViewModel.sendMessage(it)
                            }

                            MessageType.AUDIO.ordinal -> {
                                chatViewModel.uploadAudioToFirebaseStorage(it)
                            }

                        }
                    }
                },
            )
            ChatEmojisComponent(
                chatViewModel = chatViewModel, contentController = contentController
            )
            if (chatViewModel.actionState.value == ActionToolState.GALLERY) {
                AlbumComponent(
                    chatViewModel = chatViewModel, toolsViewModel = toolsViewModel
                )
            }
        }
        if (chatViewModel.actionState.value == ActionToolState.CAMERA) {
            CameraComponent(chatViewModel = chatViewModel)
        }
    }
}
