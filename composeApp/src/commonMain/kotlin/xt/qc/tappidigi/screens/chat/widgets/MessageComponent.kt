package xt.qc.tappidigi.screens.chat.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.koin.compose.koinInject
import xt.qc.tappidigi.models.Chat
import xt.qc.tappidigi.models.Message
import xt.qc.tappidigi.models.User
import xt.qc.tappidigi.screens.profile.ProfileViewModel

@Composable
fun MessageComponent(
    message: Message,
    group: Chat.GroupChat? = null,
    private: Chat.PrivateChat? = null
) {
    val profile = koinInject<ProfileViewModel>()
    val msgOwer = msgOwner(message, group, private)
    val isMe = profile.userState.value?.uid == msgOwer?.uid
    println(isMe)
    Row(
        modifier = Modifier.padding(
            end = if (isMe) 20.dp else 80.dp,
            start = if (isMe) 80.dp else 20.dp,
            bottom = 16.dp
        ).background(Color.Yellow).fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        if (!isMe && msgOwer != null) {
            msgAvatar(msgOwer)
        }
        Text(
            message.content,
            modifier = Modifier.background(Color.Red).padding(20.dp).weight(1f),
        )
        if (isMe && msgOwer != null) {
            msgAvatar(msgOwer)
        }
    }
}

@Composable
fun msgAvatar(user: User) {
    AsyncImage(
        model = user.photoUrl,
        contentDescription = null,
        modifier = Modifier.size(40.dp).clip(CircleShape)
    )
}

fun msgOwner(
    message: Message,
    group: Chat.GroupChat? = null,
    private: Chat.PrivateChat? = null
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