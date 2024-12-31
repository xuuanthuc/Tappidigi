package com.example.wibso.screens.chat.widgets

import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.wibso.screens.chat.CameraState
import com.example.wibso.screens.chat.ChatViewModel
import kotlin.math.roundToInt

@Composable
fun CameraComponent(chatViewModel: ChatViewModel) {
    val context = LocalContext.current
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or CameraController.VIDEO_CAPTURE
            )
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    var offsetY by remember { mutableFloatStateOf(0f) }


    val animatedColor by animateColorAsState(
        targetValue = if (offsetY in -70f..70f) {
            Color.Black
        } else {
            Color.Transparent
        }, animationSpec = tween(500), label = "color"
    )

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .background(animatedColor)
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        0, offsetY.roundToInt()
                    )
                }
                .aspectRatio(3f / 4f)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectDragGestures(onDragStart = {}, onDrag = { change, dragAmount ->
                        change.consume()
                        offsetY += dragAmount.y

                    }, onDragEnd = {
                        if (offsetY in -350f..350f) {
                            offsetY = 0f
                        } else {
                            chatViewModel.cameraState.value = CameraState.HIDE
                        }
                    })
                },
        ) {
            AndroidView(
                factory = {
                    PreviewView(it).apply {
                        this.scaleType = PreviewView.ScaleType.FIT_CENTER
                        this.controller = cameraController
                        cameraController.bindToLifecycle(lifecycleOwner)
                    }
                },
                modifier = Modifier.matchParentSize(),
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
        ) {
            AnimatedVisibility(
                visible = offsetY in -70f..70f, enter = fadeIn(), exit = fadeOut()
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(50.dp)
                            .background(Color.White, shape = RoundedCornerShape(100.dp)),
                    ) {

                    }
                    Box(
                        modifier = Modifier
                            .width(66.dp)
                            .height(66.dp)
                            .border(
                                width = 3.dp,
                                color = Color.White,
                                shape = RoundedCornerShape(100.dp)
                            )
                            .padding(6.dp)
                            .background(Color.White, shape = RoundedCornerShape(100.dp)),
                    ) {

                    }
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(50.dp)
                            .background(Color.White, shape = RoundedCornerShape(100.dp)),
                    ) {

                    }
                }

            }
        }

    }
}