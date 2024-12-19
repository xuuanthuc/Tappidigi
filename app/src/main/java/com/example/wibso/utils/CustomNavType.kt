package com.example.wibso.utils

import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.example.wibso.models.User

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

    val UserType = object : NavType<User>(
        isNullableAllowed = false
    ) {
        override fun get(bundle: Bundle, key: String): User? {
            return Json.decodeFromString(bundle.getString(key) ?: return null)
        }

        override fun parseValue(value: String): User {
            return Json.decodeFromString(value)
        }

        override fun serializeAsValue(value: User): String {
            return Json.encodeToString(value)
        }

        override fun put(bundle: Bundle, key: String, value: User) {
            bundle.putString(key, Json.encodeToString(value))
        }
    }
}