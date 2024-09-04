package xt.qc.tappidigi.utils

import androidx.core.bundle.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import xt.qc.tappidigi.models.User

object CustomNavType {
    val UserListType = object : NavType<List<User>>(
        isNullableAllowed = false
    ) {
        override fun get(bundle: Bundle, key: String): List<User>? {
            return Json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun parseValue(value: String): List<User> {
            return Json.decodeFromString(value)
        }

        override fun serializeAsValue(value: List<User>): String {
            return Json.encodeToString(value)
        }

        override fun put(bundle: Bundle, key: String, value: List<User>) {
            bundle.putString(key, Json.encodeToString(value))
        }
    }
}