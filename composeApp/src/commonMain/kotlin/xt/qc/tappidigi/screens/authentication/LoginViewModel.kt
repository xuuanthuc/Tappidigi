package xt.qc.tappidigi.screens.authentication

import androidx.lifecycle.ViewModel
import dev.gitlive.firebase.auth.GoogleAuthProvider

class LoginViewModel : ViewModel() {
    suspend fun loginWithGoogle(manager: SignInWithGoogleManager, onSuccess: () -> Unit) {
        val auth = manager.firebaseAuth
        val idToken = manager.getIdToken()
        println("idToken = $idToken")
        if (idToken != null) {
            val firebaseCredential = GoogleAuthProvider.credential(idToken, null)
            auth.signInWithCredential(firebaseCredential)
            onSuccess.invoke()
        }
    }
}