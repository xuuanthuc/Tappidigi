package xt.qc.tappidigi.screens.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import xt.qc.tappidigi.models.User

class ProfileViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(User())
    val uiState: StateFlow<User> = _uiState.asStateFlow()

    fun updateProfile(user: User) {
        _uiState.value = user
    }
}