package xt.qc.tappidigi

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xt.qc.tappidigi.models.User
import xt.qc.tappidigi.screens.authentication.SignInWithGoogleManager
import xt.qc.tappidigi.screens.profile.ProfileViewModel
import xt.qc.tappidigi.utils.ScreenNavigation

class AppViewModel : ViewModel() {
    fun checkAuthentication(controller: NavHostController, manager: SignInWithGoogleManager, profileViewModel: ProfileViewModel) {
        CoroutineScope(Dispatchers.Main).launch {
            if (manager.firebaseAuth.currentUser != null) {
                val currentUser = manager.firebaseAuth.currentUser;
                val user = User(
                    currentUser?.uid,
                    currentUser?.email,
                    currentUser?.email?.replace("@gmail.com", ""),
                    currentUser?.displayName,
                    currentUser?.photoURL,
                )
                profileViewModel.updateProfile(user)
                controller.navigate(ScreenNavigation.MAIN.name) {
                    popUpTo(ScreenNavigation.LOGIN.name) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            } else {
                controller.navigate(ScreenNavigation.LOGIN.name) {
                    launchSingleTop = true
                }
            }
        }
    }
}