package xt.qc.tappidigi.screens.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import org.koin.compose.koinInject
import xt.qc.tappidigi.screens.chat.ChatScreen
import xt.qc.tappidigi.screens.communities.CommunitiesScreen
import xt.qc.tappidigi.screens.create.CreateScreen
import xt.qc.tappidigi.screens.home.HomeScreen
import xt.qc.tappidigi.screens.profile.ProfileScreen
import xt.qc.tappidigi.screens.profile.ProfileViewModel
import xt.qc.tappidigi.screens.search.SearchScreen
import xt.qc.tappidigi.utils.BottomNavigation

@Composable
fun MainScreen() {
    val navController: NavHostController = rememberNavController()

    Scaffold(
        bottomBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                BottomNavigation.entries.forEach { item ->
                    BottomBarItem(item) {
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
                            restoreState = true
                        }
                    }
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
                composable(route = BottomNavigation.SEARCH.name) {
                    SearchScreen()
                }
                composable(route = BottomNavigation.CREATE.name) {
                    CreateScreen()
                }
                composable(route = BottomNavigation.MESSAGE.name) {
                    ChatScreen()
                }
//                composable(route = BottomNavigation.NOTIFICATION.name) {
//                    NotificationScreen()
//                }
                composable(route = BottomNavigation.PROFILE.name) {
                    ProfileScreen()
                }
            }
        },
    )
}

@Composable
fun BottomBarItem(item: BottomNavigation, onTap: () -> Unit) {
    val profile = koinInject<ProfileViewModel>()
    Box(modifier = Modifier.size(40.dp)) {
        when {
            item == BottomNavigation.PROFILE -> {
                AsyncImage(
                    model = profile.userState.collectAsState().value?.photoUrl,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp).clip(CircleShape).clickable {
                        onTap.invoke()
                    },
                )
            }

            else -> {
                Button(
                    onClick = {
                        onTap.invoke()
                    },
                    modifier = Modifier.size(40.dp),
                    content = {
                        Icon(
                            item.icon,
                            contentDescription = null,
                        )
                    },
                    contentPadding = PaddingValues(0.dp),
                )
            }
        }
    }
}