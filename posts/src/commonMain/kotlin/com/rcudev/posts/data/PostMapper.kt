package com.rcudev.posts.data

import com.rcudev.posts.data.model.EventResponse
import com.rcudev.posts.data.model.LaunchResponse
import com.rcudev.posts.data.model.PostResponse
import com.rcudev.posts.data.model.PostsResponse
import com.rcudev.posts.domain.model.Event
import com.rcudev.posts.domain.model.Launch
import com.rcudev.posts.domain.model.Post
import com.rcudev.posts.domain.model.PostType
import com.rcudev.posts.domain.model.Posts

internal fun PostsResponse.toPosts(postType: PostType): Posts = Posts(
    count = count,
    next = next,
    previous = previous,
    results = results.map { it.toPost(postType) }
)

private fun PostResponse.toPost(postType: PostType): Post = Post(
    id = id,
    postType = postType,
    title = title,
    url = url,
    imageUrl = imageUrl.secureImageUrl(),
    newsSite = newsSite,
    summary = summary,
    publishedAt = publishedAt.asFormattedDate(),
    updatedAt = updatedAt.asFormattedDate(),
    featured = featured,
    launches = launches.map { it.toLaunch() },
    events = events.map { it.toEvent() }
)

private fun LaunchResponse.toLaunch(): Launch = Launch(
    launchId = launchId,
    provider = provider
)

private fun EventResponse.toEvent(): Event = Event(
    eventId = eventId,
    provider = provider
)

// Optimized string operations
private fun String.secureImageUrl(): String = 
    if (startsWith("http:")) replace("http:", "https:") else this

// Simplified date formatting to avoid experimental APIs
private fun String.asFormattedDate(): String = runCatching {
    // Just return the original string with basic formatting
    // Since the original date is already in ISO format, we'll keep it simple
    this.replace("T", " ").replace("Z", "").take(16)
}.getOrElse { this }