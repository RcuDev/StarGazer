package com.rcudev.posts.data.remote

import com.rcudev.network.KtorApi
import com.rcudev.posts.data.model.PostsResponse
import com.rcudev.posts.data.mapper.toPosts
import com.rcudev.posts.domain.PostService
import com.rcudev.posts.domain.model.PostType
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class PostServiceImpl : PostService, KtorApi() {

    override suspend fun getArticles(postType: PostType, offset: Int, newsSites: String) =
        runCatching {
            client.get {
                pathUrl(postType.type.lowercase())
                parameter("offset", offset)
                if (offset != 0) {
                    parameter("limit", 10)
                }
                if (newsSites.isNotEmpty()) {
                    parameter("news_site", newsSites)
                }
            }.body<PostsResponse>().toPosts(postType)
        }

}