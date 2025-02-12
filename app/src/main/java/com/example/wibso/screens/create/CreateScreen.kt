package com.example.wibso.screens.create

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.video.VideoFrameDecoder
import coil3.video.videoFrameMillis
import com.example.wibso.models.GalleryContent
import com.example.wibso.models.GalleryType
import com.example.wibso.screens.chat.ActionToolState
import com.example.wibso.screens.chat.ActionToolsViewModel
import com.example.wibso.screens.chat.widgets.GalleryComponent
import com.example.wibso.screens.chat.widgets.secToTime
import com.example.wibso.screens.profile.ProfileViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import xt.qc.tappidigi.R

@Composable
fun CreateScreen() {
    val titleTextController: MutableState<TextFieldValue> =
        remember { mutableStateOf(TextFieldValue()) }
    val descriptionTextController: MutableState<TextFieldValue> =
        remember { mutableStateOf(TextFieldValue()) }
    val createViewModel: CreateViewModel = viewModel { CreateViewModel() }
    val profileViewModel = koinInject<ProfileViewModel>()
    val context = LocalContext.current
    val toolsViewModel = viewModel { ActionToolsViewModel() }
    val albumPermission =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.containsValue(true)) {
                CoroutineScope(Dispatchers.Main).launch {
                    toolsViewModel.fetchImagesFromGallery(context)
                    toolsViewModel.fetchVideosFromGallery(context)
                }
            }
        }
    val images: SnapshotStateList<GalleryContent> = remember { mutableStateListOf() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Box {
        LazyColumn(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            item {
                Text(
                    "Share with your friends",
                    modifier = Modifier.padding(bottom = 10.dp),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                    )
                )
            }
            item {
                OutlinedTextField(
                    value = titleTextController.value,
                    onValueChange = { titleTextController.value = it },
                    label = { Text("Title") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    maxLines = 1
                )
            }
            item {

                OutlinedTextField(
                    value = descriptionTextController.value,
                    onValueChange = { descriptionTextController.value = it },
                    label = { Text("Description") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .height(150.dp),
                    maxLines = 5
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .size(180.dp)
                        .background(
                            color = Color.LightGray, shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            if (createViewModel.actionState.value != ActionToolState.NONE) {
                                createViewModel.actionState.value = ActionToolState.NONE
                            } else {
                                if (toolsViewModel.checkAlbumPermission(context, albumPermission)) {
                                    createViewModel.actionState.value = ActionToolState.GALLERY
                                }
                            }

                        }, contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.album),
                            contentDescription = "",
                            modifier = Modifier.size(40.dp)

                        )
                        Text(
                            "Add Image",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }

            if (images.isNotEmpty()) item {
                LazyRow(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .height(128.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(images.size) { index ->
                        val content = images[index]
                        val model =
                            ImageRequest.Builder(context).data(content.uri)
                                .videoFrameMillis(10000)
                                .decoderFactory { result, options, _ ->
                                    VideoFrameDecoder(
                                        result.source, options
                                    )
                                }.build()
                        Box(
                            contentAlignment = Alignment.BottomEnd,
                            modifier = Modifier.pointerInput(content) {
                            }) {
                            AsyncImage(
                                model = if (content.type == GalleryType.IMAGE) Uri.parse(content.uri) else model,
                                contentDescription = null,
                                contentScale = ContentScale.Crop
                            )

                            if (content.type == GalleryType.VIDEO && content.duration != null) {
                                Box(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .background(
                                            Color.Black.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(2.dp)
                                        )
                                        .padding(2.dp)
                                ) {
                                    Text(
                                        (content.duration.div(1000)).secToTime(),
                                        style = TextStyle(
                                            color = Color.White, fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item {
                Button(
                    onClick = {
                        val title = titleTextController.value.text.trim()
                        val description = descriptionTextController.value.text.trim()

                        if (title.isBlank() || description.isBlank()) {
                            Toast.makeText(
                                context,
                                "Title and description cannot be empty",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            createViewModel.createPost(
                                title,
                                description,
                                profileViewModel.userState.value,
                                galleryContents = images
                            )
                            keyboardController?.hide()
                            titleTextController.value = TextFieldValue("")
                            descriptionTextController.value = TextFieldValue("")
                            images.clear()
                        }
                    },
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    content = {
                        Text("Save", style = MaterialTheme.typography.titleMedium)
                    },
                )
            }
        }
    }
    if (createViewModel.actionState.value == ActionToolState.GALLERY) {
        GalleryComponent(
            onDismissRequest = {
                createViewModel.actionState.value = ActionToolState.NONE
            },
            toolsViewModel = toolsViewModel,
            onPick = { pickedImages ->
                images.addAll(
                    pickedImages
                )
            },
        )
    }
}