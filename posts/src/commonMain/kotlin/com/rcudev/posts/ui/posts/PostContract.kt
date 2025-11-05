package com.rcudev.posts.ui.posts

import com.rcudev.posts.domain.model.Post

// Immutable screen state
sealed interface PostState {
    data object Loading : PostState
    data object Error : PostState
    data class Empty(val message: String) : PostState
    data class Content(
        val posts: List<Post>,
        val loadingNextPage: Boolean = false,
        val showLoadPageError: Boolean = false
    ) : PostState
}

// User events
sealed interface PostEvent {
    data object LoadNextPage : PostEvent
    data object Retry : PostEvent
    data object DismissLoadPageError : PostEvent
}

// Side effects
sealed interface PostEffect {
    data class ShowError(val message: String) : PostEffect
}
