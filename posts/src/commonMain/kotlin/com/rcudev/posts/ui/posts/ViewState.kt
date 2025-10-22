package com.rcudev.posts.ui.posts

import com.rcudev.posts.domain.model.Post
import com.rcudev.posts.domain.model.PostType

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

data class PostState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = true,
    val loadingNextPage: Boolean = false,
    val error: String? = null,
    val postTypeSelected: PostType = PostType.ARTICLES,
    val newsSites: List<String> = emptyList(),
    val newsSiteSelected: String = "",
    val hasMorePages: Boolean = true
)