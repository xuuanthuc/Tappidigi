package com.example.wibso.models

import com.google.firebase.Timestamp
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.encodeToMap
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class Post @OptIn(ExperimentalUuidApi::class) constructor(
    val id: String = Uuid.random().toString(),
    val title: String? = null,
    val description: String? = null,
    var medias: ArrayList<Media>? = null,
    val createdAt: Long = Timestamp.now().seconds,
    val updatedAt: Long = Timestamp.now().seconds,
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "title" to title,
            "description" to description,
            "medias" to medias?.map { it.toMap() },
            "createdAt" to createdAt,
            "updatedAt" to updatedAt,
        )
    }
}

@Serializable
data class Media @OptIn(ExperimentalUuidApi::class) constructor(
    val type: String? = null,
    var url: String? = null,
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "type" to type,
            "url" to url,
        )
    }
}