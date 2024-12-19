package com.example.wibso

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import com.example.wibso.models.Chat
import com.example.wibso.models.User
import com.example.wibso.screens.authentication.LoginScreen
import com.example.wibso.screens.chat.ChatScreen
import com.example.wibso.screens.main.MainScreen
import com.example.wibso.screens.profile.EditProfileScreen
import com.example.wibso.screens.profile.PreviewUserProfile
import com.example.wibso.screens.splash.SplashScreen
import com.example.wibso.utils.CustomNavType
import com.example.wibso.utils.ScreenNavigation
import kotlin.reflect.typeOf

@Composable
fun App() {
    MaterialTheme {
        KoinContext {
            val viewModel: AppViewModel = koinInject<AppViewModel>()
            val navController: NavHostController = rememberNavController()

            LaunchedEffect(
                Unit
            ) {
                viewModel.navHostController = navController
            }

            NavHost(
                navController = navController,
                startDestination = ScreenNavigation.SPLASH.name,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(route = ScreenNavigation.SPLASH.name) {
                    SplashScreen()
                }
                composable(route = ScreenNavigation.LOGIN.name) {
                    LoginScreen()
                }
                composable(route = ScreenNavigation.MAIN.name) {
                    MainScreen()
                }
                composable(route = ScreenNavigation.EDIT_PROFILE.name) {
                    EditProfileScreen()
                }

                composable<User> {
                    val user: User = it.toRoute()
                    PreviewUserProfile(user)
                }
                composable<Chat.GroupChat> (
                    typeMap = mapOf(
                        typeOf<List<User>>() to CustomNavType.UserListType
                    )
                ){
                    val chat: Chat.GroupChat = it.toRoute()
                    ChatScreen(group = chat)
                }
                composable<Chat.PrivateChat> (
                    typeMap = mapOf(
                        typeOf<User>() to CustomNavType.UserType
                    )
                ){
                    val chat: Chat.PrivateChat = it.toRoute()
                    ChatScreen(private = chat)
                }
            }
        }
    }
}