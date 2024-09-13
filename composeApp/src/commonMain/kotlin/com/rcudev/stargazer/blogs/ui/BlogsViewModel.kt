package com.rcudev.stargazer.blogs.ui

import com.rcudev.stargazer.common.data.remote.PostService
import com.rcudev.stargazer.common.domain.model.PostType
import com.rcudev.stargazer.common.ui.posts.BaseViewModel

internal class BlogsViewModel(
    postService: PostService
) : BaseViewModel(postService, PostType.BLOGS)