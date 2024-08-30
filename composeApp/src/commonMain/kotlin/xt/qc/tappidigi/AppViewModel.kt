package xt.qc.tappidigi

import androidx.lifecycle.ViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import xt.qc.tappidigi.models.User
import xt.qc.tappidigi.screens.profile.ProfileViewModel
import xt.qc.tappidigi.utils.ScreenNavigation

class AppViewModel : ViewModel() {
    lateinit var navHostController: NavHostController

    fun checkAuthentication(
        profileViewModel: ProfileViewModel
    ) {
        println(navHostController.graph.findStartDestination().route)
        val firebaseAuth: FirebaseAuth = Firebase.auth
        if (firebaseAuth.currentUser != null) {
            val currentUser = firebaseAuth.currentUser;
            val user = User(
                currentUser?.uid,
                currentUser?.email,
                currentUser?.email?.replace("@gmail.com", ""),
                currentUser?.displayName,
                currentUser?.photoURL,
            )
            profileViewModel.setProfile(user)
            navHostController.navigate(ScreenNavigation.MAIN.name) {
                popUpTo(0) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        } else {
            navHostController.navigate(ScreenNavigation.LOGIN.name) {
                popUpTo(0) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }
}