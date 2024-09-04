package xt.qc.tappidigi.screens.chat

import androidx.lifecycle.ViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import xt.qc.tappidigi.models.User

class ChatViewModel(users: List<User>, currentUser: User): ViewModel() {
    private val firebase = Firebase.firestore
    private val _groupUsers = MutableStateFlow<List<User>>(listOf())
    private val groupUsers: StateFlow<List<User>> = _groupUsers.asStateFlow()

    init {
        _groupUsers.value = users
    }

    fun sendMessage(content: String){
        groupUsers.value.forEach {
//            firebase.collection("accounts").document(it.uid!!).collection("chats").document().set(mapOf("content" to content))")
        }
    }
}