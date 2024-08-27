package xt.qc.tappidigi

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.compose.KoinContext

enum class CupcakeScreen {
    HOME, DETAIL
}


@Composable
fun App() {
    MaterialTheme {
        KoinContext {
            val navController: NavHostController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = CupcakeScreen.HOME.name,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(route = CupcakeScreen.HOME.name) {
                    Home(navController)
                }
                composable(route = CupcakeScreen.DETAIL.name) {
                    Detail()
                }
            }

        }
    }
}