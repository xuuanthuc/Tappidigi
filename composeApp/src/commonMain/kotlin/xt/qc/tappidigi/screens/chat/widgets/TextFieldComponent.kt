import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import tappidigi.composeapp.generated.resources.Res
import tappidigi.composeapp.generated.resources.emoji
import tappidigi.composeapp.generated.resources.keyboard
import tappidigi.composeapp.generated.resources.send
import xt.qc.tappidigi.screens.chat.ChatViewModel
import xt.qc.tappidigi.screens.chat.EmojiState
import xt.qc.tappidigi.utils.ColorsPalette

@Composable
fun MessageTextField(
    chatViewModel: ChatViewModel,
    contentController: MutableState<TextFieldValue>,
    onSend: (String) -> Unit,
) {
    var isLabelVisible by remember { mutableStateOf(true) }

    LaunchedEffect(contentController.value.text) {
        isLabelVisible = contentController.value.text.isEmpty()
    }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(isFocused) {
        chatViewModel.isFocused.value = isFocused
        if (isFocused) {
            chatViewModel.emojiState.value = EmojiState.HIDE
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
        modifier = Modifier.background(ColorsPalette.softFern).padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.weight(1f).fillMaxWidth().height(40.dp)) {
            BasicTextField(
                value = contentController.value,
                interactionSource = interactionSource,
                onValueChange = { newValue ->
                    contentController.value = newValue
                },
                modifier = Modifier.fillMaxSize().focusRequester(focusRequester),
                decorationBox = { innerTextField ->
                    Box(
                        Modifier.fillMaxHeight().background(
                            color = Color.White, shape = RoundedCornerShape(8.dp)
                        ).padding(4.dp), contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,

                            ) {
                            Box(
                                modifier = Modifier.weight(1f)
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
                                                Res.drawable.keyboard
                                            }

                                            EmojiState.HIDE -> {
                                                Res.drawable.emoji
                                            }
                                        }
                                    ),
                                    contentDescription = ""
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
        }
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
                Icon(painter = painterResource(Res.drawable.send), contentDescription = "")
            }
        }
    }
}