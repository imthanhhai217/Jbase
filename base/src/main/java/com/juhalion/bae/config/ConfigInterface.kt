package com.juhalion.bae.config

interface ConfigInterface {
    fun baseUrl(url: String): ConfigInterface
    fun setPrefName(prefName: String): ConfigInterface
    fun setPrefNameSecure(prefNameSecure: String): ConfigInterface
    fun isDebug(debugMode: Boolean): ConfigInterface
    fun environment(environment: String): ConfigInterface
    fun build(): CommonConfig
}