package com.rcudev.posts.domain

import com.rcudev.posts.domain.model.Info

interface InfoService {
    suspend fun getInfo(): Result<Info>
}