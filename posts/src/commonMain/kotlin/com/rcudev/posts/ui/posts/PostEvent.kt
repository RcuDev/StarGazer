package com.rcudev.posts.ui.posts

import com.rcudev.posts.domain.model.PostType

sealed interface PostEvent {
    data object LoadPost : PostEvent
    data object LoadNExtPage : PostEvent
    data class SelectPostType(val type: PostType): PostEvent
    data class SelectNewsSite(val site: String): PostEvent
    data class OnPostClick(val url: String): PostEvent
}