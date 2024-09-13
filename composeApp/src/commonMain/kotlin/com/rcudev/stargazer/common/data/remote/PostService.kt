package com.rcudev.stargazer.common.data.remote

import com.rcudev.network.KtorApi
import com.rcudev.stargazer.common.data.model.PostsResponse
import com.rcudev.stargazer.common.data.toPosts
import com.rcudev.stargazer.common.domain.model.PostType
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class PostService : KtorApi() {

    suspend fun getArticles(postType: PostType, offset: Int, newsSites: String) = runCatching {
        client.get {
            pathUrl(postType.type)
            parameter("offset", offset)
            if (offset != 0) {
                parameter("limit", 10)
            }
            if (newsSites.isNotEmpty()) {
                parameter("news_site", newsSites)
            }
        }.body<PostsResponse>().toPosts
    }

}