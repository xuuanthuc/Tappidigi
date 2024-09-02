package xt.qc.tappidigi.screens.authentication

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import xt.qc.tappidigi.MainApplication


actual class SignInWithGoogleManager(
    private val context: Context,
    private val credentialManager: CredentialManager
) {
    actual suspend fun getIdToken(): String? {
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId("235044783929-tjdpfrcsmmtvngktft5te1iegj4e71ur.apps.googleusercontent.com")
            .build()
        val request: GetCredentialRequest =
            GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()
        try {
            val result = credentialManager.getCredential(
                request = request,
                context = (context.applicationContext as MainApplication).getCurrentActivity()!!,
            ).credential
            val googleIdTokenCredential = GoogleIdTokenCredential
                .createFrom(result.data)

            return googleIdTokenCredential.idToken
        } catch (e: GetCredentialException) {
            return null
        }
    }
}

