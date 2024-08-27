package xt.qc.tappidigi

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

class GreetingViewModel : ViewModel() {
    var showContent = mutableStateOf(false)

    fun onShowContentClicked() {
        println(hashCode())
        showContent.value = !showContent.value
    }

    suspend fun addData() {
        val db = Firebase.firestore
        val city = hashMapOf(
            "name" to "Los Angeles",
            "state" to "CA",
            "country" to "USA",
        )
        db.collection("cities").add(city)
    }
}