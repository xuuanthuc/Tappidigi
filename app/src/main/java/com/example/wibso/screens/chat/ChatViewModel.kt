package com.example.wibso.screens.chat

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.wibso.api.Usecase
import com.example.wibso.models.AccountRoom
import com.example.wibso.models.Chat
import com.example.wibso.models.Emoji
import com.example.wibso.models.Message
import com.example.wibso.models.MessageDocumentSnapshot
import com.example.wibso.models.MessageStatus
import com.example.wibso.models.User
import com.example.wibso.screens.create.UploadCallback
import com.example.wibso.utils.ChatThemes
import com.example.wibso.utils.GreenPalette
import com.example.wibso.utils.Status
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storageMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.encodeToMap
import java.io.File
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


enum class ActionToolState {
    AUDIO,
    CAMERA,
    GALLERY,
    EMOJI,
    NONE,
}

class ChatViewModel(groupUsers: List<User>?, sender: User?, receiver: User?) : ViewModel() {
    private val firebase = Firebase.firestore
    private val _groupUsers = MutableStateFlow<List<User>>(listOf())
    private val groupUsers: StateFlow<List<User>> = _groupUsers.asStateFlow()
    private val storage = FirebaseStorage.getInstance("gs://tappidigi.firebasestorage.app")

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

    var actionState: MutableState<ActionToolState> = mutableStateOf(ActionToolState.NONE)

    var isFocused: MutableState<Boolean> = mutableStateOf(false)

    val status: MutableState<Status> = mutableStateOf(Status.LOADING)

    init {
        _groupUsers.value = groupUsers ?: listOf()
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun sendMessage(message: Message) {
        if (roomId.value == null || message.ownerId.isEmpty() || message.content.isEmpty()) return
        _messages.value.add(0, message)
        try {
            println("Sending message!")
            firebase.collection("chats").document(roomId.value!!).collection("messages")
                .document(message.id).set(Properties.encodeToMap(message))
        } catch (e: Exception) {
            println(e)
            message.status.value = MessageStatus.ERROR
        }
    }

    fun uploadImagesToFirebaseStorage(uris: ArrayList<Uri>, callback: UploadCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            val storageRef: StorageReference = storage.reference
            val downloadUrls = ArrayList<Uri>()

            try {
                uris.forEach { uri ->
                    val imageRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")
                    imageRef.putFile(uri).await()
                    val downloadUrl = imageRef.downloadUrl.await()
                    downloadUrls.add(downloadUrl)
                }
                callback.onSuccess(downloadUrls)
            } catch (exception: Exception) {
                callback.onFailure(exception)
            }
        }
    }

    fun uploadAudioToFirebaseStorage(message: Message) {
        CoroutineScope(Dispatchers.IO).launch {
            val storageRef: StorageReference = storage.reference
            try {
                println(message.attachment)
                if(message.attachment == null) return@launch
                val metadata = StorageMetadata.Builder().setCustomMetadata("lastID", File(message.attachment).path).build()
                val imageRef = storageRef.child("audios/${System.currentTimeMillis()}.aac")
                val uri = Uri.fromFile(File(message.attachment))
                imageRef.putFile(uri, metadata).await()
                val downloadUrl = imageRef.downloadUrl.await()
                println(downloadUrl)
                val newMessage = message.copy(attachment = downloadUrl.toString())
                sendMessage(newMessage)
            } catch (exception: Exception) {
                println(exception)
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun reSendMessage(message: Message) {
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

    fun checkChatRoomExists(chat: Chat.PrivateChat) {
        var isExists = false
        firebase.collection("accounts").document(chat.sender.uid!!).collection("chats").get()
            .addOnSuccessListener {
                it.documents.forEach { doc ->
                    val room = doc.toObject<AccountRoom>()
                    if (room != null) {
                        if (room.chatWithUid == chat.receiver.uid) {
                            isExists = true
                            _roomId.value = room.roomId
                        }
                    }
                }
                if (isExists && roomId.value != null) {
                    getMessages(roomId.value!!)
                } else {
                    createPrivateChatRoom(chat)
                }
            }
    }

    fun onShowingDate(message: Message?) {
        _showingDateId.value = message?.id
    }

    private fun getMessages(roomId: String) {
        firebase.collection("chats").document(roomId).collection("messages").orderBy("createdAt")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                for (dc in snapshot!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {

                            val msg = dc.document.toObject<MessageDocumentSnapshot>()
                            val json = Json.encodeToString(msg)
                            val message = Json.decodeFromString<Message>(json)
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
                            val msg = dc.document.toObject<MessageDocumentSnapshot>()
                            val json = Json.encodeToString(msg)
                            val message = Json.decodeFromString<Message>(json)
                            _messages.value += message
                        }

                        DocumentChange.Type.REMOVED -> {
                            println("REMOVED")
                            val msg = dc.document.toObject<MessageDocumentSnapshot>()
                            val json = Json.encodeToString(msg)
                            val message = Json.decodeFromString<Message>(json)
                            _messages.value -= message
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
    fun createGroupChatRoom() {
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
            val emojis = Usecase().getEmojis() ?: emptyList()
            _emojis.value = emojis
            _groupEmojis.value = emojis.groupBy { it.group }
            println(_emojis.value.size)
        }
    }
}