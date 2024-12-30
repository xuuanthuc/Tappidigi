package com.example.wibso.screens.chat.widgets

import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.wibso.screens.chat.AlbumState
import com.example.wibso.screens.chat.CameraState
import com.example.wibso.screens.chat.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraComponent(chatViewModel: ChatViewModel) {
    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or CameraController.VIDEO_CAPTURE
            )
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    ModalBottomSheet(
        onDismissRequest = {
            chatViewModel.cameraState.value = CameraState.HIDE
        },
        sheetState = sheetState,
        scrimColor = Color.Transparent,
        containerColor = Color.White,
    ) {
        AndroidView(factory = {
            PreviewView(it).apply {

                this.controller = cameraController
                cameraController.bindToLifecycle(lifecycleOwner)
            }
        }, modifier = Modifier.fillMaxSize())
    }

}