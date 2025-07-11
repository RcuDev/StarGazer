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
    protected val client = HttpClient {
        install(HttpCache) {
            // Enable HTTP cache for better performance
        }
        
        install(HttpTimeout) {
            requestTimeoutMillis = 15_000L
            connectTimeoutMillis = 10_000L
            socketTimeoutMillis = 10_000L
        }
        
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.INFO
            filter { request ->
                request.url.host.contains("api.spaceflightnewsapi.net")
            }
        }
        
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                useAlternativeNames = false
                coerceInputValues = true
                encodeDefaults = true
                explicitNulls = false
            })
        }
    }

    protected fun HttpRequestBuilder.pathUrl(path: String) {
        url {
            takeFrom(BASE_URL)
            path("v4/$path")
        }
    }
}