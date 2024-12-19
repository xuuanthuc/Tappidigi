package com.example.wibso.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.koin.compose.koinInject
import com.example.wibso.AppViewModel
import com.example.wibso.models.Chat
import com.example.wibso.models.User
import com.example.wibso.utils.ScreenNavigation

@Composable
fun PreviewUserProfile(user: User) {
    val appViewModel: AppViewModel = koinInject<AppViewModel>()
    val profile = koinInject<ProfileViewModel>()

    Column(modifier = Modifier.padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = user.photoUrl,
                contentDescription = null,
                modifier = Modifier.size(80.dp).clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    user.displayName ?: "", style = TextStyle(
                        fontWeight = FontWeight.W700,
                        fontSize = 16.sp,
                    )
                )
                Text(user.username ?: "")
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = {
                val currentUser = profile.userState.value
                if(currentUser == null || currentUser.uid == user.uid) return@Button
                val chat = Chat.PrivateChat(sender = currentUser, receiver = user)
                appViewModel.navHostController.navigate(chat)
            }, content = {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                )
                Text("Chat")
            })
        }
    }
}