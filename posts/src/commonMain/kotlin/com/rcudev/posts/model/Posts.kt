package com.rcudev.posts.model

import kotlinx.serialization.Serializable

@Serializable
data class Posts(
    val count: Int,
    val next: String? = null,
    val previous: String? = null,
    val results: List<Post> = emptyList()
)

@Serializable
data class Post(
    val id: Int,
    val postType: PostType,
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
data class Launch(
    val launchId: String,
    val provider: String,
)

@Serializable
data class Event(
    val eventId: Int,
    val provider: String,
)