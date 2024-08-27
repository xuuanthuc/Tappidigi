package xt.qc.tappidigi

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.KoinContext
import xt.qc.tappidigi.screens.chat.ChatScreen
import xt.qc.tappidigi.screens.communities.CommunitiesScreen
import xt.qc.tappidigi.screens.create.CreateScreen
import xt.qc.tappidigi.screens.home.HomeScreen
import xt.qc.tappidigi.screens.profile.ProfileScreen
import xt.qc.tappidigi.utils.BottomNavigation

@Composable
fun App() {
    MaterialTheme {
        KoinContext {
            val navController: NavHostController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            Scaffold(
                bottomBar = {
                    Row {
                        BottomNavigation.entries.forEach { item ->
                            NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = null) },
                                label = { Text(stringResource(item.title), fontSize = 8.sp, fontWeight = FontWeight.Normal) },
                                selected = currentDestination?.hierarchy?.any { it.route == item.name } == true,
                                onClick = {
                                    navController.navigate(item.name) {
                                        // Pop up to the start destination of the graph to
                                        // avoid building up a large stack of destinations
                                        // on the back stack as users select items
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        // Avoid multiple copies of the same destination when
                                        // reselecting the same item
                                        launchSingleTop = true
                                        // Restore state when reselecting a previously selected item
                                        restoreState = false
                                    }
                                }
                            )
                        }
                    }
                },
                content = {
                    NavHost(
                        navController = navController,
                        startDestination = BottomNavigation.HOME.name,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable(route = BottomNavigation.HOME.name) {
                            HomeScreen()
                        }
                        composable(route = BottomNavigation.COMMUNITIES.name) {
                            CommunitiesScreen()
                        }
                        composable(route = BottomNavigation.CREATE.name) {
                            CreateScreen()
                        }
                        composable(route = BottomNavigation.MESSAGE.name) {
                            ChatScreen()
                        }
                        composable(route = BottomNavigation.PROFILE.name) {
                            ProfileScreen()
                        }
                    }
                }
            )
        }
    }
}