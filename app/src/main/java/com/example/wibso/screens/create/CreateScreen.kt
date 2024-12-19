package com.example.wibso.screens.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.koin.compose.koinInject
import com.example.wibso.screens.profile.ProfileViewModel

@Composable
fun CreateScreen() {
    val contentTextController: MutableState<TextFieldValue> = remember { mutableStateOf(TextFieldValue()) }
    val createViewModel: CreateViewModel = viewModel { CreateViewModel() }
    val profileViewModel = koinInject<ProfileViewModel>()
    Column {
        TextField(value = contentTextController.value,
            onValueChange = { contentTextController.value = it },
            label = { Text("Content") })

        Button(onClick = {
            createViewModel.createPost(contentTextController.value.text, profileViewModel.userState.value)
        }, modifier = Modifier.height(50.dp), content = {
            Icon(
                Icons.Default.Edit,
                contentDescription = null,
            )
            Text("Save profile")
        })
    }
}