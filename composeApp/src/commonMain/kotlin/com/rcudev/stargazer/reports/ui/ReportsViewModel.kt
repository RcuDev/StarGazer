package com.rcudev.stargazer.reports.ui

import com.rcudev.stargazer.common.data.remote.PostService
import com.rcudev.stargazer.common.domain.model.PostType
import com.rcudev.stargazer.common.ui.posts.BaseViewModel

internal class ReportsViewModel(
    postService: PostService
) : BaseViewModel(postService, PostType.REPORTS)