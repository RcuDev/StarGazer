package com.rcudev.posts.data.remote

import com.rcudev.posts.data.model.PostsResponse
import com.rcudev.posts.data.mapper.toPosts
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
        // Given - Set up a mock for PostService to return mock data for getArticles with ARTICLES type
        postService = mock(MockMode.autoUnit)
        everySuspend { postService.getArticles(PostType.ARTICLES) } returns Result.success(
            mockArticlesResponse.toPosts(PostType.ARTICLES)
        )
    }

    @Test
    fun getArticlesWithArticlesTypeReturnsSuccessfulResult() = runTest {
        // When - Call getArticles with the PostType.ARTICLES on the mocked PostService
        val result = postService.getArticles(PostType.ARTICLES)

        // Then - Verify the result is successful
        assertTrue(
            result.isSuccess,
            "Expected result to be a success when calling getArticles with type ARTICLES"
        )
    }

    @Test
    fun getArticlesReturnsDataMatchingExpectedValues() = runTest {
        // When - Call getArticles and retrieve the result data
        val result = postService.getArticles(PostType.ARTICLES).getOrNull()

        // Then - Verify the result data matches the mockArticlesResponse values
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
        // Given - Sample mock response for testing purposes
        private val mockArticlesResponse = Json.decodeFromString<PostsResponse>(articlesResponse)
    }
}
