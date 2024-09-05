package xt.qc.tappidigi.models

import com.benasher44.uuid.uuid4
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val content: String,
    val ownerId: String,
    val messageType: Int,
    val createdAt: Long = Timestamp.now().seconds,
    val updatedAt: Long = Timestamp.now().seconds,
    val id: String = uuid4().toString()
)

enum class MessageType {
    TEXT,
    IMAGE,
    VIDEO,
    AUDIO
}