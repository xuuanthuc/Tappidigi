package xt.qc.tappidigi.screens.search

import androidx.lifecycle.ViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import xt.qc.tappidigi.models.User

class SeachViewModel : ViewModel() {
    private val _searchedUsers = MutableStateFlow<List<User>>(listOf())
    val searchedUsers: StateFlow<List<User>> = _searchedUsers.asStateFlow()

    fun search(keyword: String) {
        if (keyword.isEmpty()) return
        val firebase = Firebase.firestore
        _searchedUsers.value = emptyList()
        CoroutineScope(Dispatchers.Main).launch {
            firebase.collection("accounts").orderBy("username").startAt(keyword).endAt("$keyword~")
                .get().documents.forEach {
                    val user = it.data(User.serializer())
                    _searchedUsers.update { users ->
                        users + user
                    }
                }
            cancel()
        }
    }
}