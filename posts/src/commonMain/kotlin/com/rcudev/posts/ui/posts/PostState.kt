package com.rcudev.posts.ui.posts

import com.rcudev.posts.domain.model.Post
import com.rcudev.posts.domain.model.PostType

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