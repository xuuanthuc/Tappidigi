package xt.qc.tappidigi.screens.chat.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.koin.compose.koinInject
import xt.qc.tappidigi.models.Chat
import xt.qc.tappidigi.models.Message
import xt.qc.tappidigi.models.User
import xt.qc.tappidigi.screens.profile.ProfileViewModel

enum class MessagePosition {
    FIRST, LAST, MIDDLE
}

@Composable
fun MessageComponent(
    message: Message,
    group: Chat.GroupChat? = null,
    private: Chat.PrivateChat? = null,
    position: MessagePosition,
) {
    val profile = koinInject<ProfileViewModel>()
    val msgOwer = msgOwner(message, group, private)
    val isMe = profile.userState.value?.uid == msgOwer?.uid

    fun <T> gap(first: T, second: T): T {
        return if (isMe) first else second
    }

    println(position)

    Row(
        modifier = Modifier.padding(
            end = gap(0.dp, 80.dp), start = gap(80.dp, 0.dp)
        ).fillMaxWidth(), horizontalArrangement = gap(Arrangement.End, Arrangement.Start)
    ) {
        if (!isMe && msgOwer != null) {
            msgAvatar(msgOwer)
        }
        Box(
            Modifier.weight(1f).padding(vertical = 2.dp, horizontal = 6.dp),
            contentAlignment = gap(Alignment.CenterEnd, Alignment.CenterStart)
        ) {
            Text(
                message.content,
                style = TextStyle(
                    color = gap(Color.White, Color.Black)
                ),
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        println("tapped")
                    })
                }.background(
                    gap(Color.Blue, Color.LightGray), shape = RoundedCornerShape(
                        topStart = 15.dp,
                        topEnd = when (position) {
                            MessagePosition.FIRST -> 15.dp
                            MessagePosition.LAST -> 0.dp
                            MessagePosition.MIDDLE -> 0.dp
                        },
                        bottomStart = gap(15.dp, 8.dp),
                        bottomEnd = gap(
                            when (position) {
                                MessagePosition.FIRST -> 0.dp
                                MessagePosition.LAST -> 8.dp
                                MessagePosition.MIDDLE -> 0.dp
                            }, 15.dp
                        )
                    )
                ).padding(10.dp),
            )
        }
    }
}

@Composable
fun msgAvatar(user: User) {
    Box(
        Modifier.padding(top = 2.dp, start = 10.dp)
    ) {
        AsyncImage(
            model = user.photoUrl,
            contentDescription = null,
            modifier = Modifier.size(40.dp).clip(CircleShape)
        )
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

