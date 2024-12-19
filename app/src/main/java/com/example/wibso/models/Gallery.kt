package com.example.wibso.models

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

data class GalleryVideo(
    val uri: String? = null,
    val name: String? = null,
    val duration: Int? = null,
    val size: Int? = null,
)

data class GalleryImage(
    val uri: String? = null,
    val name: String? = null,
)