package com.rcudev.posts.data

import com.rcudev.posts.data.model.InfoResponse
import com.rcudev.posts.domain.model.Info

internal val InfoResponse.toInfo
    get() = Info(
        version = version,
        newsSites = newsSites
    )