package xt.qc.tappidigi.utils

import coil3.Uri

internal actual class SharedFileReader actual constructor() {
    actual fun loadJsonFile(fileName: String): String? {
        TODO("Not yet implemented")
    }
}

actual data class GalleryVideo actual constructor(
    uri: Uri,
    name: String,
    duration: Int,
    size: Int
)

actual class GalleryImage {
    actual val uri: String?
        get() = TODO("Not yet implemented")
    actual val name: String?
        get() = TODO("Not yet implemented")
}