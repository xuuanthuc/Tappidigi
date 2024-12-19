package com.example.wibso.models

import kotlinx.serialization.Serializable

@Serializable
data class Emoji(
    val slug: String,
    val character: String,
    val unicodeName: String,
    val codePoint: String,
    val group: String,
    val subGroup: String
)
