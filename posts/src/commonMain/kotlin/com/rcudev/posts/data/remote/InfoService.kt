package com.rcudev.posts.data.remote

import com.rcudev.network.KtorApi
import com.rcudev.posts.data.model.InfoResponse
import com.rcudev.posts.data.toInfo
import io.ktor.client.call.body
import io.ktor.client.request.get

class InfoService : KtorApi() {

    suspend fun getInfo() = runCatching {
        client.get {
            pathUrl("info")
        }.body<InfoResponse>().toInfo
    }

}