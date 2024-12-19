package com.example.wibso.api

import io.ktor.http.HttpMethod
import com.example.wibso.models.Emoji

class Usecase: ApiProvider() {
    suspend fun getEmojis(): List<Emoji>{
         return request<List<Emoji>>(
            method = HttpMethod.Get,
            url = "emojis",
            params = listOf(
                mapOf("access_key" to "3c7bfc681c291fc0749cc7d7c9bd1f346b2c4082")
            ),
        )
    }
}