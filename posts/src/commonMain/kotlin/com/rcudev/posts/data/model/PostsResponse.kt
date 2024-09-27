package com.rcudev.posts.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PostsResponse(
    val count: Int,
    val next: String? = null,
    val previous: String? = null,
    val results: List<PostResponse> = emptyList()
)

@Serializable
internal data class PostResponse(
    val id: Int,
    val title: String,
    val url: String,
    @SerialName("image_url")
    val imageUrl: String,
    @SerialName("news_site")
    val newsSite: String,
    val summary: String,
    @SerialName("published_at")
    val publishedAt: String,
    @SerialName("updated_at")
    val updatedAt: String,
    val featured: Boolean? = false,
    val launches: List<LaunchResponse> = emptyList(),
    val events: List<EventResponse> = emptyList()
)

@Serializable
internal data class LaunchResponse(
    @SerialName("launch_id")
    val launchId: String,
    val provider: String,
)

@Serializable
internal data class EventResponse(
    @SerialName("event_id")
    val eventId: Int,
    val provider: String,
)