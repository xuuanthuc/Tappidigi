package xt.qc.tappidigi.screens.chat.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import xt.qc.tappidigi.screens.chat.ChatViewModel

@Composable
fun ChatEmojisComponent(chatViewModel: ChatViewModel) {
    val groups = chatViewModel.groupEmoji.collectAsState().value
    val state: LazyGridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()

    if (groups == null) return
    Column(modifier = Modifier.background(Color.White).padding(8.dp)) {
        LazyHorizontalGrid(
            rows = GridCells.Fixed(5), // Number of columns
            modifier = Modifier.height(200.dp), state = state
        ) {
            groups.forEach { (group, emojis) ->
                item(span = { GridItemSpan(maxLineSpan) }) {}
                items(emojis.size) { index ->
                    Box(
                        modifier = Modifier.size(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            emojis[index].character,
                            style = TextStyle(
                                fontSize = 26.sp
                            )
                        )
                    }
                }
            }
        }
        LazyRow {
            items(groups.size) { index ->
                Text(groups.keys.elementAt(index),
                    style = TextStyle(
                        fontSize = 20.sp,
                    ),
                    modifier = Modifier.padding(horizontal = 5.dp)
                        .pointerInput(groups.keys.elementAt(index)) {
                            detectTapGestures(onTap = {
                                scope.launch {
                                    var i = 0
                                    for (id in 0..<index) {
                                        i += (groups.values.elementAtOrNull(id)?.size ?: 0)
                                    }
                                    state.animateScrollToItem(i + index)
                                }
                            })
                        })
            }
        }
    }
}