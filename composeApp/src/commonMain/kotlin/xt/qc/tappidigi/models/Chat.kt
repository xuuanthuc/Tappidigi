package xt.qc.tappidigi.models

import androidx.core.bundle.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Chat(
    var users: List<User> = emptyList()
)