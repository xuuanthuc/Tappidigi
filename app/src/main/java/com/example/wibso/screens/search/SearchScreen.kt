package com.example.wibso.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.wibso.AppViewModel
import xt.qc.tappidigi.R
import org.koin.compose.koinInject

@Composable
fun SearchScreen() {
    val usernameTextController: MutableState<TextFieldValue> = remember {
        mutableStateOf(
            TextFieldValue()
        )
    }
    val viewModel: SeachViewModel = viewModel { SeachViewModel() }
    val appViewModel: AppViewModel = koinInject<AppViewModel>()
    val users = viewModel.searchedUsers.collectAsState().value

    Column {
        TextField(
            value = usernameTextController.value,
            onValueChange = { usernameTextController.value = it },
            label = { Text(stringResource(R.string.search)) },
        )

        Button(onClick = {
            viewModel.search(usernameTextController.value.text.lowercase())
        }, modifier = Modifier.height(50.dp), content = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
            )
            Text("Search profile")
        })
        LazyColumn {
            items(users.size) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable{
                    appViewModel.navHostController.navigate(users[it])
                }) {
                    AsyncImage(
                        model = users[it].photoUrl,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp).clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Column(verticalArrangement = Arrangement.Center) {
                        Text(
                            users[it].displayName ?: "",
                            style = TextStyle(fontWeight = FontWeight.W700, fontSize = 16.sp)
                        )
                        Text(users[it].username ?: "")
                    }
                }
            }
        }
    }
}