package com.example.wibso.screens.search

import androidx.lifecycle.ViewModel
import com.example.wibso.models.AccountRoom
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.wibso.models.User
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.decodeFromMap

class SeachViewModel : ViewModel() {
    private val _searchedUsers = MutableStateFlow<List<User>>(listOf())
    val searchedUsers: StateFlow<List<User>> = _searchedUsers.asStateFlow()

    @OptIn(ExperimentalSerializationApi::class)
    fun search(keyword: String) {
        if (keyword.isEmpty()) return
        val firebase = Firebase.firestore
        _searchedUsers.value = emptyList()
        CoroutineScope(Dispatchers.Main).launch {
            firebase.collection("accounts").orderBy("username").startAt(keyword).endAt("$keyword~")
                .get().result.documents.forEach {
                    val user = Properties.decodeFromMap<User>(it?.data ?: mapOf())
                    _searchedUsers.update { users ->
                        users + user
                    }
                }
            cancel()
        }
    }
}