package com.example.wibso.screens.authentication

import com.example.wibso.MainActivity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import com.example.wibso.models.User
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    fun loginWithGoogle(context: Context, onSuccess: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val idToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImVlYzUzNGZhNWI4Y2FjYTIwMWNhOGQwZmY5NmI1NGM1NjIyMTBkMWUiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiIyMzUwNDQ3ODM5MjktdDBnZm1jdjI0b2V1aG4xaWhvbDZvZ2pjaW1wNnBwY3IuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiIyMzUwNDQ3ODM5MjktdGpkcGZyY3NtbXR2bmdrdGZ0NXRlMWllZ2o0ZTcxdXIuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMDUwNzMxMjUwMjYzNDQ5MDYxNDEiLCJlbWFpbCI6InhhYmM4MTg1N0BnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6Ilh5eiBBQkMiLCJwaWN0dXJlIjoiaHR0cHM6Ly9saDMuZ29vZ2xldXNlcmNvbnRlbnQuY29tL2EvQUNnOG9jSWMxT19fRUNQX0tpcDYxNGN4RkRvanl4dGM5NWR6c3lVYXJaZ3hpaGJGM3ROdEl3PXM5Ni1jIiwiZ2l2ZW5fbmFtZSI6Ilh5eiIsImZhbWlseV9uYW1lIjoiQUJDIiwiaWF0IjoxNzM4NzQ1NTg2LCJleHAiOjE3Mzg3NDkxODZ9.mcDC7ke-EohbaOmLRjO_R0E0Q17a-klGhVcfIJQW86pywf5ZWoKsv1FxtwY8XOM4SRDqWPCNIb-TD4YGd92kjs54c-P-qj9AbU0kG6oxN8ekfbJkyoQr_MLGwJm0AfSgM172I_jYdDb7zO8-yGlAAR9mljJLrr5Be7q0vTiCZnhs7ERWo5UQgZgK_8ND7QFZczcCLYi1wSNYNODAvCr1bIpP_6lFZUZL0qnqMVLTxCu4ISwQkYT-EBCq71qjoZLRcuGjoe-bdUSNDdJ1GeQTuavvcXlTRYv0G2GEq6itjwD8wS4KuP0mOtQrOVHx0mpIe5dKLWmSA98bOqR6jh5PJQ"

            println("idToken: $idToken")
            val firebase = Firebase.firestore

            if (idToken != null) {
                try {
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    auth.signInWithCredential(firebaseCredential)

                    auth.currentUser?.uid?.let {
                        try {
                            val user = User(
                                auth.currentUser?.uid,
                                auth.currentUser?.email,
                                auth.currentUser?.email?.replace("@gmail.com", ""),
                                auth.currentUser?.displayName,
                                auth.currentUser?.photoUrl.toString(),
                            )
                            firebase.collection("accounts").document(it)
                                .set(Json.encodeToJsonElement(user), SetOptions.merge())
                        } catch (e: Exception) {
                            auth.signOut()
                        }
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

    suspend fun getIdToken(context: Context): String? {
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId("235044783929-tjdpfrcsmmtvngktft5te1iegj4e71ur.apps.googleusercontent.com")
            .build()
        val manager = CredentialManager.create(context as MainActivity)
        println("idToken: com.example.wibso.MainActivity ${context.hashCode()}")
        println("idToken: ${manager.hashCode()}")
        println("idToken: ${googleIdOption}")
        println("idToken: request")
        val request: GetCredentialRequest =
            GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()
        try {
            println("idToken: request ${request.credentialOptions.first().requestData}")

            val credential = manager.getCredential(
                request = request,
                context = context,
            ).credential

            when (credential) {

                // Passkey credential
                is PublicKeyCredential -> {
                    // Share responseJson such as a GetCredentialResponse on your server to
                    // validate and authenticate
                }

                // Password credential
                is PasswordCredential -> {
                    // Send ID and password to your server to validate and authenticate.
                    val username = credential.id
                    val password = credential.password
                }

                // GoogleIdToken credential
                is CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        try {
                            // Use googleIdTokenCredential and extract the ID to validate and
                            // authenticate on your server.
                            val googleIdTokenCredential = GoogleIdTokenCredential
                                .createFrom(credential.data)
                            // You can use the members of googleIdTokenCredential directly for UX
                            // purposes, but don't use them to store or control access to user
                            // data. For that you first need to validate the token:
                            // pass googleIdTokenCredential.getIdToken() to the backend server.

                        } catch (e: GoogleIdTokenParsingException) {
                            println("Received an invalid google id token response")
                        }
                    } else {
                        // Catch any unrecognized custom credential type here.
                        println("Unexpected type of credential")
                    }
                }
            }

            val googleIdTokenCredential = GoogleIdTokenCredential
                .createFrom(credential.data)

            println("idToken: id ${googleIdTokenCredential.id}")
            println("idToken: token ${googleIdTokenCredential.idToken}")

            return googleIdTokenCredential.idToken
        } catch (e: GetCredentialException) {
            println("idToken: e ${e}")

            return null
        }
    }
}