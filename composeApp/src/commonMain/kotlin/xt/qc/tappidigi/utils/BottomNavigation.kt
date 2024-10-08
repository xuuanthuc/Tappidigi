package xt.qc.tappidigi.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource
import tappidigi.composeapp.generated.resources.*

enum class BottomNavigation(val title: StringResource, val icon: ImageVector) {
    HOME(Res.string.home, Icons.Filled.Home),
    SEARCH(Res.string.search, Icons.Filled.Search),
    CREATE(Res.string.create, Icons.Filled.Add),
    MESSAGE(Res.string.messages, Icons.Filled.Email),
//    NOTIFICATION(Res.string.notification, Icons.Filled.Notifications),
    PROFILE(Res.string.profile , Icons.Filled.Person),
}