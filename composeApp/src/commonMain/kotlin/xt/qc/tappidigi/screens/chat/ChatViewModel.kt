package xt.qc.tappidigi.screens.chat

import androidx.lifecycle.ViewModel
import com.benasher44.uuid.uuid4
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.encodeToMap
import xt.qc.tappidigi.models.AccountRoom
import xt.qc.tappidigi.models.Chat
import xt.qc.tappidigi.models.User

class ChatViewModel(groupUsers: List<User>?, sender: User?, receiver: User?) : ViewModel() {
    private val firebase = Firebase.firestore
    private val _groupUsers = MutableStateFlow<List<User>>(listOf())
    private val groupUsers: StateFlow<List<User>> = _groupUsers.asStateFlow()

    init {
        _groupUsers.value = groupUsers ?: listOf()
    }

    fun sendMessage(content: String) {
        groupUsers.value.forEach {
//            firebase.collection("accounts").document(it.uid!!).collection("chats").document().set(mapOf("content" to content))")
        }
    }

    suspend fun checkChatRoomExists(chat: Chat.PrivateChat) {
        var isExists = false
        var roomId: String? = null
        val myRooms =
            firebase.collection("accounts").document(chat.sender.uid!!).collection("chats")
                .get().documents
        myRooms.forEach {
            val room = it.data(AccountRoom.serializer())
            if (room.chatWithUid == chat.receiver.uid) {
                isExists = true
                roomId = room.roomId
            }
        }

        if (isExists && roomId != null) {
            getMessages(roomId!!)
        } else {
            createPrivateChatRoom(chat)
        }
    }

    private suspend fun getMessages(chatRoomId: String) {
        println("get message")
    }

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun createPrivateChatRoom(private: Chat.PrivateChat) {
        val chatRoomId = uuid4().toString()
        CoroutineScope(Dispatchers.IO).launch {
            //create in chat collection for two people
            firebase.collection("chats").document(chatRoomId).collection("members")
                .document(private.sender.uid!!)
                .set(Properties.encodeToMap(private.sender), merge = true)
            firebase.collection("chats").document(chatRoomId).collection("members")
                .document(private.receiver.uid!!)
                .set(Properties.encodeToMap(private.receiver), merge = true)

            //create in account chat collection for each people
            firebase.collection("accounts").document(private.sender.uid!!).collection("chats")
                .document(chatRoomId).set(
                    Properties.encodeToMap(
                        AccountRoom(
                            roomId = chatRoomId,
                            chatWithUid = private.receiver.uid
                        )
                    ), merge = true
                )
            firebase.collection("accounts").document(private.receiver.uid!!).collection("chats")
                .document(chatRoomId).set(
                    Properties.encodeToMap(
                        AccountRoom(
                            roomId = chatRoomId,
                            chatWithUid = private.sender.uid
                        )
                    ), merge = true
                )
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun createGroupChatRoom() {
        val chatRoomId = uuid4().toString()

        groupUsers.value.forEach {
            if (it.uid == null) return
            firebase.collection("chats").document(chatRoomId).collection("members").document(it.uid)
                .set(Properties.encodeToMap(it), merge = true)
            firebase.collection("accounts").document(it.uid).collection("chats")
                .document(chatRoomId).set(Properties.encodeToMap(AccountRoom(roomId = chatRoomId)))
        }
    }
}