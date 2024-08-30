package xt.qc.tappidigi.screens.authentication

expect class SignInWithGoogleManager {
    suspend fun getIdToken(): String?
}

