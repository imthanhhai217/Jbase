package com.juhalion.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ProcessLifecycleOwner
import com.juhalion.bae.config.BaseApplication
import com.juhalion.bae.config.CommonConfigManager
import com.juhalion.bae.config.ConfigBuilder
import com.juhalion.bae.events.SingleEvent
import com.juhalion.bae.lifecycle.AppLifecycleObserver
import com.juhalion.base.constants.Constant
import com.juhalion.base.constants.ConstantApi
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : BaseApplication() {
    @Inject
    lateinit var appLifecycleObserver: AppLifecycleObserver

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)
        initBaseConfig()
    }

    private fun initBaseConfig() {
        val isDebug = BuildConfig.DEBUG
//        val env = BuildConfig.FLAVOR
        val env = "Dev"

        val config = ConfigBuilder(this).apply {
            baseUrl(ConstantApi.BASE_URL)
            setPrefName(Constant.PREF_NAME)
            setPrefNameSecure(Constant.PREF_NAME_SECURE)
            isDebug(isDebug)
            environment(env)
        }.build()

        CommonConfigManager.setCommonConfig(config)
    }

    override fun getInstances(): BaseApplication {
        return this
    }

    override fun isAppRunning() = appLifecycleObserver.isAppRunning

    override fun isAppInForeground() = appLifecycleObserver.isAppInForeground

    // Gọi khi ứng dụng bị hủy hoặc đóng
    override fun onTerminate() {
        super.onTerminate()
        appLifecycleObserver.onAppTerminate()
    }

    override val notificationComing = MutableLiveData<SingleEvent<Any>>()
    override fun sendNotificationComing() {
        unAuthenticationEvent.postValue(SingleEvent(Any()))
    }

    override val unAuthenticationEvent = MutableLiveData<SingleEvent<Any>>()
    override fun onUnAuthentication() {
        notificationComing.postValue(SingleEvent(Any()))
    }
}