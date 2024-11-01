package com.rcudev.posts.ui

import com.rcudev.posts.domain.model.Post

sealed class ViewState {
    data object Loading : ViewState()
    data object Error : ViewState()
    data class Empty(val message: String) : ViewState()
    data class Success(
        val posts: List<Post>,
        val loadingNextPage: Boolean = false,
        val loadingError: Boolean = false,
    ) : ViewState()
}