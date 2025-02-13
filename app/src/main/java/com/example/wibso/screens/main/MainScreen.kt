package com.example.wibso.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import org.koin.compose.koinInject
import com.example.wibso.screens.chat.MessagesScreen
import com.example.wibso.screens.create.CreateScreen
import com.example.wibso.screens.home.HomeScreen
import com.example.wibso.screens.profile.ProfileScreen
import com.example.wibso.screens.profile.ProfileViewModel
import com.example.wibso.screens.search.SearchScreen
import com.example.wibso.utils.BottomNavigation

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
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
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
                modifier = Modifier.padding(it)
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
                    MessagesScreen()
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
    Column {
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
        Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
    }
}