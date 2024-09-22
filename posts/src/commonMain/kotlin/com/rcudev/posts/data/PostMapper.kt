package com.rcudev.posts.data

import com.rcudev.posts.data.model.EventResponse
import com.rcudev.posts.data.model.LaunchResponse
import com.rcudev.posts.data.model.PostResponse
import com.rcudev.posts.data.model.PostsResponse
import com.rcudev.posts.model.Event
import com.rcudev.posts.model.Launch
import com.rcudev.posts.model.Post
import com.rcudev.posts.model.Posts

internal val PostsResponse.toPosts
    get() = Posts(
        count = count,
        next = next,
        previous = previous,
        results = results.map { it.toPost }
    )

private val PostResponse.toPost
    get() = Post(
        id = id,
        title = title,
        url = url,
        imageUrl = imageUrl.replace("http:", "https:"), // Only for images to load on iOS devices
        newsSite = newsSite,
        summary = summary,
        publishedAt = publishedAt,
        updatedAt = updatedAt,
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