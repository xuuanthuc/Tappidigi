package com.example.wibso.screens.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.encodeToMap
import com.example.wibso.models.Post
import com.example.wibso.models.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.serialization.properties.decodeFromMap

class ProfileViewModel : ViewModel() {
    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> = _userState.asStateFlow()

    private val _listPostState = MutableStateFlow<List<Post>>(listOf())
    val listPostState: StateFlow<List<Post>> = _listPostState.asStateFlow()

    fun setProfile(user: User) {
        _userState.value = user
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun updateProfile(bio: String) {
        val firebase = Firebase.firestore
        if (_userState.value == null) return
        val user = _userState.value?.copy(bio = bio)
        if (bio.isNotEmpty() && user != null && user.uid != null) {
            CoroutineScope(Dispatchers.IO).launch {
                firebase.collection("accounts").document(user.uid)
                    .set(Properties.encodeToMap(user), SetOptions.merge())
                cancel()
            }
        }
    }

    fun logout(
        onSuccess: () -> Unit,
    ) {
        val firebaseAuth: FirebaseAuth = Firebase.auth
        CoroutineScope(Dispatchers.Main).launch {
            firebaseAuth.signOut()
            println(firebaseAuth.currentUser)
            if (firebaseAuth.currentUser == null) {
                _userState.value = null
                onSuccess.invoke()
            }
            cancel()
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun getListPost() {
        val firebase = Firebase.firestore
        val user = _userState.value ?: return
        val userPostId = mutableListOf<String>()
        CoroutineScope(Dispatchers.IO).launch {
            val postIds = firebase.collection("accounts").document(user.uid!!)
                .collection("posts").get().result.documents
            postIds.forEach {
                userPostId.add(it.id)
            }
            if(userPostId.isNotEmpty()){
                firebase.collection("posts")
                    .whereArrayContains("id") {
                        userPostId
                    }
                    .get().result.documents.forEach {
                        val post = Properties.decodeFromMap<Post>(map = it?.data ?: mapOf())
                        _listPostState.update { posts ->
                            posts + post
                        }
                    }
            }
            cancel()
        }
    }
}