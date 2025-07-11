package com.rcudev.posts.domain.model

import androidx.compose.runtime.Stable

@Stable
enum class PostType(val type: String) {
    ARTICLES("Articles"),
    BLOGS("Blogs"),
    REPORTS("Reports");
    
    companion object {
        // Cached map for better performance
        private val typeMap = entries.associateBy { it.type }
        
        fun fromString(type: String?): PostType? = typeMap[type]
    }
}