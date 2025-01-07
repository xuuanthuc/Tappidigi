import android.media.MediaRecorder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wibso.screens.chat.ActionToolState
import com.example.wibso.screens.chat.ActionToolsViewModel
import xt.qc.tappidigi.R
import com.example.wibso.screens.chat.ChatViewModel
import com.example.wibso.utils.ColorsPalette
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MessageTextField(
    chatViewModel: ChatViewModel,
    contentController: MutableState<TextFieldValue>,
    onSend: (String) -> Unit,
) {
    var isLabelVisible by remember { mutableStateOf(true) }
    val maxChatLines = remember { mutableIntStateOf(1) }
    var isRecordingAudio by remember { mutableStateOf(false) }
    LaunchedEffect(contentController.value.text) {
        isLabelVisible = contentController.value.text.isEmpty()
    }
    LaunchedEffect(chatViewModel.actionState.value) {
        isRecordingAudio = chatViewModel.actionState.value == ActionToolState.AUDIO
    }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val toolbarIsShowing = remember { mutableStateOf(true) }
    val context = LocalContext.current
    val toolsViewModel = viewModel { ActionToolsViewModel() }

    val albumPermission =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.containsValue(true)) {
                CoroutineScope(Dispatchers.Main).launch {
                    toolsViewModel.fetchImagesFromGallery(context)
                    toolsViewModel.fetchVideosFromGallery(context)
                }
                chatViewModel.actionState.value = ActionToolState.GALLERY
            }
        }

    val audioPermission =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.containsValue(true)) {
                chatViewModel.actionState.value = ActionToolState.AUDIO
                isLabelVisible = false
                focusManager.clearFocus()
                toolsViewModel.recordAudio(context)
            }
        }

    val cameraPermission =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.containsValue(true)) {
                chatViewModel.actionState.value = ActionToolState.CAMERA
            }
        }

    LaunchedEffect(isFocused) {
        chatViewModel.isFocused.value = isFocused
        if (isFocused) {
            chatViewModel.actionState.value = ActionToolState.NONE
        }
        toolbarIsShowing.value = !isFocused
    }

    LaunchedEffect(toolbarIsShowing.value) {
        CoroutineScope(Dispatchers.Main).launch {
            if (toolbarIsShowing.value) {
                maxChatLines.intValue = 1
            } else {
                delay(300)
                maxChatLines.intValue = 5
            }
        }
    }

    val animationDuration = 500
    val sentMessageButtonSize by animateIntAsState(
        targetValue = if (isLabelVisible) 0 else 40,
        animationSpec = tween(durationMillis = animationDuration),
        label = ""
    )
    val sentMessageButtonMargin by animateIntAsState(
        targetValue = if (isLabelVisible) 0 else 8,
        animationSpec = tween(durationMillis = animationDuration),
        label = ""
    )

    val textFieldBackground by animateColorAsState(
        targetValue = if (isRecordingAudio) chatViewModel.theme.value.sendButtonColor else Color.White,
        animationSpec = tween(durationMillis = animationDuration),
        label = ""
    )

    Row(
        modifier = Modifier
            .background(ColorsPalette.softFern)
            .padding(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        AnimatedVisibility(
            visible = !toolbarIsShowing.value && !isRecordingAudio,
            modifier = Modifier.height(40.dp),
            enter = expandHorizontally(),
            exit = shrinkOut(shrinkTowards = Alignment.CenterEnd)
        ) {
            Button(
                onClick = {
                    toolbarIsShowing.value = true
                },
                modifier = Modifier.size(40.dp),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = chatViewModel.theme.value.ownerColor,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.Gray
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_left),
                    contentDescription = "",
                )
            }
        }

        AnimatedVisibility(
            visible = isRecordingAudio,
            modifier = Modifier.height(40.dp),
            enter = expandHorizontally(),
            exit = shrinkOut(shrinkTowards = Alignment.CenterEnd)
        ) {
            Button(
                onClick = {
                    chatViewModel.actionState.value = ActionToolState.NONE
                    isLabelVisible = contentController.value.text.isEmpty()
                    toolsViewModel.stopRecord()
                },
                modifier = Modifier.size(40.dp),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = chatViewModel.theme.value.ownerColor,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.Gray
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.delete), contentDescription = ""
                )
            }
        }

        AnimatedVisibility(
            visible = toolbarIsShowing.value && !isRecordingAudio,
            modifier = Modifier.height(40.dp),
            enter = expandHorizontally(),
            exit = slideOutHorizontally() + shrinkOut(
                shrinkTowards = Alignment.CenterEnd,
            )
        ) {
            Row {
                Button(
                    onClick = {
                        if (chatViewModel.actionState.value != ActionToolState.NONE) {
                            chatViewModel.actionState.value = ActionToolState.NONE
                        } else {
                            if (toolsViewModel.checkCameraPermission(
                                    context, cameraPermission
                                )
                            ) {
                                chatViewModel.actionState.value = ActionToolState.CAMERA
                            }
                        }
                    },
                    modifier = Modifier.size(40.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = chatViewModel.theme.value.ownerColor,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.camera), contentDescription = ""
                    )
                }
                Button(
                    onClick = {
                        if (chatViewModel.actionState.value != ActionToolState.NONE) {
                            chatViewModel.actionState.value = ActionToolState.NONE
                        } else {
                            if (toolsViewModel.checkAlbumPermission(context, albumPermission)) {
                                chatViewModel.actionState.value = ActionToolState.GALLERY
                            }
                        }
                    },
                    modifier = Modifier.size(40.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = chatViewModel.theme.value.ownerColor,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.album), contentDescription = ""
                    )
                }
                Button(
                    onClick = {
                        if (chatViewModel.actionState.value != ActionToolState.NONE) {
                            chatViewModel.actionState.value = ActionToolState.NONE
                        } else {
                            if (toolsViewModel.checkAudioPermission(context, audioPermission)) {
                                chatViewModel.actionState.value = ActionToolState.AUDIO
                                isLabelVisible = false
                                focusManager.clearFocus()
                                toolsViewModel.recordAudio(context)
                            }
                        }
                    },
                    modifier = Modifier.size(40.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = chatViewModel.theme.value.ownerColor,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.microphone), contentDescription = ""
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))
        BasicTextField(
            value = contentController.value,
            interactionSource = interactionSource,
            onValueChange = { newValue ->
                contentController.value = newValue
                toolbarIsShowing.value = false
            },
            enabled = chatViewModel.actionState.value != ActionToolState.AUDIO,
            maxLines = maxChatLines.intValue,
            modifier = Modifier
                .weight(1f)
                .height(IntrinsicSize.Min)
                .focusRequester(focusRequester),
            decorationBox = { innerTextField ->
                Box(
                    Modifier
                        .fillMaxHeight()
                        .background(
                            textFieldBackground,
                            shape = RoundedCornerShape(8.dp)
                        ), contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,

                        ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            if (chatViewModel.actionState.value != ActionToolState.AUDIO) {
                                innerTextField()
                            }
                        }
                        Button(
                            onClick = {
                                CoroutineScope(Dispatchers.Main).launch {
                                    if (chatViewModel.actionState.value != ActionToolState.NONE) {
                                        if (chatViewModel.actionState.value == ActionToolState.EMOJI) {
                                            focusRequester.requestFocus()
                                        }
                                        chatViewModel.actionState.value = ActionToolState.NONE
                                    } else {
                                        focusManager.clearFocus()
                                        delay(200)
                                        chatViewModel.actionState.value = ActionToolState.EMOJI
                                    }
                                }
                            },
                            modifier = Modifier.size(40.dp),
                            contentPadding = PaddingValues(0.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = chatViewModel.theme.value.sendButtonColor,
                                disabledContainerColor = Color.Gray,
                                disabledContentColor = Color.Gray
                            )
                        ) {
                            Icon(
                                painter = painterResource(
                                    when (chatViewModel.actionState.value) {
                                        ActionToolState.EMOJI -> {
                                            R.drawable.keyboard
                                        }

                                        else -> {
                                            R.drawable.emoji
                                        }
                                    }
                                ), contentDescription = ""
                            )
                        }
                    }
                    if (isLabelVisible) {
                        Text(
                            "Type a message...", style = TextStyle(
                                color = Color.Gray,
                                fontWeight = FontWeight.W300,
                            ), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    if (isRecordingAudio) {
                        Text(
                            "Recording", style = TextStyle(
                                color = Color.Gray,
                                fontWeight = FontWeight.W300,
                            ), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

            },
        )
        Spacer(modifier = Modifier.width(sentMessageButtonMargin.dp))
        AnimatedVisibility(
            visible = !isLabelVisible,
            enter = fadeIn(animationSpec = tween(durationMillis = animationDuration)),
            exit = fadeOut(animationSpec = tween(durationMillis = animationDuration))
        ) {
            Button(
                onClick = {
                    val message = contentController.value.text
                    contentController.value = TextFieldValue("")
                    onSend.invoke(message)
                },
                modifier = Modifier.size(sentMessageButtonSize.dp),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonColors(
                    containerColor = chatViewModel.theme.value.sendButtonColor,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.Gray
                )
            ) {
                Icon(painter = painterResource(R.drawable.send), contentDescription = "")
            }
        }
    }
}