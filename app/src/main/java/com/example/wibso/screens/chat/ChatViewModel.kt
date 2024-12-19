package com.example.wibso.screens.chat

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.encodeToMap
import com.example.wibso.api.Usecase
import com.example.wibso.models.AccountRoom
import com.example.wibso.models.Chat
import com.example.wibso.models.Emoji
import com.example.wibso.models.Message
import com.example.wibso.models.MessageStatus
import com.example.wibso.models.MessageType
import com.example.wibso.models.User
import com.example.wibso.utils.ChatThemes
import com.example.wibso.utils.GreenPalette
import com.example.wibso.utils.Status
import com.google.api.ChangeType
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.snapshots
import kotlinx.serialization.properties.decodeFromMap
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

enum class EmojiState {
    SHOW, HIDE
}

enum class AlbumState {
    SHOW, HIDE
}

class ChatViewModel(groupUsers: List<User>?, sender: User?, receiver: User?) : ViewModel() {
    private val firebase = Firebase.firestore
    private val _groupUsers = MutableStateFlow<List<User>>(listOf())
    private val groupUsers: StateFlow<List<User>> = _groupUsers.asStateFlow()

    private val _messages = MutableStateFlow<SnapshotStateList<Message>>(mutableStateListOf())
    val message: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _roomId = MutableStateFlow<String?>(null)
    private val roomId: StateFlow<String?> = _roomId.asStateFlow()

    private val _showingDateId = MutableStateFlow<String?>(null)
    val showingDateId: StateFlow<String?> = _showingDateId.asStateFlow()

    private val _emojis = MutableStateFlow<List<Emoji>>(emptyList())
    val emojis: StateFlow<List<Emoji>> = _emojis.asStateFlow()


    private val _groupEmojis = MutableStateFlow<Map<String, List<Emoji>>?>(null)
    val groupEmoji: StateFlow<Map<String, List<Emoji>>?> = _groupEmojis.asStateFlow()

    val theme: MutableState<ChatThemes> = mutableStateOf(GreenPalette)

    var emojiState: MutableState<EmojiState> = mutableStateOf(EmojiState.HIDE)

    var albumState: MutableState<AlbumState> = mutableStateOf(AlbumState.HIDE)

    var isFocused: MutableState<Boolean> = mutableStateOf(false)

    val status: MutableState<Status> = mutableStateOf(Status.LOADING)

    init {
        _groupUsers.value = groupUsers ?: listOf()
    }

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun sendMessage(content: String, uid: String) {
        if (roomId.value == null || uid.isEmpty()) return
        val message =
            Message(
                content = content,
                ownerId = uid,
                messageType = MessageType.TEXT.ordinal,
                status = mutableStateOf(MessageStatus.SENDING),
            )
        _messages.value.add(0, message)
        try {
            firebase.collection("chats").document(roomId.value!!).collection("messages")
                .document(message.id).set(Properties.encodeToMap(message))
        } catch (e: Exception) {
            message.status.value = MessageStatus.ERROR
        }
    }


    @OptIn(ExperimentalSerializationApi::class)
    suspend fun reSendMessage(message: Message) {
        message.status.value = MessageStatus.SENDING;
        try {
            firebase.collection("chats").document(roomId.value!!).collection("messages")
                .document(message.id).set(Properties.encodeToMap(message))
        } catch (e: Exception) {
            message.status.value = MessageStatus.ERROR
        }
    }


    private fun findMessage(id: String): Message? {
        return _messages.value.firstOrNull {
            it.id == id
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun checkChatRoomExists(chat: Chat.PrivateChat) {
        var isExists = false
        val myRooms =
            firebase.collection("accounts").document(chat.sender.uid!!).collection("chats")
                .get().result.documents
        myRooms.forEach {
            val room = Properties.decodeFromMap<AccountRoom>(it?.data ?: mapOf())
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

    fun onShowingDate(message: Message?) {
        _showingDateId.value = message?.id
    }

    @OptIn(ExperimentalSerializationApi::class)
    private suspend fun getMessages(roomId: String) {
        val snapshot =
            firebase.collection("chats").document(roomId).collection("messages")
                .orderBy("createdAt").snapshots()
        snapshot.collect {
            for (dc in it.documentChanges) {
                when (dc.type) {
                    DocumentChange.Type.ADDED -> {
                        println("ADDED")
                        val message = Properties.decodeFromMap<Message>(dc.document.data)
                        val found = findMessage(message.id)
                        if (found != null) {
                            found.status.value = MessageStatus.SENT
                        } else {
                            message.status.value = MessageStatus.SENT
                            _messages.value.add(0, message)
                        }
                    }

                    DocumentChange.Type.MODIFIED -> {
                        println("MODIFIED")

                        _messages.value += Properties.decodeFromMap<Message>(dc.document.data)
                    }

                    DocumentChange.Type.REMOVED -> {
                        println("REMOVED")

                        _messages.value -= Properties.decodeFromMap<Message>(dc.document.data)
                    }

                    else -> {
                        TODO()
                    }
                }
            }
            if (status.value != Status.LOADED) {
                status.value = Status.LOADED
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class, ExperimentalUuidApi::class)
    fun createPrivateChatRoom(private: Chat.PrivateChat) {
        val chatRoomId = Uuid.random().toString()
        _roomId.value = chatRoomId
        CoroutineScope(Dispatchers.IO).launch {
            //create in chat collection for two people
            firebase.collection("chats").document(chatRoomId).collection("members")
                .document(private.sender.uid!!)
                .set(Properties.encodeToMap(private.sender), SetOptions.merge())
            firebase.collection("chats").document(chatRoomId).collection("members")
                .document(private.receiver.uid!!)
                .set(Properties.encodeToMap(private.receiver), SetOptions.merge())

            //create in account chat collection for each people
            firebase.collection("accounts").document(private.sender.uid!!).collection("chats")
                .document(chatRoomId).set(
                    Properties.encodeToMap(
                        AccountRoom(
                            roomId = chatRoomId, chatWithUid = private.receiver.uid
                        )
                    ), SetOptions.merge()
                )
            firebase.collection("accounts").document(private.receiver.uid!!).collection("chats")
                .document(chatRoomId).set(
                    Properties.encodeToMap(
                        AccountRoom(
                            roomId = chatRoomId, chatWithUid = private.sender.uid
                        )
                    ), SetOptions.merge()
                )
            getMessages(chatRoomId)
            cancel()
        }
    }

    @OptIn(ExperimentalSerializationApi::class, ExperimentalUuidApi::class)
    suspend fun createGroupChatRoom() {
        val chatRoomId = Uuid.random().toString()

        groupUsers.value.forEach {
            if (it.uid == null) return
            firebase.collection("chats").document(chatRoomId).collection("members").document(it.uid)
                .set(Properties.encodeToMap(it), SetOptions.merge())
            firebase.collection("accounts").document(it.uid).collection("chats")
                .document(chatRoomId).set(Properties.encodeToMap(AccountRoom(roomId = chatRoomId)))
        }
    }

    suspend fun getEmojiProvider() {
        if (_emojis.value.isEmpty()) {
            val emojis = Usecase().getEmojis()
            _emojis.value = emojis
            _groupEmojis.value = emojis.groupBy { it.group }
            println(_emojis.value.size)
        }
    }
}