package com.example.wibso.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.Timestamp
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class Message @OptIn(ExperimentalUuidApi::class) constructor(
    val content: String = "",
    val attachment: String? = null,
    val ownerId: String = "",
    val messageType: Int = 0,
    val createdAt: Long = Timestamp.now().seconds,
    val updatedAt: Long = Timestamp.now().seconds,
    val id: String = Uuid.random().toString(),
    @Transient
    var status: MutableState<MessageStatus> = mutableStateOf(MessageStatus.SENDING),
)

@Serializable
@OptIn(ExperimentalUuidApi::class)
data class MessageDocumentSnapshot (
    val content: String = "",
    val ownerId: String = "",
    val attachment: String? = null,
    val messageType: Int = 0,
    val createdAt: Long = Timestamp.now().seconds,
    val updatedAt: Long = Timestamp.now().seconds,
    val id: String = Uuid.random().toString(),
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

    fun toContent(): String = when (this) {
        SENDING -> "Sending"
        ERROR -> "Couldn't send!"
        SENT -> "Sent"
    }
}

enum class MessagePosition {
    FIRST, LAST, MIDDLE, SINGLE
}