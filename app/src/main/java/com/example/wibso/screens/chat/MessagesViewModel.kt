package com.example.wibso.screens.chat

import androidx.lifecycle.ViewModel
import com.example.wibso.models.AccountRoom
import com.example.wibso.models.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MessagesViewModel : ViewModel() {
    private val firebase = Firebase.firestore
    private val _groupChats = MutableStateFlow<List<List<User>>>(listOf())
    val groupChats: StateFlow<List<List<User>>> = _groupChats.asStateFlow()

    suspend fun getMyMessages(user: User) = coroutineScope {
        user.uid?.let { uid ->

            firebase.collection("accounts").document(uid).collection("chats").get()
                .addOnSuccessListener { docs ->
                    val deferredResults = docs.map {
                        CoroutineScope(Dispatchers.IO).async {
                            val room = it.toObject<AccountRoom>()
                            listOf(getRoomInformation(room, user))
                        }
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        val result = deferredResults.awaitAll()
                        _groupChats.value = result.flatten().filter { it.isNotEmpty() }
                    }
                }
        }
    }

    private suspend fun getRoomInformation(room: AccountRoom, user: User): List<User> =
        suspendCoroutine { continuation ->
            val users: ArrayList<User> = arrayListOf()

            firebase.collection("chats").document(room.roomId!!).collection("messages").get()
                .addOnSuccessListener { roomMessageDocs ->
                    if (roomMessageDocs.isEmpty) {
                        continuation.resume(emptyList())
                    } else {
                        firebase.collection("chats").document(room.roomId).collection("members")
                            .get().addOnSuccessListener { roomMembersDocs ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    roomMembersDocs.forEach {
                                        if (it.id != user.uid) {
                                            getRoomChatWithUser(it.id)?.let { u ->
                                                users.add(u)
                                            }
                                        }
                                    }
                                    continuation.resume(users.toList())
                                }
                            }
                    }
                }
        }

    private suspend fun getRoomChatWithUser(uid: String): User? = suspendCoroutine { continuation ->
        try {
            firebase.collection("accounts").document(uid).get().addOnSuccessListener {
                continuation.resume(it.toObject<User>())
            }
        } catch (e: Exception) {
            continuation.resume(null)
        }
    }
}