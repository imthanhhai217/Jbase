package com.juhalion.bae.config

import com.juhalion.bae.base.BaseApplication
data class CommonConfig(
    var baseApps: BaseApplication,
    var baseUrl: String = "",
    var prefName: String? = null,
    var prefNameSecure: String? = null,
    var isDebug: Boolean = false,
    var env: String? = null
)