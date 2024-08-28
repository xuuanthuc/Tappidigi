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
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import xt.qc.tappidigi.screens.authentication.LoginScreen
import xt.qc.tappidigi.screens.authentication.SignInWithGoogleManager
import xt.qc.tappidigi.screens.main.MainScreen
import xt.qc.tappidigi.utils.ScreenNavigation

@Composable
fun App() {
    MaterialTheme {
        KoinContext {
            val navController: NavHostController = rememberNavController()
            val viewModel: AppViewModel = koinInject<AppViewModel>()
            val manager = koinInject<SignInWithGoogleManager>()

            LaunchedEffect(Unit) {
                viewModel.checkAuthentication(navController, manager)
            }
            NavHost(
                navController = navController,
                startDestination = ScreenNavigation.LOGIN.name,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(route = ScreenNavigation.LOGIN.name) {
                    LoginScreen(navController)
                }
                composable(route = ScreenNavigation.MAIN.name) {
                    MainScreen()
                }
            }
        }
    }
}