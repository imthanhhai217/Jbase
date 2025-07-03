package com.juhalion.bae.config

class ConfigBuilder(private val baseApps: BaseApplication): ConfigInterface {
    private var baseUrl = ""
    private var prefName = ""
    private var prefNameSecure = ""
    private var debug = false
    private var env = ""

    override fun baseUrl(url: String): ConfigInterface {
        baseUrl = url
        return this
    }

    override fun setPrefName(prefName: String): ConfigInterface {
        this.prefName = prefName
        return this
    }

    override fun setPrefNameSecure(prefNameSecure: String): ConfigInterface {
        this.prefNameSecure = prefNameSecure
        return this
    }

    override fun isDebug(debugMode: Boolean): ConfigInterface {
        debug = debugMode
        return this
    }

    override fun environment(environment: String): ConfigInterface {
        env = environment
        return this
    }

    override fun build(): CommonConfig {
        return CommonConfig(
            baseUrl = baseUrl,
            prefName = prefName,
            prefNameSecure = prefNameSecure,
            isDebug = debug, env = env, baseApps = baseApps)
    }
}