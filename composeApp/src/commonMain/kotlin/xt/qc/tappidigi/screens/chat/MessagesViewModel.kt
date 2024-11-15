package xt.qc.tappidigi.screens.chat

import androidx.lifecycle.ViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import xt.qc.tappidigi.models.AccountRoom
import xt.qc.tappidigi.models.User

class MessagesViewModel : ViewModel() {
    private val firebase = Firebase.firestore
    private val _groupChats = MutableStateFlow<List<List<User>>>(listOf())
    val groupChats: StateFlow<List<List<User>>> = _groupChats.asStateFlow()

    suspend fun getMyMessages(user: User) {
        user.uid?.let { uid ->
            val myRooms =
                firebase.collection("accounts").document(uid).collection("chats").get().documents
            val deferredResults = myRooms.map {
                CoroutineScope(Dispatchers.IO).async {
                    val room = it.data(AccountRoom.serializer())
                    listOf(getRoomInformation(room, user))
                }
            }
            val result = deferredResults.awaitAll()
            _groupChats.value = result.flatten().filter { it.isNotEmpty() }
        }
    }

    private suspend fun getRoomInformation(room: AccountRoom, user: User): List<User> {
        val users: ArrayList<User> = arrayListOf()

        val roomMessage = firebase.collection("chats").document(room.roomId).collection("messages")
            .get().documents

        if (roomMessage.isEmpty()) return emptyList()

        val roomMembers =
            firebase.collection("chats").document(room.roomId).collection("members").get().documents
        roomMembers.forEach {
            if (it.id != user.uid) {
                getRoomChatWithUser(it.id)?.let { u ->
                    users.add(u)
                }
            }
        }
        return users.toList()
    }

    private suspend fun getRoomChatWithUser(uid: String): User? {
        try {
            val user = firebase.collection("accounts").document(uid).get()
            return user.data(User.serializer())
        } catch (e: Exception) {
            return null
        }
    }
}