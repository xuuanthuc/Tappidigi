package xt.qc.tappidigi.screens.profile

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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import org.koin.compose.koinInject
import xt.qc.tappidigi.AppViewModel
import xt.qc.tappidigi.utils.ScreenNavigation

@Composable
fun ProfileScreen() {
    val profile = koinInject<ProfileViewModel>()
    val appViewModel = koinInject<AppViewModel>()

    Column(modifier = Modifier.padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = profile.uiState.collectAsState().value?.photoUrl,
                contentDescription = null,
                modifier = Modifier.size(80.dp).clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    profile.uiState.value?.displayName ?: "",
                    style = TextStyle(
                        fontWeight = FontWeight.W700,
                        fontSize = 16.sp,
                    )
                )
                Text(profile.uiState.value?.username ?: "")
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    appViewModel.navHostController.navigate(ScreenNavigation.EDIT_PROFILE.name)
                },
                content = {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                    )
                    Text("edit profile")
                }
            )
        }
    }
}