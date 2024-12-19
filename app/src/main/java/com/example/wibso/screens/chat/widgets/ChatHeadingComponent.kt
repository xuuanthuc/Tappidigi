package com.example.wibso.screens.chat.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.koin.compose.koinInject
import com.example.wibso.AppViewModel
import xt.qc.tappidigi.R
import com.example.wibso.models.Chat
import com.example.wibso.screens.chat.ChatViewModel

@Composable
fun ChatHeadingComponent(chatViewModel: ChatViewModel, private: Chat.PrivateChat?) {
    val appViewModel: AppViewModel = koinInject<AppViewModel>()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().background(chatViewModel.theme.value.informationColor)
            .padding(horizontal = 4.dp, vertical = 8.dp)
    ) {
        Button(
            onClick = {
                appViewModel.navHostController.popBackStack()
            },
            modifier = Modifier.size(40.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Black,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = Color.Transparent
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_right),
                contentDescription = "",
                tint = Color.White,
            )
        }
        Spacer(Modifier.width(4.dp))
        AsyncImage(
            model = private?.receiver?.photoUrl,
            contentDescription = null,
            modifier = Modifier.size(40.dp).clip(CircleShape)
        )
        Text(
            private?.receiver?.displayName ?: "", style = TextStyle(
                color = Color.White,
                fontWeight = FontWeight.W600,
            ), modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}