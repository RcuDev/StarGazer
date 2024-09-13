package com.rcudev.stargazer.common.di

import com.rcudev.stargazer.common.domain.model.Post

internal sealed class ViewState {
    data object Loading : ViewState()
    data object Error : ViewState()
    data class Empty(val message: String) : ViewState()
    data class Success(
        val posts: List<Post>,
        val loadingNextPage: Boolean = false,
        val loadingError: Boolean = false,
    ) : ViewState()
}