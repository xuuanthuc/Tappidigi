package com.example.wibso.models

import kotlinx.datetime.Instant
import java.io.InputStreamReader

internal class SharedFileReader {
    fun loadJsonFile(fileName: String): String? {
        return javaClass.classLoader?.getResourceAsStream(fileName).use { stream ->
            InputStreamReader(stream).use { reader ->
                reader.readText()
            }
        }
    }
}

data class GalleryContent(
    val uri: String? = null,
    val name: String? = null,
    val album: String? = null,
    val duration: Int? = null,
    val size: Int? = null,
    val type: GalleryType? = null,
    val createdAt: Instant? = null,
)

enum class GalleryType {
    IMAGE, VIDEO
}