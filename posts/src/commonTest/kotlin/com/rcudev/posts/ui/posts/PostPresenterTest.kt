package com.rcudev.posts.ui.posts

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import com.rcudev.posts.domain.PostService
import com.rcudev.posts.domain.model.Post
import com.rcudev.posts.domain.model.Posts
import com.rcudev.posts.domain.model.PostType
import com.rcudev.storage.POST_TYPE_FILTER
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class PostPresenterTest {

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var postService: PostService
    private lateinit var presenter: PostPresenter
    private lateinit var events: MutableSharedFlow<PostEvent>

    companion object {
        // Mock data used across tests
        val mockPosts = listOf(
            Post(
                id = 1,
                postType = PostType.ARTICLES,
                title = "Test Post 1",
                url = "https://example.com/1",
                imageUrl = "https://example.com/image1.jpg",
                newsSite = "TechCrunch",
                summary = "Summary 1",
                publishedAt = "2024-01-01T00:00:00Z",
                updatedAt = "2024-01-01T00:00:00Z",
                featured = false
            ),
            Post(
                id = 2,
                postType = PostType.ARTICLES,
                title = "Test Post 2",
                url = "https://example.com/2",
                imageUrl = "https://example.com/image2.jpg",
                newsSite = "The Verge",
                summary = "Summary 2",
                publishedAt = "2024-01-02T00:00:00Z",
                updatedAt = "2024-01-02T00:00:00Z",
                featured = false
            )
        )
    }

    @BeforeTest
    fun setup() {
        dataStore = PreferenceDataStoreFactory.createWithPath(
            produceFile = { "test_posts.preferences_pb".toPath() }
        )
        postService = mock<PostService>()
        presenter = PostPresenter(dataStore, postService)
        events = MutableSharedFlow(extraBufferCapacity = 10)
    }

    @AfterTest
    fun tearDown() = runTest {
        // Clean up DataStore
        dataStore.edit { it.clear() }
    }

    @Test
    fun emitsLoadingStateInitiallyAndThenContentWithPosts() = runTest {
        // Given - PostService returns a list of posts
        val mockResponse = Posts(
            count = 2,
            results = mockPosts
        )
        every {
            suspend {
                postService.getArticles(
                    postType = PostType.ARTICLES,
                    offset = 0,
                    newsSites = ""
                )
            }
        } returns { Result.success(mockResponse) }

        // Setup initial preferences with ARTICLES
        dataStore.edit { prefs ->
            prefs[POST_TYPE_FILTER] = PostType.ARTICLES.type
        }

        val flow = moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }

        // When - Collecting the state
        flow.test {
            // Then - First emission should be Loading
            val loadingState = awaitItem().first
            assertIs<PostState.Loading>(loadingState)

            // Then - Second emission should be Content with posts
            val contentState = awaitItem().first as PostState.Content
            assertEquals(2, contentState.posts.size)
            assertEquals("Test Post 1", contentState.posts[0].title)
            assertEquals("Test Post 2", contentState.posts[1].title)
        }
    }

    @Test
    fun emitsErrorStateWhenPostServiceFails() = runTest {
        // Given - PostService returns an error
        every {
            suspend {
                postService.getArticles(
                    postType = PostType.ARTICLES,
                    offset = 0,
                    newsSites = ""
                )
            }
        } returns { Result.failure(Exception("Network error")) }

        // Setup initial preferences with ARTICLES
        dataStore.edit { prefs ->
            prefs[POST_TYPE_FILTER] = PostType.ARTICLES.type
        }

        val flow = moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }

        // When - Collecting the state
        flow.test {
            // Then - Skip Loading state
            awaitItem()

            // Then - Should emit Error state
            val errorState = awaitItem().first
            assertIs<PostState.Error>(errorState)
        }
    }

    @Test
    fun emitsEffectWhenPostClickEventIsReceived() = runTest {
        // Given - PostService returns posts
        val mockResponse = Posts(
            count = 1,
            results = mockPosts.take(1)
        )
        every {
            suspend {
                postService.getArticles(
                    postType = PostType.ARTICLES,
                    offset = 0,
                    newsSites = ""
                )
            }
        } returns { Result.success(mockResponse) }

        // Setup initial preferences
        dataStore.edit { prefs ->
            prefs[POST_TYPE_FILTER] = PostType.ARTICLES.type
        }

        val flow = moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }

        // When - Collecting the state and effects
        flow.test {
            // Skip Loading and Content states
            awaitItem()
            awaitItem()

            // When - OnPostClick event is emitted
            events.emit(PostEvent.OnPostClick("https://example.com/1"))

            // Then - Should receive NavigateToWebView effect
            val (_, effects) = awaitItem()
            effects.test {
                val effect = awaitItem()
                assertIs<PostEffect.NavigateToWebView>(effect)
                assertEquals("https://example.com/1", effect.url)
            }
        }
    }
}
