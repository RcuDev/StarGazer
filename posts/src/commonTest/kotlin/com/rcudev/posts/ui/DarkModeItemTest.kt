@file:OptIn(ExperimentalTestApi::class)

package com.rcudev.posts.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import com.rcudev.posts.ui.settings.DarkModeItem
import com.rcudev.storage.DARK_MODE
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
class DarkModeItemTest : KoinTest {

    private lateinit var preferences: DataStore<Preferences>
    private lateinit var dataFlow: MutableStateFlow<Preferences>

    @BeforeTest
    fun setUp() {
        // Given - Setup mock for DataStore and initial preferences flow
        dataFlow = MutableStateFlow(
            mutablePreferencesOf(
                DARK_MODE to false
            )
        )
        preferences = mock<DataStore<Preferences>>(MockMode.autoUnit)
        every { preferences.data } returns dataFlow
    }

    @AfterTest
    fun tearDown() {
        // Reset preferences data flow after each test
        dataFlow.value = mutablePreferencesOf()
    }

    @Test
    fun displaysSwitchWithCorrectInitialState() = runComposeUiTest {
        // Given - Set up content with mocked preferences where DARK_MODE is initially false
        setContent {
            KoinTestFrame {
                DarkModeItem()
            }
        }

        // Then - Check that "Dark mode" text and switch exist, and that switch is initially off
        onNodeWithText("Dark mode").assertIsDisplayed()
        onNodeWithTag("DarkModeSwitch").assertIsDisplayed()
        onNodeWithTag("DarkModeSwitch").assertIsOff()
    }

    @Test
    fun switchIsCheckedWhenDarkModeIsEnabled() = runComposeUiTest {
        // When - Set DARK_MODE to true in the mock preferences flow
        dataFlow.value = mutablePreferencesOf(DARK_MODE to true)

        setContent {
            DarkModeItem(preferences = preferences)
        }

        // Then - Verify that the switch is on when DARK_MODE is true
        onNodeWithTag("DarkModeSwitch").assertIsOn()
    }
}
