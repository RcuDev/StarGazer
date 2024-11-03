package com.rcudev.posts.data.remote

import com.rcudev.posts.data.model.InfoResponse
import com.rcudev.posts.data.toInfo
import com.rcudev.posts.domain.InfoService
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import responses.infoResponse
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InfoServiceTest {

    private lateinit var infoService: InfoService

    @BeforeTest
    fun setUp() {
        // Given - Set up a mock of InfoService to return mock data for getInfo()
        infoService = mock<InfoService>(MockMode.autoUnit)
        everySuspend { infoService.getInfo() } returns Result.success(mockInfoResponse.toInfo)
    }

    @Test
    fun `When getInfo is called Then it returns a successful result`() = runTest {
        // When - Call getInfo() on the mocked InfoService
        val result = infoService.getInfo()

        // Then - Verify the result is successful
        assertTrue(result.isSuccess, "Expected getInfo() result to be a success")
    }

    @Test
    fun `When getInfo is called Then returned data matches expected infoResponse values`() =
        runTest {
            // When - Call getInfo() and retrieve the result data
            val result = infoService.getInfo().getOrNull()

            // Then - Verify the result data matches the mockInfoResponse values
            assertTrue(result != null, "Expected non-null result from getInfo()")
            assertEquals(
                mockInfoResponse.version,
                result.version,
                "Expected version in result to match mock version"
            )
            assertEquals(
                mockInfoResponse.newsSites,
                result.newsSites,
                "Expected newsSites in result to match mock newsSites list"
            )
        }

    companion object {
        // Given - Sample mock response for testing purposes
        private val mockInfoResponse = Json.decodeFromString<InfoResponse>(infoResponse)
    }
}
