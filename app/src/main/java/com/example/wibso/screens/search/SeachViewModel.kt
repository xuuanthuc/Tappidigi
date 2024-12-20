package com.example.wibso.screens.search

import androidx.lifecycle.ViewModel
import com.example.wibso.models.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SeachViewModel : ViewModel() {
    private val _searchedUsers = MutableStateFlow<List<User>>(listOf())
    val searchedUsers: StateFlow<List<User>> = _searchedUsers.asStateFlow()

    fun search(keyword: String) {
        if (keyword.isEmpty()) return
        val firebase = Firebase.firestore
        _searchedUsers.value = emptyList()
        firebase.collection("accounts").orderBy("username").startAt(keyword).endAt("$keyword~")
            .get().addOnSuccessListener {
                it.documents.forEach {
                    val user = it.toObject<User>()
                    user?.let {
                        _searchedUsers.update { users ->
                            users + user
                        }
                    }
                }
            }
    }
}