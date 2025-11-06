@file:OptIn(ExperimentalTestApi::class)

package com.rcudev.posts.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import com.rcudev.posts.ui.settings.DarkModeItem
import org.koin.test.KoinTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class DarkModeItemTest : KoinTest {

    @Test
    fun displaysSwitchWithCorrectInitialState() = runComposeUiTest {
        // Given - Set up content with isDarkMode = false
        setContent {
            DarkModeItem(
                isDarkMode = false,
                onToggleDarkMode = {}
            )
        }

        // Then - Check that "Dark mode" text and switch exist, and that switch is initially off
        onNodeWithText("Dark mode").assertIsDisplayed()
        onNodeWithTag("DarkModeSwitch").assertIsDisplayed()
        onNodeWithTag("DarkModeSwitch").assertIsOff()
    }

    @Test
    fun switchIsCheckedWhenDarkModeIsEnabled() = runComposeUiTest {
        // Given - Set up content with isDarkMode = true
        setContent {
            DarkModeItem(
                isDarkMode = true,
                onToggleDarkMode = {}
            )
        }

        // Then - Verify that the switch is on when DARK_MODE is true
        onNodeWithTag("DarkModeSwitch").assertIsOn()
    }
}
