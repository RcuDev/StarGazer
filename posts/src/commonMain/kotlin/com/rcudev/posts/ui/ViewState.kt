package com.rcudev.posts.ui

import androidx.compose.runtime.Stable
import com.rcudev.posts.domain.model.Post

@Stable
sealed interface ViewState {
    data object Loading : ViewState
    data object Error : ViewState
    
    @Stable
    data class Empty(val message: String) : ViewState
    
    @Stable
    data class Success(
        val posts: List<Post>,
        val loadingNextPage: Boolean = false,
        val loadingError: Boolean = false,
    ) : ViewState
}