package com.rcudev.posts.ui.posts

interface PostEffect {
    data class showError(val message: String) : PostEffect
    data class navigateToUrl(val url: String) : PostEffect
}