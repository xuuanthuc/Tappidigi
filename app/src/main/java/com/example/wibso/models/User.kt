package com.example.wibso.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val uid: String? = null,
    val email: String? = null,
    val username: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val bio: String? = null,
)

