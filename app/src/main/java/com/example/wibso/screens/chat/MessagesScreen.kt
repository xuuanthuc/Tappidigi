package com.example.wibso.screens.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import org.koin.compose.koinInject
import com.example.wibso.AppViewModel
import com.example.wibso.models.Chat
import com.example.wibso.screens.profile.ProfileViewModel
import xt.qc.tappidigi.R

@Composable
fun MessagesScreen() {
    val messagesViewModel: MessagesViewModel = viewModel { MessagesViewModel() }
    val profile = koinInject<ProfileViewModel>()
    val contentController: MutableState<TextFieldValue> = remember {
        mutableStateOf(
            TextFieldValue()
        )
    }
    val appViewModel = koinInject<AppViewModel>()
    val groups = messagesViewModel.groupChats.collectAsState().value

    LaunchedEffect(Unit) {
        profile.userState.value?.let {
            messagesViewModel.getMyMessages(it)
        }
    }

    Column {
        TextField(
            value = contentController.value,
            onValueChange = { contentController.value = it },
            label = { Text(stringResource(R.string.search)) },
        )
        LazyColumn(modifier = Modifier.fillMaxHeight()) {
            items(
                count = groups.size,
                itemContent = { index ->
                    val user = groups[index]
                    Row {
                        AsyncImage(
                            model = user.first().photoUrl,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp).clip(CircleShape)
                        )
                        Text(
                            text = user.joinToString(separator = ", ") {
                                it.displayName ?: ""
                            },
                            modifier = Modifier.height(58.dp).clickable {
                                val currentUser = profile.userState.value
                                currentUser?.let {
                                    val chat = Chat.PrivateChat(
                                        sender = currentUser, receiver = user.first()
                                    )
                                    appViewModel.navHostController.navigate(chat)
                                }
                            },
                        )
                    }
                },
            )
        }
    }
}