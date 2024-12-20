package com.example.wibso.screens.create

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.encodeToMap
import com.example.wibso.models.Post
import com.example.wibso.models.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.serialization.ExperimentalSerializationApi

class CreateViewModel : ViewModel() {
    @OptIn(ExperimentalSerializationApi::class)
    fun createPost(content: String, user: User?) {
        val firebase = Firebase.firestore
        val post = Post(content = content)
        if (user?.uid == null) return
        CoroutineScope(Dispatchers.IO).launch {
            firebase.collection("posts").document(post.id).set(Properties.encodeToMap(post))
            firebase.collection("accounts").document(user.uid).collection("posts").document(post.id)
                .set(hashMapOf<String, String>())
            cancel()
        }
    }
}
