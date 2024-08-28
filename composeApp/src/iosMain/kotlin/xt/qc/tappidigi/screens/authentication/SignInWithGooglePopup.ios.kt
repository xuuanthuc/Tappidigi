package xt.qc.tappidigi.screens.authentication

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth

actual class SignInWithGoogleManager {
    actual suspend fun getIdToken(): String? {
        return ""
    }

    actual val firebaseAuth: FirebaseAuth = Firebase.auth
}

