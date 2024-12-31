package com.example.wibso.screens.chat.widgets

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.wibso.screens.chat.CameraState
import com.example.wibso.screens.chat.ChatViewModel
import xt.qc.tappidigi.R
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
        Spacer(modifier = Modifier.height(30.dp))
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
        Spacer(modifier = Modifier.height(30.dp))
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
                            .width(45.dp)
                            .height(45.dp)
                            .background(
                                Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(100.dp)
                            )
                            .padding(8.dp)
                            .pointerInput(Unit) {
                                detectTapGestures(onTap = {
                                    chatViewModel.cameraState.value = CameraState.HIDE
                                })
                            },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.close),
                            contentDescription = "",
                            tint = Color.White,
                        )
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
                            .background(Color.White, shape = RoundedCornerShape(100.dp)).pointerInput(Unit) {
                                detectTapGestures(onTap = {
                                    cameraController.takePicture(
                                        ContextCompat.getMainExecutor(context),
                                        object: ImageCapture.OnImageCapturedCallback() {
                                            override fun onCaptureSuccess(image: ImageProxy) {
                                                super.onCaptureSuccess(image)


                                            }
                                        }
                                    )
                                })
                            },
                    ) {

                    }
                    Box(modifier = Modifier
                        .width(45.dp)
                        .height(45.dp)
                        .background(
                            Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(100.dp)
                        )
                        .padding(8.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                cameraController.cameraSelector =
                                    if (cameraController.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                                        CameraSelector.DEFAULT_FRONT_CAMERA
                                    } else {
                                        CameraSelector.DEFAULT_BACK_CAMERA
                                    }
                            })
                        }) {
                        Icon(
                            painter = painterResource(R.drawable.rotate),
                            contentDescription = "",
                            tint = Color.White,
                        )
                    }
                }

            }
        }

    }
}