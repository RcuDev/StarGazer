package com.rcudev.posts.ui.posts

interface PostEffect {
    data class ShowError(val message: String) : PostEffect
    data class NavigateToUrl(val url: String) : PostEffect
}