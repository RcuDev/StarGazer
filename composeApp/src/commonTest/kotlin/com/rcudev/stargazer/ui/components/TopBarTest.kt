@file:OptIn(ExperimentalTestApi::class)

package com.rcudev.stargazer.ui.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import com.rcudev.posts.domain.model.PostType
import com.rcudev.storage.POST_TYPE_FILTER
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.test.KoinTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class TopBarTest : KoinTest {

    private lateinit var preferences: DataStore<Preferences>
    private lateinit var dataFlow: MutableStateFlow<Preferences>

    @BeforeTest
    fun setUp() {
        // Given - Setup mock for DataStore with initial preference for POST_TYPE_FILTER set to ARTICLES
        dataFlow = MutableStateFlow(
            mutablePreferencesOf(
                POST_TYPE_FILTER to PostType.ARTICLES.type
            )
        )
        preferences = mock<DataStore<Preferences>>(MockMode.autoUnit)
        every { preferences.data } returns dataFlow // Mock preferences data flow with initial values
    }

    @AfterTest
    fun tearDown() {
        // Reset preferences data flow after each test
        dataFlow.value = mutablePreferencesOf()
    }

    @Test
    fun `TopBar is displayed with correct initial state`() = runComposeUiTest {
        // Given - TopBar is displayed without the back button and with PostTypeChips
        setContent {
            TopBar(
                preferences = preferences,
                showBackButton = false
            )
        }

        // Then - Verify correct initial UI elements are displayed
        onNodeWithTag("Back").assertDoesNotExist() // Back button should not be present
        onNodeWithTag("PostTypeChips").assertIsDisplayed() // PostTypeChips should be visible
        onNodeWithTag(PostType.ARTICLES.type).assertIsDisplayed()
        onNodeWithTag(PostType.BLOGS.type).assertIsDisplayed()
        onNodeWithTag(PostType.REPORTS.type).assertIsDisplayed()
    }

    @Test
    fun `PostTypeChips is displayed with correct initial state`() = runComposeUiTest {
        // Given - Initial selection is set to ARTICLES in the preferences
        setContent {
            TopBar(
                preferences = preferences,
                showBackButton = false
            )
        }

        // Then - Verify that the ARTICLES chip is selected and others are not
        onNodeWithTag(PostType.ARTICLES.type).assertIsSelected()
        onNodeWithTag(PostType.BLOGS.type).assertIsNotSelected()
        onNodeWithTag(PostType.REPORTS.type).assertIsNotSelected()
    }

    @Test
    fun `PostTypeChips preference is updated when selecting BLOGS Then BLOGS is selected`() =
        runComposeUiTest {
            // When - Update preference to select BLOGS type
            dataFlow.value = mutablePreferencesOf(POST_TYPE_FILTER to PostType.BLOGS.type)

            setContent {
                TopBar(
                    preferences = preferences,
                    showBackButton = false
                )
            }

            // Then - Verify that BLOGS chip is selected and others are not
            onNodeWithTag(PostType.ARTICLES.type).assertIsNotSelected()
            onNodeWithTag(PostType.BLOGS.type).assertIsSelected()
            onNodeWithTag(PostType.REPORTS.type).assertIsNotSelected()
        }

    @Test
    fun `PostTypeChips preference is updated when selecting REPORTS Then REPORTS is selected`() =
        runComposeUiTest {
            // When - Update preference to select REPORTS type
            dataFlow.value = mutablePreferencesOf(POST_TYPE_FILTER to PostType.REPORTS.type)

            setContent {
                TopBar(
                    preferences = preferences,
                    showBackButton = false
                )
            }

            // Then - Verify that REPORTS chip is selected and others are not
            onNodeWithTag(PostType.ARTICLES.type).assertIsNotSelected()
            onNodeWithTag(PostType.BLOGS.type).assertIsNotSelected()
            onNodeWithTag(PostType.REPORTS.type).assertIsSelected()
        }

    @Test
    fun `TopBar is displayed with correct WebView state`() = runComposeUiTest {
        // Given - TopBar is displayed with the back button and without PostTypeChips
        setContent {
            TopBar(
                preferences = preferences,
                showBackButton = true
            )
        }

        // Then - Verify back button is displayed and PostTypeChips is not
        onNodeWithTag("Back").assertIsDisplayed()
        onNodeWithTag("PostTypeChips").assertDoesNotExist()
    }
}
