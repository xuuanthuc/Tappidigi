package xt.qc.tappidigi.screens.authentication

import dev.gitlive.firebase.auth.FirebaseAuth

expect class SignInWithGoogleManager {
    suspend fun getIdToken(): String?

    val firebaseAuth: FirebaseAuth
}

