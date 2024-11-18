import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import tappidigi.composeapp.generated.resources.Res
import tappidigi.composeapp.generated.resources.send
import xt.qc.tappidigi.models.Chat
import xt.qc.tappidigi.screens.chat.ChatViewModel

@Composable
fun MessageTextField(
    chatViewModel: ChatViewModel,
    contentController: MutableState<TextFieldValue>,
    private: Chat.PrivateChat?
) {
    var isLabelVisible by remember { mutableStateOf(true) }

    LaunchedEffect(contentController.value.text) {
        isLabelVisible = contentController.value.text.isEmpty()
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
        modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.weight(1f).fillMaxWidth().height(40.dp)) {
            BasicTextField(
                value = contentController.value,
                onValueChange = { newValue ->
                    contentController.value = newValue
                },
                modifier = Modifier.fillMaxSize(),
                decorationBox = { innerTextField ->
                    Box(
                        Modifier.fillMaxHeight().background(
                            color = Color.White, shape = RoundedCornerShape(8.dp)
                        ).padding(horizontal = 12.dp), contentAlignment = Alignment.CenterStart
                    ) {
                        innerTextField()
                        if (isLabelVisible) {
                            Text(
                                "Type a message...",
                                style = TextStyle(
                                    color = Color.Gray,
                                    fontWeight = FontWeight.W300,
                                ),
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
                    CoroutineScope(Dispatchers.Main).launch {
                        chatViewModel.sendMessage(
                            message, private?.sender?.uid ?: ""
                        )
                    }
                },
                modifier = Modifier.size(sentMessageButtonSize.dp),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(painter = painterResource(Res.drawable.send), contentDescription = "")
            }
        }
    }
}