package com.rcudev.posts.ui.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import com.rcudev.posts.domain.InfoService
import com.rcudev.posts.domain.model.Info
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SettingsPresenterTest {

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var infoService: InfoService
    private lateinit var presenter: SettingsPresenter
    private lateinit var events: MutableSharedFlow<SettingsEvent>

    @BeforeTest
    fun setup() {
        dataStore = PreferenceDataStoreFactory.createWithPath(
            produceFile = { "test_settings.preferences_pb".toPath() }
        )
        infoService = mock<InfoService>()
        presenter = SettingsPresenter(dataStore, infoService)
        events = MutableSharedFlow(extraBufferCapacity = 10)
    }

    @AfterTest
    fun tearDown() = runTest {
        // Clean up DataStore
        dataStore.edit { it.clear() }
    }

    @Test
    fun emitsInitialStateWithDefaultValues() = runTest {
        // Given - InfoService returns a list of news sites
        val mockNewsSites = listOf("TechCrunch", "The Verge", "Ars Technica")
        val mockInfo = Info(version = "", newsSites = mockNewsSites)
        every { suspend { infoService.getInfo() } } returns { Result.success(mockInfo) }

        val flow = moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }

        // When - Collecting the state
        flow.test {
            val state = awaitItem() as SettingsState.Content

            // Then - Should emit default values
            assertEquals("", state.newsSitesSelected)
            assertFalse(state.isDarkMode)
            assertEquals(mockNewsSites, state.newsSites)
        }
    }

    @Test
    fun updatesStateWhenToggleDarkModeEventIsEmitted() = runTest {
        // Given - Presenter is running
        val mockInfo = Info(version = "", newsSites = listOf("Site1"))
        every { suspend { infoService.getInfo() } } returns { Result.success(mockInfo) }

        val flow = moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }

        flow.test {
            // When - Initial state with dark mode disabled
            val initialState = awaitItem() as SettingsState.Content
            assertFalse(initialState.isDarkMode)

            // When - ToggleDarkMode event is emitted to enable dark mode
            events.emit(SettingsEvent.ToggleDarkMode(isEnabled = true))

            // Then - State should update with dark mode enabled
            val updatedState = awaitItem() as SettingsState.Content
            assertTrue(updatedState.isDarkMode)
        }
    }

    @Test
    fun updatesStateWhenSelectNewsSiteEventIsEmitted() = runTest {
        // Given - Presenter is running with available news sites
        val mockNewsSites = listOf("TechCrunch", "The Verge", "Ars Technica")
        val mockInfo = Info(version = "", newsSites = mockNewsSites)
        every { suspend { infoService.getInfo() } } returns { Result.success(mockInfo) }

        val flow = moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }

        flow.test {
            // When - Initial state with no site selected
            val initialState = awaitItem() as SettingsState.Content
            assertEquals("", initialState.newsSitesSelected)

            // When - SelectNewsSite event is emitted
            events.emit(SettingsEvent.SelectNewsSite("TechCrunch"))

            // Then - State should update with selected news site
            val updatedState = awaitItem() as SettingsState.Content
            assertEquals("TechCrunch", updatedState.newsSitesSelected)
        }
    }
}
