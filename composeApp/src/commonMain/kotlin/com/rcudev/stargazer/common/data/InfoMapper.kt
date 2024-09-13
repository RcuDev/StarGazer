package com.rcudev.stargazer.common.data

import com.rcudev.stargazer.common.data.model.InfoResponse
import com.rcudev.stargazer.common.domain.model.Info

internal val InfoResponse.toInfo
    get() = Info(
        version = version,
        newsSites = newsSites
    )