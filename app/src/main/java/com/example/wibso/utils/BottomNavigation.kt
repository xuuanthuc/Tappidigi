package com.example.wibso.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import xt.qc.tappidigi.R

enum class BottomNavigation(val title: Int, val icon: ImageVector) {
    HOME(R.string.home, Icons.Filled.Home),
    SEARCH(R.string.search, Icons.Filled.Search),
    CREATE(R.string.create, Icons.Filled.Add),
    MESSAGE(R.string.messages, Icons.Filled.Email),
//    NOTIFICATION(Res.string.notification, Icons.Filled.Notifications),
    PROFILE(R.string.profile , Icons.Filled.Person),
}

enum class ChatNavigation {
    MESSAGE,
    CAMERA
}