package com.example.wibso.models

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class Post @OptIn(ExperimentalUuidApi::class) constructor(
    val id: String = Uuid.random().toString(),
    val title: String? = null,
    val description: String? = null,
    var media: Media? = null,
)

@Serializable
data class Media @OptIn(ExperimentalUuidApi::class) constructor(
    val type: GalleryType? = null,
    var url: String? = null,
)