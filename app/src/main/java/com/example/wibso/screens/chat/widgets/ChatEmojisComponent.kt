package com.example.wibso.screens.chat.widgets

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wibso.screens.chat.ActionToolState
import xt.qc.tappidigi.R
import kotlinx.coroutines.launch
import com.example.wibso.screens.chat.ChatViewModel
import com.example.wibso.utils.removeLastChar

@Composable
fun ChatEmojisComponent(
    chatViewModel: ChatViewModel, contentController: MutableState<TextFieldValue>
) {
    val groups = chatViewModel.groupEmoji.collectAsState().value
    val scope = rememberCoroutineScope()

    val animatedSize by animateDpAsState(
        targetValue = if (chatViewModel.actionState.value == ActionToolState.EMOJI) 300.dp else 0.dp,
        animationSpec = tween(
            durationMillis = 0, easing = LinearEasing
        ), label = ""
    )

    Column(
        modifier = Modifier
            .background(Color.White)
            .heightIn(0.dp, animatedSize)
            .padding(8.dp)
    ) {
        if (groups == null) {
            if (chatViewModel.actionState.value == ActionToolState.EMOJI) Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        } else {
            val pageState = rememberPagerState { groups.size }
            val gridStates = remember {
                List(groups.size) { LazyGridState() }
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.BottomEnd) {
                HorizontalPager(
                    state = pageState, modifier = Modifier.fillMaxSize()
                ) { page ->
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 40.dp), // Number of columns
                        modifier = Modifier.fillMaxHeight(), state = gridStates[page]
                    ) {
                        items(groups.values.elementAt(page).size) { index ->
                            val emoji = groups.values.elementAt(page)[index]
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .pointerInput(emoji) {
                                        detectTapGestures(onTap = {
                                            val newValue =
                                                contentController.value.text + emoji.character
                                            contentController.value = TextFieldValue(
                                                newValue, selection = TextRange(newValue.length)
                                            )
                                        })
                                    }, contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    emoji.character, style = TextStyle(
                                        fontSize = 26.sp
                                    )
                                )
                            }
                        }
                    }
                }
                Button(
                    onClick = {
                        val newValue = contentController.value.text.removeLastChar()
                        contentController.value = TextFieldValue(
                            newValue, selection = TextRange(newValue.length)
                        )
                    },
                    modifier = Modifier.size(40.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonColors(
                        containerColor = chatViewModel.theme.value.sendButtonColor.copy(alpha = 0.8f),
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.delete),
                        contentDescription = ""
                    )
                }
            }
            if (chatViewModel.actionState.value == ActionToolState.EMOJI) LazyRow {
                items(groups.size) { index ->
                    Text(
                        groups.keys.elementAt(index),
                        style = TextStyle(
                            fontSize = 20.sp,
                        ),
                        modifier = Modifier
                            .padding(horizontal = 5.dp)
                            .pointerInput(groups.keys.elementAt(index)) {
                                detectTapGestures(
                                    onTap = {
                                        scope.launch {
                                            pageState.animateScrollToPage(index)
                                        }
                                    },
                                )
                            },
                    )
                }
            }
        }
    }

}