package com.rcudev.stargazer.common.domain.model

import kotlinx.serialization.Serializable

@Serializable
internal data class Posts(
    val count: Int,
    val next: String? = null,
    val previous: String? = null,
    val results: List<Post> = emptyList()
)

@Serializable
internal data class Post(
    val id: Int,
    val title: String,
    val url: String,
    val imageUrl: String,
    val newsSite: String,
    val summary: String,
    val publishedAt: String,
    val updatedAt: String,
    val featured: Boolean? = false,
    val launches: List<Launch> = emptyList(),
    val events: List<Event> = emptyList()
)

@Serializable
internal data class Launch(
    val launchId: String,
    val provider: String,
)

@Serializable
internal data class Event(
    val eventId: Int,
    val provider: String,
)