package com.juhalion.base

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ProcessLifecycleOwner
import com.juhalion.bae.base.BaseApplication
import com.juhalion.bae.config.CommonConfigManager
import com.juhalion.bae.config.ConfigBuilder
import com.juhalion.bae.events.SingleEvent
import com.juhalion.bae.lifecycle.AppLifecycleObserver
import com.juhalion.base.constants.Constant
import com.juhalion.base.constants.ConstantApi
import com.juhalion.base.ui.fragments.settings.SettingsFragment
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : BaseApplication() {
    @Inject lateinit var appLifecycleObserver: AppLifecycleObserver

    override fun onCreate() {
        super.onCreate()
        instances = this
        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)
        initBaseConfig()
        applyDayNightMode()
    }

    private fun applyDayNightMode() {
        val sharedPreferences = getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean(SettingsFragment.KEY_DARK_MODE, false)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
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

    override fun isAppRunning() = appLifecycleObserver.isAppRunning

    override fun isAppInForeground() = appLifecycleObserver.isAppInForeground

    // Gọi khi ứng dụng bị hủy hoặc đóng
    override fun onTerminate() {
        super.onTerminate()
        appLifecycleObserver.onAppTerminate()
    }

    override val notificationComing = MutableLiveData<SingleEvent<Any>>()
    override fun sendNotificationComing() {
        notificationComing.postValue(SingleEvent(Any()))
    }

    override val unAuthenticationEvent = MutableLiveData<SingleEvent<Any>>()
    override fun onUnAuthentication() {
        unAuthenticationEvent.postValue(SingleEvent(Any()))
    }
}