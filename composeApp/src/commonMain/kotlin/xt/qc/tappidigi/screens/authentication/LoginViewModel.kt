package xt.qc.tappidigi.screens.authentication

import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import xt.qc.tappidigi.models.User
import kotlinx.serialization.*
import kotlinx.serialization.properties.*

class LoginViewModel : ViewModel() {
    @OptIn(ExperimentalSerializationApi::class)
    fun loginWithGoogle(manager: SignInWithGoogleManager, onSuccess: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val auth = manager.firebaseAuth
            val idToken = manager.getIdToken()
            val firebase = Firebase.firestore

            if (idToken != null) {
                try {
                    val firebaseCredential = GoogleAuthProvider.credential(idToken, null)
                    auth.signInWithCredential(firebaseCredential)
                    val user = User(
                        auth.currentUser?.uid,
                        auth.currentUser?.email,
                        auth.currentUser?.displayName,
                        auth.currentUser?.photoURL,
                    )
                    auth.currentUser?.uid?.let {
                        firebase.collection("accounts").document(it)
                            .set(Properties.encodeToMap(user), merge = true)
                    }

                    onSuccess.invoke()
                } catch (e: Exception) {
                    println("Error: ${e.message}")
                }
            }
        }
    }
}