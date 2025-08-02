package com.juhalion.bae.config

class CommonConfigManager {
    companion object {
        @Volatile
        private lateinit var _instance: CommonConfig

        @Synchronized
        fun setCommonConfig(newConfig: CommonConfig) {
            _instance = newConfig
        }

        @Synchronized
        fun getInstance() = _instance
    }
}