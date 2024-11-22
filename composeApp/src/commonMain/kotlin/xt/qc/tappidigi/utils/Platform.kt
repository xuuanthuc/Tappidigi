package xt.qc.tappidigi.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

expect class Platform {
    val name: String

    fun fetchImagesFromGallery(): List<GalleryImage>

    fun fetchVideosFromGallery(): List<GalleryVideo>

    fun checkImagePermission(): Boolean
    fun checkVideoPermission(): Boolean

    @Composable
    fun screenHeight() : Dp
}

expect class GalleryVideo {
    val uri: String?
    val name: String?
    val duration: Int?
    val size: Int?
}

expect class GalleryImage {
    val uri: String?
    val name: String?
}

internal expect class SharedFileReader() {
    fun loadJsonFile(fileName: String): String?
}


