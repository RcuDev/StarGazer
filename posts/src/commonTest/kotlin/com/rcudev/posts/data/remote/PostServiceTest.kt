package com.rcudev.posts.data.remote

import com.rcudev.posts.data.model.PostsResponse
import com.rcudev.posts.data.toPosts
import com.rcudev.posts.domain.PostService
import com.rcudev.posts.domain.model.PostType
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import responses.articlesResponse
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class PostServiceTest {

    private val postService = mock<PostService>(MockMode.autoUnit)

    @BeforeTest
    fun setUp() = runTest {
        everySuspend { postService.getArticles(PostType.ARTICLES) } returns Result.success(
            postsResponse
        )
    }

    @Test
    fun `When do Articles call then get result is Success`() = runTest {
        val result = postService.getArticles(PostType.ARTICLES)

        assertTrue { result.isSuccess }
    }

    companion object {
        private val postsResponse = Json.decodeFromString<PostsResponse>(articlesResponse)
            .toPosts(PostType.ARTICLES)
    }
}