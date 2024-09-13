package com.rcudev.stargazer.articles.ui

import com.rcudev.stargazer.common.data.remote.PostService
import com.rcudev.stargazer.common.domain.model.PostType
import com.rcudev.stargazer.common.ui.posts.BaseViewModel

internal class ArticlesViewModel(
    postService: PostService
) : BaseViewModel(postService, PostType.ARTICLES)