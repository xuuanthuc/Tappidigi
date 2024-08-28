package xt.qc.tappidigi

import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import xt.qc.tappidigi.screens.authentication.SignInWithGoogleManager
import xt.qc.tappidigi.utils.ScreenNavigation

class AppViewModel : ViewModel() {
    fun checkAuthentication(controller: NavHostController, manager: SignInWithGoogleManager) {
        if (manager.firebaseAuth.currentUser != null) {
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