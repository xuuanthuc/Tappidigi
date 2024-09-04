package xt.qc.tappidigi

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
import xt.qc.tappidigi.models.Chat
import xt.qc.tappidigi.models.User
import xt.qc.tappidigi.screens.authentication.LoginScreen
import xt.qc.tappidigi.screens.chat.ChatScreen
import xt.qc.tappidigi.screens.main.MainScreen
import xt.qc.tappidigi.screens.profile.EditProfileScreen
import xt.qc.tappidigi.screens.profile.PreviewUserProfile
import xt.qc.tappidigi.screens.splash.SplashScreen
import xt.qc.tappidigi.utils.CustomNavType
import xt.qc.tappidigi.utils.ScreenNavigation
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
                composable<Chat> (
                    typeMap = mapOf(
                        typeOf<List<User>>() to CustomNavType.UserListType
                    )
                ){
                    val chat: Chat = it.toRoute()
                    ChatScreen(chat)
                }
            }
        }
    }
}