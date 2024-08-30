package xt.qc.tappidigi.screens.profile

import androidx.lifecycle.ViewModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.encodeToMap
import xt.qc.tappidigi.AppViewModel
import xt.qc.tappidigi.models.User
import xt.qc.tappidigi.screens.authentication.SignInWithGoogleManager

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<User?>(null)
    val uiState: StateFlow<User?> = _uiState.asStateFlow()

    fun setProfile(user: User) {
        _uiState.value = user
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun updateProfile(bio: String) {
        val firebase = Firebase.firestore
        if(_uiState.value == null) return
        val user = _uiState.value?.copy(bio = bio)
        if (bio.isNotEmpty() && user != null && user.uid != null) {
            CoroutineScope(Dispatchers.IO).launch {
                firebase.collection("accounts").document(user.uid)
                    .set(Properties.encodeToMap(user), merge = true)
                cancel()
            }
        }
    }

    fun logout(
        onSuccess: () -> Unit,
    ){
        val firebaseAuth: FirebaseAuth = Firebase.auth
        CoroutineScope(Dispatchers.Main).launch {
            firebaseAuth.signOut()
            println(firebaseAuth.currentUser)
            if(firebaseAuth.currentUser == null){
                _uiState.value = null
                onSuccess.invoke()
            }
            cancel()
        }
    }
}