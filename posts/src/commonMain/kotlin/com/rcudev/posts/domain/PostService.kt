package com.rcudev.posts.domain

import com.rcudev.posts.domain.model.PostType
import com.rcudev.posts.domain.model.Posts

interface PostService {
    suspend fun getArticles(
        postType: PostType,
        offset: Int = 0,
        newsSites: String = ""
    ): Result<Posts>
}