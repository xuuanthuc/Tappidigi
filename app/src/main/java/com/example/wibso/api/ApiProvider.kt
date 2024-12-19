package com.example.wibso.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.path
import kotlinx.serialization.json.Json

open class ApiProvider {
    val client = HttpClient(CIO) {
        install(Logging) {
            level = LogLevel.INFO
        }
        install(DefaultRequest) {
            url("https://emoji-api.com/") // Set the base URL
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 30000 // Example timeout setting
        }
    }

    suspend inline fun <reified T> request(
        method: HttpMethod,
        url: String? = null,
        params: List<Map<String, String>>? = null,
        body: Any? = null,
    ): T {
        try {
            val res = when (method) {
                HttpMethod.Get -> {
                    client.get {
                        url {
                            url?.let {
                                path(it)
                            }
                            params?.forEach { map ->
                                map.forEach { (key, value) ->
                                    parameters.append(key, value)
                                }
                            }
                        }
                    }
                }

                HttpMethod.Post -> {
                    client.post {
                        url(url)
                        contentType(ContentType.Application.Json)
                        setBody(body)
                    }
                }

                HttpMethod.Put -> {
                    client.put { }
                }

                HttpMethod.Delete -> {
                    client.delete {

                    }
                }

                else -> {
                    throw IllegalArgumentException("Unsupported HTTP method: $method")
                }
            }
            if (res.status.value in 200..299) {
                val l = res.bodyAsText()
                val withUnknownKeys = Json { ignoreUnknownKeys = true }
                return withUnknownKeys.decodeFromString<T>(l)
            } else {
                throw Exception(res.bodyAsText())
            }
        } catch (e: Exception) {
            throw e
        }
    }
}