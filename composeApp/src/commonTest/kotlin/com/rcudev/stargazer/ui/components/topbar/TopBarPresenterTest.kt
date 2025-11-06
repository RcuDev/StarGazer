package com.rcudev.stargazer.ui.components.topbar

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import com.rcudev.posts.domain.model.PostType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TopBarPresenterTest {

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var presenter: TopBarPresenter
    private lateinit var events: MutableSharedFlow<TopBarEvent>

    @BeforeTest
    fun setup() {
        dataStore = PreferenceDataStoreFactory.createWithPath(
            produceFile = { "test.preferences_pb".toPath() }
        )
        presenter = TopBarPresenter(dataStore)
        events = MutableSharedFlow(extraBufferCapacity = 10)
    }

    @AfterTest
    fun tearDown() = runTest {
        // Clean up DataStore
        dataStore.edit { it.clear() }
    }

    @Test
    fun emitsInitialStateWithDefaultPostType() = runTest {
        // Given - Presenter is initialized with default preferences
        val flow = moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }

        // When - Collecting the state
        flow.test {
            val state = awaitItem() as TopBarState.Content

            // Then - Should emit default ARTICLES post type
            assertEquals(PostType.ARTICLES.type, state.selectedPostType)
        }
    }

    @Test
    fun updatesStateWhenSelectPostTypeEventIsEmitted() = runTest {
        // Given - Presenter is running and collecting states
        val flow = moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }

        flow.test {
            // When - Initial state is received
            val initialState = awaitItem() as TopBarState.Content
            assertEquals(PostType.ARTICLES.type, initialState.selectedPostType)

            // When - SelectPostType event is emitted
            events.emit(TopBarEvent.SelectPostType(PostType.BLOGS.type))

            // Then - State should update to BLOGS
            val updatedState = awaitItem() as TopBarState.Content
            assertEquals(PostType.BLOGS.type, updatedState.selectedPostType)
        }
    }

    @Test
    fun persistsSelectedPostTypeAcrossMultipleEvents() = runTest {
        // Given - Presenter is running
        val flow = moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }

        flow.test {
            // When - Initial state
            awaitItem()

            // When - Selecting REPORTS
            events.emit(TopBarEvent.SelectPostType(PostType.REPORTS.type))
            val reportsState = awaitItem() as TopBarState.Content
            assertEquals(PostType.REPORTS.type, reportsState.selectedPostType)

            // When - Selecting BLOGS
            events.emit(TopBarEvent.SelectPostType(PostType.BLOGS.type))
            val blogsState = awaitItem() as TopBarState.Content

            // Then - Should reflect the latest selection
            assertEquals(PostType.BLOGS.type, blogsState.selectedPostType)
        }
    }
}
