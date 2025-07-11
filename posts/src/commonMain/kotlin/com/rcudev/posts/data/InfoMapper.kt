package com.rcudev.posts.data

import com.rcudev.posts.data.model.InfoResponse
import com.rcudev.posts.domain.model.Info

internal fun InfoResponse.toInfo(): Info = Info(
    version = version,
    newsSites = newsSites
)