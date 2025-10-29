package com.rcudev.posts.domain.repository

import com.rcudev.posts.domain.model.Posts

interface PostRepository {
    suspend fun getPosts(): Result<List<Posts>>
}