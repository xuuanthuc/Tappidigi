package com.example.wibso

import androidx.lifecycle.ViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.example.wibso.models.User
import com.example.wibso.screens.profile.ProfileViewModel
import com.example.wibso.utils.ScreenNavigation

class AppViewModel : ViewModel() {
    lateinit var navHostController: NavHostController

    fun checkAuthentication(
        profileViewModel: ProfileViewModel
    ) {
        println(navHostController.graph.findStartDestination().route)
        val firebaseAuth: FirebaseAuth = Firebase.auth
        if (firebaseAuth.currentUser != null) {
            val currentUser = firebaseAuth.currentUser

            val user = User(
                currentUser?.uid,
                currentUser?.email,
                currentUser?.email?.replace("@gmail.com", ""),
                currentUser?.displayName,
                currentUser?.photoUrl.toString(),
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