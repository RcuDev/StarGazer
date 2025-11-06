package com.rcudev.stargazer.ui.components.topbar

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import com.rcudev.posts.domain.model.PostType
import com.rcudev.stargazer.ui.components.KoinTestFrame
import org.koin.test.KoinTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class TopBarTest : KoinTest {

    @Test
    fun displaysTopBarWithCorrectInitialState() = runComposeUiTest {
        // Given - TopBar is displayed without the back button and with PostTypeChips
        setContent {
            KoinTestFrame {
                TopBar(
                    showBackButton = false
                )
            }
        }

        // Then - Verify correct initial UI elements are displayed
        onNodeWithTag("Back").assertDoesNotExist() // Back button should not be present
        onNodeWithTag("PostTypeChips").assertIsDisplayed()
        onNodeWithTag(PostType.ARTICLES.type).assertIsDisplayed()
        onNodeWithTag(PostType.BLOGS.type).assertIsDisplayed()
        onNodeWithTag(PostType.REPORTS.type).assertIsDisplayed()
    }

    @Test
    fun displaysPostTypeChipsWithCorrectInitialState() = runComposeUiTest {
        // Given - TopBar will use DataStore from Koin with default ARTICLES selection
        setContent {
            KoinTestFrame {
                TopBar(
                    showBackButton = false
                )
            }
        }

        waitForIdle()

        // Then - Verify that the ARTICLES chip is selected by default and others are not
        onNodeWithTag(PostType.ARTICLES.type).assertIsSelected()
        onNodeWithTag(PostType.BLOGS.type).assertIsNotSelected()
        onNodeWithTag(PostType.REPORTS.type).assertIsNotSelected()
    }

    @Test
    fun displaysTopBarWithBackButtonAndHidesPostTypeChipsWhenWebViewIsEnabled() = runComposeUiTest {
        // Given - TopBar is displayed with the back button and without PostTypeChips
        setContent {
            KoinTestFrame {
                TopBar(
                    showBackButton = true
                )
            }
        }

        // Then - Verify back button is displayed and PostTypeChips is not
        onNodeWithTag("Back").assertIsDisplayed()
        onNodeWithTag("PostTypeChips").assertDoesNotExist()
    }
}