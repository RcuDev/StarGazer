package com.rcudev.posts.domain.repository

import com.rcudev.posts.domain.model.Info

interface InfoRepository {
    suspend fun getInfo(): Result<Info>
}