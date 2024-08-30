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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import xt.qc.tappidigi.screens.authentication.LoginScreen
import xt.qc.tappidigi.screens.authentication.SignInWithGoogleManager
import xt.qc.tappidigi.screens.main.MainScreen
import xt.qc.tappidigi.screens.profile.EditProfileScreen
import xt.qc.tappidigi.screens.profile.ProfileViewModel
import xt.qc.tappidigi.screens.splash.SplashScreen
import xt.qc.tappidigi.utils.ScreenNavigation

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
            }
        }
    }
}