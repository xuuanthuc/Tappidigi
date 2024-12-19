package com.example.wibso.screens.chat.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import com.example.wibso.screens.chat.AlbumState
import com.example.wibso.screens.chat.ChatViewModel
import com.example.wibso.utils.Platform

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumComponent(chatViewModel: ChatViewModel) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val platform = koinInject<Platform>()

    LaunchedEffect(Unit) {
        if (platform.checkImagePermission()) {
            platform.fetchImagesFromGallery()
        }
        if (platform.checkVideoPermission()) {
            platform.fetchVideosFromGallery()
        }
    }
    val maxHeight = platform.screenHeight()


    ModalBottomSheet(
        onDismissRequest = {
            chatViewModel.albumState.value = AlbumState.HIDE
        },
        sheetState = sheetState,
        scrimColor = Color.Transparent,
        modifier = Modifier.heightIn(min = 300.dp, max = maxHeight)
    ) {
        Column {
            Box(modifier = Modifier.weight(1f))
            // Sheet content
            Button(onClick = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        chatViewModel.albumState.value = AlbumState.HIDE
                    }
                }
            }) {
                Text("Hide bottom sheet")
            }
        }

    }
}