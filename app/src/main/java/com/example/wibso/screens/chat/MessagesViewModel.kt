package com.example.wibso.screens.chat

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.wibso.models.AccountRoom
import com.example.wibso.models.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.dataObjects
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.decodeFromMap

class MessagesViewModel : ViewModel() {
    private val firebase = Firebase.firestore
    private val _groupChats = MutableStateFlow<List<List<User>>>(listOf())
    val groupChats: StateFlow<List<List<User>>> = _groupChats.asStateFlow()

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun getMyMessages(user: User) {
        user.uid?.let { uid ->
            val myRooms =
                firebase.collection("accounts").document(uid).collection("chats").get().addOnSuccessListener { documents ->
                    val deferredResults = documents.map {
                        CoroutineScope(Dispatchers.IO).async {
                            val room =
                                Properties.decodeFromMap<AccountRoom>(map = it?.data ?: mapOf())
                            listOf(getRoomInformation(room, user))
                        }
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        val result = deferredResults.awaitAll()
                        _groupChats.value = result.flatten().filter { it.isNotEmpty() }
                    }
                }
        }
    }

    private suspend fun getRoomInformation(room: AccountRoom, user: User): List<User> {
        val users: ArrayList<User> = arrayListOf()

        val roomMessage = firebase.collection("chats").document(room.roomId).collection("messages")
            .get().result.documents

        if (roomMessage.isEmpty()) return emptyList()

        val roomMembers =
            firebase.collection("chats").document(room.roomId).collection("members").get().result.documents
        roomMembers.forEach {
            if (it.id != user.uid) {
                getRoomChatWithUser(it.id)?.let { u ->
                    users.add(u)
                }
            }
        }
        return users.toList()
    }

    @OptIn(ExperimentalSerializationApi::class)
    private suspend fun getRoomChatWithUser(uid: String): User? {
        try {
            val user = firebase.collection("accounts").document(uid).get().result
            return Properties.decodeFromMap<User>(map = user?.data ?: mapOf())
        } catch (e: Exception) {
            return null
        }
    }
}