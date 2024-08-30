package xt.qc.tappidigi.screens.authentication

import androidx.lifecycle.ViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.encodeToMap
import xt.qc.tappidigi.models.User

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    @OptIn(ExperimentalSerializationApi::class)
    fun loginWithGoogle(manager: SignInWithGoogleManager, onSuccess: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val idToken = manager.getIdToken()
            val firebase = Firebase.firestore

            if (idToken != null) {
                try {
                    val firebaseCredential = GoogleAuthProvider.credential(idToken, null)
                    auth.signInWithCredential(firebaseCredential)
                    val user = User(
                        auth.currentUser?.uid,
                        auth.currentUser?.email,
                        auth.currentUser?.email?.replace("@gmail.com", ""),
                        auth.currentUser?.displayName,
                        auth.currentUser?.photoURL,
                    )
                    auth.currentUser?.uid?.let {
                        firebase.collection("accounts").document(it)
                            .set(Properties.encodeToMap(user), merge = true)
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        onSuccess.invoke()
                    }
                } catch (e: Exception) {
                    println("Error: ${e.message}")
                }
            }
            cancel()
        }
    }
}