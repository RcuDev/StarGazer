package com.rcudev.posts.data.remote

import com.rcudev.posts.data.model.InfoResponse
import com.rcudev.posts.data.toInfo
import com.rcudev.posts.domain.InfoService
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
        // Given - Mock setup
        infoService = mock<InfoService>()
        everySuspend { infoService.getInfo() } returns Result.success(mockInfoResponse.toInfo)
    }

    @Test
    fun `When getInfo is called Then it returns a successful result`() = runTest {
        // When
        val result = infoService.getInfo()

        // Then
        assertTrue(result.isSuccess, "Expected result to be a success")
    }

    @Test
    fun `When getInfo is called Then returned data matches expected infoResponse values`() =
        runTest {
            // When
            val result = infoService.getInfo().getOrNull()

            // Then
            assertTrue(result != null, "Expected non-null result")
            assertEquals(
                mockInfoResponse.version,
                result.version,
                "Expected version to match mock data"
            )
            assertEquals(
                mockInfoResponse.newsSites,
                result.newsSites,
                "Expected newsSites to match mock data"
            )
        }

    companion object {
        // Given - Sample response for testing
        private val mockInfoResponse = Json.decodeFromString<InfoResponse>(infoResponse)
    }
}
