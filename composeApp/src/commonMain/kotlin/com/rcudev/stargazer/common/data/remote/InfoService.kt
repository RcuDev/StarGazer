package com.rcudev.stargazer.common.data.remote

import com.rcudev.network.KtorApi
import com.rcudev.stargazer.common.data.model.InfoResponse
import com.rcudev.stargazer.common.data.toInfo
import io.ktor.client.call.body
import io.ktor.client.request.get

internal class InfoService : KtorApi() {

    suspend fun getInfo() = runCatching {
        client.get {
            pathUrl("info")
        }.body<InfoResponse>().toInfo
    }

}