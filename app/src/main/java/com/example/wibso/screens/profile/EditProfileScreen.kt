package com.example.wibso.screens.profile

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
import org.koin.compose.koinInject
import com.example.wibso.AppViewModel

@Composable
fun EditProfileScreen() {
    val viewModel: ProfileViewModel = koinInject<ProfileViewModel>()
    val bioTextValue: MutableState<TextFieldValue> = remember { mutableStateOf(TextFieldValue()) }
    val appViewModel: AppViewModel = koinInject<AppViewModel>()

    Column {
        TextField(value = bioTextValue.value,
            onValueChange = { bioTextValue.value = it },
            label = { Text("Bio") })

        Button(onClick = {
            viewModel.updateProfile(bioTextValue.value.text)
        }, modifier = Modifier.height(50.dp), content = {
            Icon(
                Icons.Default.Edit,
                contentDescription = null,
            )
            Text("Save profile")
        })

        Button(onClick = {
            viewModel.logout(
                onSuccess = {
                    appViewModel.checkAuthentication(viewModel)
                },
            )
        }, modifier = Modifier.height(50.dp), content = {
            Icon(
                Icons.Default.Edit,
                contentDescription = null,
            )
            Text("Logout")
        })
    }
}