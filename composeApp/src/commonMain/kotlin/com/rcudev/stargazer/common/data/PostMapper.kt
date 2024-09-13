package com.rcudev.stargazer.common.data

import com.rcudev.stargazer.common.data.model.PostResponse
import com.rcudev.stargazer.common.data.model.PostsResponse
import com.rcudev.stargazer.common.data.model.EventResponse
import com.rcudev.stargazer.common.data.model.LaunchResponse
import com.rcudev.stargazer.common.domain.model.Post
import com.rcudev.stargazer.common.domain.model.Posts
import com.rcudev.stargazer.common.domain.model.Event
import com.rcudev.stargazer.common.domain.model.Launch

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