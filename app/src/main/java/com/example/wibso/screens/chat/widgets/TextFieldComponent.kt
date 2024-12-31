import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.navigation.NavHostController
import com.example.wibso.screens.chat.ActionToolsViewModel
import xt.qc.tappidigi.R
import com.example.wibso.screens.chat.AlbumState
import com.example.wibso.screens.chat.CameraState
import com.example.wibso.screens.chat.ChatViewModel
import com.example.wibso.screens.chat.EmojiState
import com.example.wibso.utils.ChatNavigation
import com.example.wibso.utils.ColorsPalette
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MessageTextField(
    chatViewModel: ChatViewModel,
    contentController: MutableState<TextFieldValue>,
    onSend: (String) -> Unit,
) {
    var isLabelVisible by remember { mutableStateOf(true) }
    val maxChatLines = remember { mutableStateOf(1) }

    LaunchedEffect(contentController.value.text) {
        isLabelVisible = contentController.value.text.isEmpty()
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
                when (chatViewModel.albumState.value) {
                    AlbumState.SHOW -> {
                        chatViewModel.albumState.value = AlbumState.HIDE
                    }

                    AlbumState.HIDE -> {
                        chatViewModel.albumState.value = AlbumState.SHOW
                    }
                }
            }
        }

    val cameraPermission =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.containsValue(true)) {
                when (chatViewModel.cameraState.value) {
                    CameraState.SHOW -> {
                        chatViewModel.cameraState.value = CameraState.HIDE
                    }

                    CameraState.HIDE -> {
                        chatViewModel.cameraState.value = CameraState.SHOW
                    }
                }
            }
        }

    LaunchedEffect(isFocused) {
        chatViewModel.isFocused.value = isFocused
        if (isFocused) {
            chatViewModel.emojiState.value = EmojiState.HIDE
        }
        toolbarIsShowing.value = !isFocused
    }

    LaunchedEffect(toolbarIsShowing.value) {
        CoroutineScope(Dispatchers.Main).launch {
            if (toolbarIsShowing.value) {
                maxChatLines.value = 1
            } else {
                delay(300)
                maxChatLines.value = 5
            }
        }
    }

    val animationDuration = 500
    val sentMessageButtonSize by animateIntAsState(
        targetValue = if (isLabelVisible) 0 else 40,
        animationSpec = tween(durationMillis = animationDuration)
    )
    val sentMessageButtonMargin by animateIntAsState(
        targetValue = if (isLabelVisible) 0 else 8,
        animationSpec = tween(durationMillis = animationDuration)
    )

    Row(
        modifier = Modifier
            .background(ColorsPalette.softFern)
            .padding(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        AnimatedVisibility(
            visible = !toolbarIsShowing.value,
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
            visible = toolbarIsShowing.value,
            modifier = Modifier.height(40.dp),
            enter = expandHorizontally(),
            exit = slideOutHorizontally() + shrinkOut(
                shrinkTowards = Alignment.CenterEnd,
            )
        ) {
            Row {
                Button(
                    onClick = {
                        when (chatViewModel.cameraState.value) {
                            CameraState.SHOW -> {
                                chatViewModel.cameraState.value = CameraState.HIDE
                            }

                            CameraState.HIDE -> {
                                if (toolsViewModel.checkCameraPermission(
                                        context, cameraPermission
                                    )
                                ) {
                                    chatViewModel.cameraState.value = CameraState.SHOW
                                }
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
                        when (chatViewModel.albumState.value) {
                            AlbumState.SHOW -> {
                                chatViewModel.albumState.value = AlbumState.HIDE
                            }

                            AlbumState.HIDE -> {
                                if (toolsViewModel.checkAlbumPermission(context, albumPermission)) {
                                    chatViewModel.albumState.value = AlbumState.SHOW
                                }
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
                        when (chatViewModel.albumState.value) {
                            AlbumState.SHOW -> {
                                chatViewModel.albumState.value = AlbumState.HIDE
                            }

                            AlbumState.HIDE -> {
                                chatViewModel.albumState.value = AlbumState.SHOW
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
            maxLines = maxChatLines.value,
            modifier = Modifier
                .weight(1f)
                .height(IntrinsicSize.Min)
                .focusRequester(focusRequester),
            decorationBox = { innerTextField ->
                Box(
                    Modifier
                        .fillMaxHeight()
                        .background(
                            color = Color.White, shape = RoundedCornerShape(8.dp)
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
                            innerTextField()
                        }
                        Button(
                            onClick = {
                                CoroutineScope(Dispatchers.Main).launch {
                                    when (chatViewModel.emojiState.value) {
                                        EmojiState.SHOW -> {
                                            focusRequester.requestFocus()
                                            chatViewModel.emojiState.value = EmojiState.HIDE
                                        }

                                        EmojiState.HIDE -> {
                                            focusManager.clearFocus()
                                            delay(200)
                                            chatViewModel.emojiState.value = EmojiState.SHOW
                                        }
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
                                    when (chatViewModel.emojiState.value) {
                                        EmojiState.SHOW -> {
                                            R.drawable.keyboard
                                        }

                                        EmojiState.HIDE -> {
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