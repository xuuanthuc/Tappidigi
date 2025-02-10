package com.example.wibso.screens.create

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.wibso.models.GalleryContent
import com.example.wibso.models.Media
import com.example.wibso.models.Post
import com.example.wibso.models.User
import com.example.wibso.screens.chat.ActionToolState
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.encodeToMap

class CreateViewModel : ViewModel() {
    private val storage = FirebaseStorage.getInstance("gs://tappidigi.firebasestorage.app")
    var actionState: MutableState<ActionToolState> = mutableStateOf(ActionToolState.NONE)

    @OptIn(ExperimentalSerializationApi::class)
    fun createPost(
        title: String,
        description: String,
        user: User?,
        galleryContents: SnapshotStateList<GalleryContent>
    ) {
        val firebase = Firebase.firestore
        val post = Post(title = title, description = description)
        if (user?.uid == null) return
        CoroutineScope(Dispatchers.IO).launch {
            if (galleryContents.isEmpty()) return@launch
            val content = galleryContents.first()
            uploadImageToFirebaseStorage(
                Uri.parse(content.uri),
                object : UploadCallback {
                    override fun onSuccess(downloadUrl: Uri) {
                        post.media = Media(type = content.type, url = downloadUrl.path)
                        firebase.collection("posts").document(post.id)
                            .set(Properties.encodeToMap(post))
                        firebase.collection("accounts").document(user.uid).collection("posts")
                            .document(post.id)
                            .set(hashMapOf<String, String>())
                        cancel()
                    }

                    override fun onFailure(exception: Exception) {
                        println("Failed to upload image: ${exception.message}")
                    }
                },
            )

        }
    }

    private fun uploadImageToFirebaseStorage(uri: Uri, callback: UploadCallback) {
        val storageRef: StorageReference = storage.reference

        // Create a reference to the image you want to upload
        val imageRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")

        // Upload the image
        val uploadTask = imageRef.putFile(uri)
        uploadTask.addOnSuccessListener {
            // Get the download URL after successful upload
            imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                println("Image uploaded successfully. Download URL: $downloadUrl")
                callback.onSuccess(downloadUrl)
            }
        }.addOnFailureListener { exception ->
            callback.onFailure(exception)
        }

    }
}

interface UploadCallback {
    fun onSuccess(downloadUrl: Uri)
    fun onFailure(exception: Exception)
}