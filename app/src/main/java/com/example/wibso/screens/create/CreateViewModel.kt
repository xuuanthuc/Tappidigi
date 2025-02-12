package com.example.wibso.screens.create

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.wibso.models.GalleryContent
import com.example.wibso.models.GalleryType
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
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.encodeToMap
import java.io.File
import java.util.ArrayList

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
        if (galleryContents.isEmpty()) return

        val uris = galleryContents.mapNotNull { it.uri?.let { uri -> Uri.fromFile(File(uri)) } }
        val uriArrayList = ArrayList(uris)
        uploadImagesToFirebaseStorage(uriArrayList, object : UploadCallback {
            override fun onSuccess(downloadUrls: ArrayList<Uri>) {
                CoroutineScope(Dispatchers.IO).launch {
                    post.medias =
                        downloadUrls.map {
                            Media(
                                type = GalleryType.IMAGE.toString(),
                                url = it.toString()
                            )
                        } as ArrayList<Media>
                    val postMap = post.toMap()
                    firebase.collection("posts").document(post.id)
                        .set(postMap)
                    firebase.collection("accounts").document(user.uid).collection("posts")
                        .document(post.id)
                        .set(hashMapOf<String, String>())
                    cancel()
                }
            }

            override fun onFailure(exception: Exception) {
                println("Failed to upload images: ${exception.message}")
            }
        })
    }

    private fun uploadImagesToFirebaseStorage(uris: ArrayList<Uri>, callback: UploadCallback) {
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
}

interface UploadCallback {
    fun onSuccess(downloadUrls: ArrayList<Uri>)
    fun onFailure(exception: Exception)
}