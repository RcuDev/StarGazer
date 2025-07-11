package com.rcudev.posts.domain.model

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Stable
@Serializable
data class Posts(
    val count: Int,
    val next: String? = null,
    val previous: String? = null,
    val results: List<Post> = emptyList()
)

@Stable
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
) {
    // Lazy computed property para optimizar el render de UI
    val displaySummary: String by lazy { summary.take(200) + if (summary.length > 200) "..." else "" }
}

@Stable
@Serializable
data class Launch(
    val launchId: String,
    val provider: String,
)

@Stable
@Serializable
data class Event(
    val eventId: Int,
    val provider: String,
)