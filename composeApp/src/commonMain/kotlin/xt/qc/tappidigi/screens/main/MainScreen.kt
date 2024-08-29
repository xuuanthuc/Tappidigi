package xt.qc.tappidigi.screens.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import tappidigi.composeapp.generated.resources.Res
import tappidigi.composeapp.generated.resources.compose_multiplatform
import xt.qc.tappidigi.screens.chat.ChatScreen
import xt.qc.tappidigi.screens.communities.CommunitiesScreen
import xt.qc.tappidigi.screens.create.CreateScreen
import xt.qc.tappidigi.screens.home.HomeScreen
import xt.qc.tappidigi.screens.notification.NotificationScreen
import xt.qc.tappidigi.screens.profile.ProfileScreen
import xt.qc.tappidigi.screens.profile.ProfileViewModel
import xt.qc.tappidigi.utils.BottomNavigation

@Composable
fun MainScreen() {
    val navController: NavHostController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

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
                            restoreState = false
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
                composable(route = BottomNavigation.COMMUNITIES.name) {
                    CommunitiesScreen()
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
                    model = profile.uiState.collectAsState().value.photoUrl,
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