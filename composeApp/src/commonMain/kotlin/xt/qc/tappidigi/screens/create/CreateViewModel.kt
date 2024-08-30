package xt.qc.tappidigi.screens.create

import androidx.lifecycle.ViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.encodeToMap
import xt.qc.tappidigi.models.Post
import xt.qc.tappidigi.models.User

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
