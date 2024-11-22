package xt.qc.tappidigi.utils

import java.io.InputStreamReader

internal actual class SharedFileReader {
    actual fun loadJsonFile(fileName: String): String? {
        return javaClass.classLoader?.getResourceAsStream(fileName).use { stream ->
            InputStreamReader(stream).use { reader ->
                reader.readText()
            }
        }
    }
}

actual data class GalleryVideo(
    actual val uri: String? = null,
    actual val name: String? = null,
    actual val duration: Int? = null,
    actual val size: Int? = null,
)
actual data class GalleryImage(
    actual val uri: String? = null,
    actual val name: String? = null,
)