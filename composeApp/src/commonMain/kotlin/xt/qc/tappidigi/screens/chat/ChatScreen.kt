package xt.qc.tappidigi.screens.chat

import MessageTextField
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import tappidigi.composeapp.generated.resources.Res
import tappidigi.composeapp.generated.resources.arrow_right
import tappidigi.composeapp.generated.resources.send
import xt.qc.tappidigi.AppViewModel
import xt.qc.tappidigi.models.Chat
import xt.qc.tappidigi.models.MessagePosition
import xt.qc.tappidigi.models.MessageStatus
import xt.qc.tappidigi.screens.chat.widgets.MessageComponent
import xt.qc.tappidigi.screens.chat.widgets.MessageStatusComponent
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
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        if (private != null) {
            chatViewModel.checkChatRoomExists(private)
        }
    }
    Column(modifier = Modifier.imePadding().safeDrawingPadding().background(Color.LightGray)
        .pointerInput(Unit) {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
            })
        }) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().background(Color.White)
                .padding(horizontal = 4.dp, vertical = 8.dp)
        ) {
            Button(
                onClick = {
                    appViewModel.navHostController.popBackStack()
                },
                modifier = Modifier.size(40.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(painter = painterResource(Res.drawable.arrow_right), contentDescription = "")
            }
            Spacer(Modifier.width(4.dp))
            AsyncImage(
                model = private?.receiver?.photoUrl,
                contentDescription = null,
                modifier = Modifier.size(40.dp).clip(CircleShape)
            )
            Text(
                private?.receiver?.displayName ?: "",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom,
            reverseLayout = true
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
            private = private,
        )
    }
}
