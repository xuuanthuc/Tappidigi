package com.example.wibso.screens.chat.widgets

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.video.VideoFrameDecoder
import coil3.video.videoFrameMillis
import com.example.wibso.models.GalleryContent
import com.example.wibso.models.GalleryType
import com.example.wibso.screens.chat.ActionToolState
import com.example.wibso.screens.chat.ActionToolsViewModel
import com.example.wibso.screens.chat.ChatViewModel
import com.example.wibso.utils.ChatGalleryStyle
import com.example.wibso.utils.ItemGalleryStyle
import com.example.wibso.utils.PostGalleryStyle
import xt.qc.tappidigi.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumComponent(chatViewModel: ChatViewModel, toolsViewModel: ActionToolsViewModel) {
    val sheetState = rememberModalBottomSheetState()

    ///Ban đầu sử dụng MutableList<> nhưng khi item trong list được thêm vào hoặc xoá bớt đi thì các
    // item con không lắng nghe được sự thay đổi, sau khi sử dụng SnapshotStateList
    // thì giải quyết được vấn đề này để cập nhật lại index

    val selectionList: SnapshotStateList<GalleryContent> = remember { mutableStateListOf() }

    val galleryContents = toolsViewModel.data.collectAsState().value

    val gridStates = remember {
        LazyGridState()
    }
    ModalBottomSheet(
        onDismissRequest = {
            chatViewModel.actionState.value = ActionToolState.NONE
        },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        scrimColor = Color.Transparent,
        containerColor = Color.White,
        dragHandle = {
            Row(modifier = Modifier.height(30.dp)) {
                Box {
                    Text("Recent")
                }
            }

        },
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 80.dp),
            modifier = Modifier.fillMaxHeight(),
            state = gridStates,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            items(galleryContents.size) { index ->
                val content = galleryContents[index]
                ItemGallery(
                    content = content, style = ChatGalleryStyle(), selectionList = selectionList
                )
            }
        }
        if (selectionList.isNotEmpty()) {
            Popup(
                alignment = Alignment.BottomCenter
            ) {
                Button(onClick = {}) {
                    Text("Send")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryComponent(
    onDismissRequest: () -> Unit,
    toolsViewModel: ActionToolsViewModel,
    onPick: (SnapshotStateList<GalleryContent>) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ///Ban đầu sử dụng MutableList<> nhưng khi item trong list được thêm vào hoặc xoá bớt đi thì các
    // item con không lắng nghe được sự thay đổi, sau khi sử dụng SnapshotStateList
    // thì giải quyết được vấn đề này để cập nhật lại index

    val selectionList: SnapshotStateList<GalleryContent> = remember { mutableStateListOf() }

    val galleryContents = toolsViewModel.data.collectAsState().value

    val gridStates = remember {
        LazyGridState()
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        scrimColor = Color.Transparent,
        containerColor = Color.White,
        dragHandle = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Box {
                    Text("Recent")
                }
                Spacer(modifier = Modifier.weight(1f))
                Box(modifier = Modifier
                    .clickable {
                        onPick(selectionList)
                        onDismissRequest()
                    }
                    .padding(16.dp)) {
                    Icon(painter = painterResource(R.drawable.send), contentDescription = "")
                }
            }

        },
    ) {
        Column {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 80.dp),
                modifier = Modifier.weight(1f),
                state = gridStates,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                items(galleryContents.size) { index ->
                    val content = galleryContents[index]
                    ItemGallery(
                        content = content, style = PostGalleryStyle(), selectionList = selectionList
                    )
                }
            }
            Box {
                Text("send")
            }
        }
    }
}

@Composable
fun ItemGallery(
    content: GalleryContent, style: ItemGalleryStyle, selectionList: MutableList<GalleryContent>
) {
    val context = LocalContext.current
    val maxWidth = LocalConfiguration.current.screenWidthDp.dp

    val model = ImageRequest.Builder(context).data(content.uri).videoFrameMillis(10000)
        .decoderFactory { result, options, _ ->
            VideoFrameDecoder(
                result.source, options
            )
        }.build()
    val selectedIndex: MutableState<Int> = remember { mutableIntStateOf(0) }
    LaunchedEffect(selectionList.size) {
        selectedIndex.value = selectionList.indexOf(content) + 1
    }

    Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.pointerInput(content) {
        detectTapGestures(onTap = {
            if (!content.isSelected.value) {
                if (selectionList.size < 10) {
                    selectionList.add(content)
                    content.isSelected.value = true
                }
            } else {
                selectionList.remove(content)
                content.isSelected.value = false
            }
        })
    }) {
        AsyncImage(
            model = if (content.type == GalleryType.IMAGE) Uri.parse(content.uri) else model,
            contentDescription = null,
            modifier = Modifier.size(maxWidth / 4),
            contentScale = ContentScale.Crop
        )

        if (content.type == GalleryType.VIDEO && content.duration != null) {
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .background(
                        Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(2.dp)
                    )
                    .padding(2.dp)
            ) {
                Text(
                    (content.duration.div(1000)).secToTime(), style = TextStyle(
                        color = Color.White, fontSize = 10.sp
                    )
                )
            }
        }
        AnimatedVisibility(
            visible = content.isSelected.value, modifier = Modifier.matchParentSize()
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .border(width = 2.dp, color = style.color)
                    .background(Color.Black.copy(alpha = 0.3f)), contentAlignment = Alignment.TopEnd
            ) {
                Box(
                    Modifier
                        .width(20.dp)
                        .height(20.dp)
                        .background(
                            color = style.color, shape = RoundedCornerShape(2.dp)
                        ), contentAlignment = Alignment.Center
                ) {
                    if (selectedIndex.value != 0) Text(
                        selectedIndex.value.toString(), style = TextStyle(
                            color = Color.White, fontWeight = FontWeight.W500, fontSize = 10.sp
                        )
                    )
                }
            }
        }

    }
}

fun Int.secToTime(): String {
    val second = this % 60
    var minute = this / 60
    if (minute >= 60) {
        val hour = minute / 60
        minute %= 60
        return hour.toString() + ":" + (if (minute < 10) "0$minute" else minute) + ":" + (if (second < 10) "0$second" else second)
    }
    return minute.toString() + ":" + (if (second < 10) "0$second" else second)
}