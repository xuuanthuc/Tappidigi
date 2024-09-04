package xt.qc.tappidigi.screens.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import tappidigi.composeapp.generated.resources.Res
import tappidigi.composeapp.generated.resources.search
import xt.qc.tappidigi.AppViewModel
import xt.qc.tappidigi.models.Chat
import xt.qc.tappidigi.screens.profile.ProfileViewModel

@Composable
fun ChatScreen(chat: Chat) {
    val profile = koinInject<ProfileViewModel>()
    val chatViewModel: ChatViewModel = viewModel { ChatViewModel(chat.users, profile.userState.value!!) }
    val appViewModel: AppViewModel = koinInject<AppViewModel>()
    val contentController: MutableState<TextFieldValue> = remember {
        mutableStateOf(
            TextFieldValue()
        )
    }
    Column {
        Text(chat.users[0].displayName ?: "")
        Text(chat.users[1].displayName ?: "")
        Row {
            TextField(
                value = contentController.value,
                onValueChange = { contentController.value = it },
                label = { Text(stringResource(Res.string.search)) },
            )
            Button(onClick = {

            }) {
                Text("Send")
            }
        }

    }
}