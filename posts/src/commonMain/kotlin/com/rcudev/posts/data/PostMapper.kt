package com.rcudev.posts.data

import com.rcudev.posts.data.model.EventResponse
import com.rcudev.posts.data.model.LaunchResponse
import com.rcudev.posts.data.model.PostResponse
import com.rcudev.posts.data.model.PostsResponse
import com.rcudev.posts.model.Event
import com.rcudev.posts.model.Launch
import com.rcudev.posts.model.Post
import com.rcudev.posts.model.PostType
import com.rcudev.posts.model.Posts
import kotlinx.datetime.*
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern

internal fun PostsResponse.toPosts(postType: PostType) = Posts(
    count = count,
    next = next,
    previous = previous,
    results = results.map { it.toPost(postType) }
)

private fun PostResponse.toPost(postType: PostType) = Post(
    id = id,
    postType = postType,
    title = title,
    url = url,
    imageUrl = imageUrl.replace("http:", "https:"), // Only for images to load on iOS devices
    newsSite = newsSite,
    summary = summary,
    publishedAt = publishedAt.asFormattedDate,
    updatedAt = updatedAt.asFormattedDate,
    featured = featured,
    launches = launches.map { it.toLaunch },
    events = events.map { it.toEvent }
)

private val LaunchResponse.toLaunch
    get() = Launch(
        launchId = launchId,
        provider = provider
    )

private val EventResponse.toEvent
    get() = Event(
        eventId = eventId,
        provider = provider
    )

@OptIn(FormatStringsInDatetimeFormats::class)
private val String.asFormattedDate: String
    get() {
        val instant = Instant.parse(this)
        return instant
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .format(LocalDateTime.Format { byUnicodePattern("yyyy/MM/dd - HH:mm") })
    }