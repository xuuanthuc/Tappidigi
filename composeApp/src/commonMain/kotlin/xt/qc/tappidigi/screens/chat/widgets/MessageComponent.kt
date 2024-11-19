package xt.qc.tappidigi.screens.chat.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import tappidigi.composeapp.generated.resources.Res
import tappidigi.composeapp.generated.resources.warning
import xt.qc.tappidigi.models.Chat
import xt.qc.tappidigi.models.Message
import xt.qc.tappidigi.models.MessagePosition
import xt.qc.tappidigi.models.MessageStatus
import xt.qc.tappidigi.models.User
import xt.qc.tappidigi.screens.profile.ProfileViewModel
import xt.qc.tappidigi.utils.ChatThemes

@Composable
fun MessageComponent(
    message: Message,
    theme: ChatThemes,
    group: Chat.GroupChat? = null,
    private: Chat.PrivateChat? = null,
    position: MessagePosition,
    onResend: (Message) -> Unit,
) {
    val profile = koinInject<ProfileViewModel>()
    val owner = msgOwner(message, group, private)
    val isMe = profile.userState.value?.uid == owner?.uid

    fun <T> gap(first: T, second: T): T {
        return if (isMe) first else second
    }

    val messageShape = RoundedCornerShape(
        topStart = gap(
            16.dp, when (position) {
                MessagePosition.FIRST -> 16.dp
                MessagePosition.LAST -> 4.dp
                MessagePosition.MIDDLE -> 4.dp
                MessagePosition.SINGLE -> 16.dp
            }
        ), topEnd = when (position) {
            MessagePosition.FIRST -> 16.dp
            MessagePosition.LAST -> gap(4.dp, 16.dp)
            MessagePosition.MIDDLE -> gap(4.dp, 16.dp)
            MessagePosition.SINGLE -> 16.dp
        }, bottomStart = gap(
            16.dp, when (position) {
                MessagePosition.FIRST -> 4.dp
                MessagePosition.LAST -> 8.dp
                MessagePosition.MIDDLE -> 4.dp
                MessagePosition.SINGLE -> 8.dp
            }
        ), bottomEnd = gap(
            when (position) {
                MessagePosition.FIRST -> 4.dp
                MessagePosition.LAST -> 8.dp
                MessagePosition.MIDDLE -> 4.dp
                MessagePosition.SINGLE -> 8.dp
            }, 16.dp
        )
    )

    Column {
        Row(
            modifier = Modifier.padding(
                end = gap(0.dp, 80.dp), start = gap(80.dp, 0.dp)
            ).fillMaxWidth(),
            horizontalArrangement = gap(Arrangement.End, Arrangement.Start),
            verticalAlignment = Alignment.Bottom,
        ) {
            msgAvatar(
                owner,
                isShow = !isMe && owner != null && (position == MessagePosition.SINGLE || position == MessagePosition.LAST)
            )
            Box(
                Modifier.weight(1f).padding(vertical = 2.dp, horizontal = 8.dp),
                contentAlignment = gap(Alignment.CenterEnd, Alignment.CenterStart)
            ) {
                Text(
                    message.content,
                    style = TextStyle(
                        color = gap(Color.White, Color.Black)
                    ),
                    modifier = Modifier.pointerInput(message) {
                        detectTapGestures(onTap = {
                            if (message.status.value == MessageStatus.ERROR) {
                                onResend.invoke(message)
                            }
                        })
                    }.background(
                        gap(
                            theme.ownerColor, theme.otherColor,
                        ).copy(alpha = if (message.status.value == MessageStatus.ERROR) 0.5f else 1f),
                        shape = messageShape
                    ).padding(9.dp),
                )
            }
            AnimatedVisibility(
                visible = message.status.value == MessageStatus.ERROR,
                enter = fadeIn(tween(durationMillis = 300)),
                exit = fadeOut(tween(durationMillis = 300))
            ) {
                Row {
                    Icon(
                        painter = painterResource(Res.drawable.warning),
                        contentDescription = "",
                        modifier = Modifier.size(18.dp),
                        tint = Color.Red,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }
        AnimatedVisibility(
            visible = message.status.value == MessageStatus.ERROR,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Couldn't send!", style = TextStyle(
                        color = Color.Red, fontSize = 12.sp
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

@Composable
fun msgAvatar(user: User?, isShow: Boolean) {
    Box(
        Modifier.padding(bottom = 2.dp, start = 10.dp)
    ) {
        if (isShow) {
            AsyncImage(
                model = user?.photoUrl,
                contentDescription = null,
                modifier = Modifier.size(30.dp).clip(CircleShape)
            )
        } else {
            Spacer(modifier = Modifier.size(30.dp))
        }
    }
}

fun msgOwner(
    message: Message, group: Chat.GroupChat? = null, private: Chat.PrivateChat? = null
): User? {
    if (private != null) {
        return if (private.sender.uid == message.ownerId) {
            private.sender
        } else if (private.receiver.uid == message.ownerId) {
            private.receiver
        } else {
            null
        }
    } else if (group != null) {
        var user: User? = null
        group.users.forEach {
            if (it.uid == message.ownerId) {
                user = it
            }
        }
        return user
    } else {
        return null
    }
}

