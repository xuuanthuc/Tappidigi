package xt.qc.tappidigi.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.benasher44.uuid.uuid4
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Message(
    val content: String,
    val ownerId: String,
    val messageType: Int,
    val createdAt: Long = Timestamp.now().seconds,
    val updatedAt: Long = Timestamp.now().seconds,
    val id: String = uuid4().toString(),
    @Transient
    var status: MutableState<MessageStatus> = mutableStateOf(MessageStatus.SENDING),
)

enum class MessageType {
    TEXT,
    IMAGE,
    VIDEO,
    AUDIO
}

enum class MessageStatus {
    SENDING,
    ERROR,
    SENT;

    fun toContent(): String =  when (this) {
        SENDING -> "Sending"
        ERROR -> "Couldn't send!"
        SENT -> "Sent!"
    }
}

enum class MessagePosition {
    FIRST, LAST, MIDDLE, SINGLE
}