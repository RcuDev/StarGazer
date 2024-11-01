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
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PostServiceTest {

    private lateinit var postService: PostService

    @BeforeTest
    fun setUp() {
        // Given - Mock setup
        postService = mock(MockMode.autoUnit)
        everySuspend { postService.getArticles(PostType.ARTICLES) } returns Result.success(
            mockArticlesResponse.toPosts(PostType.ARTICLES)
        )
    }

    @Test
    fun `When getArticles is called with ARTICLES type Then it returns a successful result`() =
        runTest {
            // When
            val result = postService.getArticles(PostType.ARTICLES)

            // Then
            assertTrue(
                result.isSuccess,
                "Expected result to be a success when calling getArticles with type ARTICLES"
            )
        }

    @Test
    fun `When getArticles is called Then returned data matches expected article response values`() =
        runTest {
            // When
            val result = postService.getArticles(PostType.ARTICLES).getOrNull()

            // Then
            assertTrue(
                result != null,
                "Expected non-null result for getArticles call with type ARTICLES"
            )
            assertEquals(
                mockArticlesResponse.count,
                result.count,
                "Expected 'count' to match the mock data for the number of articles"
            )
            assertEquals(
                mockArticlesResponse.next,
                result.next,
                "Expected 'next' pagination URL to match the mock data for the articles list"
            )
            assertEquals(
                mockArticlesResponse.previous,
                result.previous,
                "Expected 'previous' pagination URL to match the mock data for the articles list"
            )
            assertEquals(
                mockArticlesResponse.results.size,
                result.results.size,
                "Expected 'results' size to match the number of items in mock data"
            )
        }

    companion object {
        // Given - Sample response for testing
        private val mockArticlesResponse = Json.decodeFromString<PostsResponse>(articlesResponse)
    }
}
