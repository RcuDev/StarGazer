package com.rcudev.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.path
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val BASE_URL = "https://api.spaceflightnewsapi.net/"

abstract class KtorApi {
    val client = HttpClient {
        install(HttpCache)
        install(HttpTimeout) {
            requestTimeoutMillis = 10000L
            connectTimeoutMillis = 5000L
            socketTimeoutMillis = 5000L
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
            filter { request ->
                request.url.host.contains("api.spaceflightnewsapi.net")
            }
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                useAlternativeNames = false
            })
        }
        install(HttpCache)
    }

    fun HttpRequestBuilder.pathUrl(path: String) {
        url {
            takeFrom(BASE_URL)
            path("v4/$path")
        }
    }
}