package com.juhalion.bae.config


data class CommonConfig(
    var baseApps: BaseApplication,
    var baseUrl: String = "",
    var prefName: String? = null,
    var prefNameSecure: String? = null,
    var isDebug: Boolean = false,
    var env: String? = null
)