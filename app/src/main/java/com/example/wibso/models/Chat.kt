package com.example.wibso.models

import kotlinx.serialization.Serializable

sealed class Chat {
    @Serializable
    data class PrivateChat(
        var receiver: User = User(),
        var sender: User = User()
    ) : Chat()

    @Serializable
    data class GroupChat(
        var users: List<User> = emptyList()
    ) : Chat()
}


@Serializable
data class AccountRoom(
    val chatWithUid: String? = null,
    val roomId: String,
)