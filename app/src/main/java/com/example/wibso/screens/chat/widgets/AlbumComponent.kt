package com.example.wibso.screens.chat.widgets

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.video.VideoFrameDecoder
import coil3.video.videoFrameMillis
import com.example.wibso.models.GalleryType
import com.example.wibso.screens.chat.ActionToolsViewModel
import com.example.wibso.screens.chat.AlbumState
import com.example.wibso.screens.chat.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumComponent(chatViewModel: ChatViewModel, toolsViewModel: ActionToolsViewModel) {
    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current

    val maxWidth = LocalConfiguration.current.screenWidthDp.dp

    val galleryContents = toolsViewModel.data.collectAsState().value

    val gridStates = remember {
        LazyGridState()
    }

    ModalBottomSheet(
        onDismissRequest = {
            chatViewModel.albumState.value = AlbumState.HIDE
        },
        sheetState = sheetState,
        scrimColor = Color.Transparent,
        containerColor = Color.White,
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

                val model = ImageRequest.Builder(context).data(content.uri).videoFrameMillis(10000)
                    .decoderFactory { result, options, _ ->
                        VideoFrameDecoder(
                            result.source, options
                        )
                    }.build()

                Box(contentAlignment = Alignment.BottomEnd) {
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