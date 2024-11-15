package xt.qc.tappidigi.screens.chat

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.benasher44.uuid.uuid4
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.ChangeType
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.encodeToMap
import xt.qc.tappidigi.models.AccountRoom
import xt.qc.tappidigi.models.Chat
import xt.qc.tappidigi.models.Message
import xt.qc.tappidigi.models.MessageType
import xt.qc.tappidigi.models.User

class ChatViewModel(groupUsers: List<User>?, sender: User?, receiver: User?) : ViewModel() {
    private val firebase = Firebase.firestore
    private val _groupUsers = MutableStateFlow<List<User>>(listOf())
    private val groupUsers: StateFlow<List<User>> = _groupUsers.asStateFlow()

    private val _messages = MutableStateFlow<SnapshotStateList<Message>>(mutableStateListOf())
    val message: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _roomId = MutableStateFlow<String?>(null)
    private val roomId: StateFlow<String?> = _roomId.asStateFlow()

    init {
        _groupUsers.value = groupUsers ?: listOf()
    }

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun sendMessage(content: String, uid: String) {
        if (roomId.value == null || uid.isEmpty()) return
        val message =
            Message(content = content, ownerId = uid, messageType = MessageType.TEXT.ordinal)
        firebase.collection("chats").document(roomId.value!!).collection("messages")
            .document(message.id).set(Properties.encodeToMap(message))
    }

    suspend fun checkChatRoomExists(chat: Chat.PrivateChat) {
        var isExists = false
        val myRooms =
            firebase.collection("accounts").document(chat.sender.uid!!).collection("chats")
                .get().documents
        myRooms.forEach {
            val room = it.data(AccountRoom.serializer())
            if (room.chatWithUid == chat.receiver.uid) {
                isExists = true
                _roomId.value = room.roomId
            }
        }

        if (isExists && roomId.value != null) {
            getMessages(roomId.value!!)
        } else {
            createPrivateChatRoom(chat)
        }
    }

    private suspend fun getMessages(roomId: String) {
        val snapshot =
            firebase.collection("chats").document(roomId).collection("messages").orderBy("createdAt").snapshots
        println("Collected")
        snapshot.collect {
            for (dc in it.documentChanges) {
                when (dc.type) {
                    ChangeType.ADDED -> {
                        println("ADDED")
                        _messages.value.add(0, dc.document.data(Message.serializer()))
                    }

                    ChangeType.MODIFIED -> {
                        println("MODIFIED")

                        _messages.value += dc.document.data(Message.serializer())
                    }

                    ChangeType.REMOVED -> {
                        println("REMOVED")

                        _messages.value -= dc.document.data(Message.serializer())
                    }

                    else -> {
                        TODO()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun createPrivateChatRoom(private: Chat.PrivateChat) {
        val chatRoomId = uuid4().toString()
        _roomId.value = chatRoomId
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
                            roomId = chatRoomId, chatWithUid = private.receiver.uid
                        )
                    ), merge = true
                )
            firebase.collection("accounts").document(private.receiver.uid!!).collection("chats")
                .document(chatRoomId).set(
                    Properties.encodeToMap(
                        AccountRoom(
                            roomId = chatRoomId, chatWithUid = private.sender.uid
                        )
                    ), merge = true
                )
            getMessages(chatRoomId)
            cancel()
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